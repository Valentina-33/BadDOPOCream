package domain.game;

import domain.entities.Enemy;
import domain.entities.Fruit;
import domain.entities.Player;
import domain.model.Board;
import domain.model.Position;
import domain.utils.Direction;

import java.util.*;

/**
 * Decide el próximo movimiento de un jugador controlado por máquina.
 * Mejoras:
 *  - Memoria corta por jugador para evitar bucles (ping-pong).
 *  - Detecta "stuck" y usa BFS solo como rescate (no siempre).
 *  - Fearful penaliza callejones para no encerrarse.
 */
public class AIController {

    private final AIProfile profile;
    private final Random rng = new Random();

    // Memoria por jugador para evitar loops (A-B-A-B...)
    private final Map<Player, ArrayDeque<Position>> lastPositions = new HashMap<>();
    private static final int MEMORY = 6;

    public AIController(AIProfile profile) {
        this.profile = profile;
    }

    public AIProfile getProfile() {
        return profile;
    }

    public Direction decide(Level level, Player me) {
        if (me == null || me.isDead()) return Direction.NONE;

        remember(me);

        return switch (profile) {
            case HUNGRY -> hungryMove(level, me);
            case FEARFUL -> fearfulMove(level, me);
            case EXPERT -> expertMove(level, me);
        };
    }

    // =========================
    // Profiles
    // =========================

    private Direction hungryMove(Level level, Player me) {
        Board board = level.getBoard();

        Fruit target = nearestActiveFruit(level, me.getPosition());
        if (target == null) {
            return randomWalk(board, me.getPosition(), me);
        }

        // Si está atascado, BFS de rescate
        if (isStuck(me)) {
            Direction bfs = bfsFirstStep(board, me.getPosition(), target.getPosition());
            if (bfs != Direction.NONE) return bfs;
        }

        return stepToward(board, me.getPosition(), target.getPosition(), me);
    }

    private Direction fearfulMove(Level level, Player me) {
        Board board = level.getBoard();

        Enemy threat = nearestEnemy(level, me.getPosition());
        if (threat == null) {
            Fruit target = nearestActiveFruit(level, me.getPosition());
            if (target != null) {
                if (isStuck(me)) {
                    Direction bfs = bfsFirstStep(board, me.getPosition(), target.getPosition());
                    if (bfs != Direction.NONE) return bfs;
                }
                return stepToward(board, me.getPosition(), target.getPosition(), me);
            }
            return randomWalk(board, me.getPosition(), me);
        }

        // Alejarse del enemigo, evitando loops y callejones
        Direction away = stepAway(board, me.getPosition(), threat.getPosition(), me);
        if (away != Direction.NONE) return away;

        return randomWalk(board, me.getPosition(), me);
    }

    private Direction expertMove(Level level, Player me) {
        Board board = level.getBoard();
        Position mePos = me.getPosition();

        // 1) si hay enemigo cerca, primero aléjate
        Enemy threat = nearestEnemy(level, mePos);
        if (threat != null) {
            int d = manhattan(mePos, threat.getPosition());
            if (d <= 3) {
                Direction away = stepAway(board, mePos, threat.getPosition(), me);
                if (away != Direction.NONE) return away;
            }
        }

        // 2) si no hay amenaza inmediata, ve por fruta
        Fruit target = nearestActiveFruit(level, mePos);
        if (target != null) {
            if (isStuck(me)) {
                Direction bfs = bfsFirstStep(board, mePos, target.getPosition());
                if (bfs != Direction.NONE) return bfs;
            }
            Direction toward = stepToward(board, mePos, target.getPosition(), me);
            if (toward != Direction.NONE) return toward;
        }

        // 3) si no hay nada, camina random
        return randomWalk(board, mePos, me);
    }

    // =========================
    // Target selection
    // =========================

    private Fruit nearestActiveFruit(Level level, Position from) {
        List<Fruit> fruits = level.getFruitManager().getActiveFruits();
        Fruit best = null;
        int bestDist = Integer.MAX_VALUE;

        for (Fruit f : fruits) {
            if (f.isCollected()) continue;
            if (f.isFrozen()) continue;

            int d = manhattan(from, f.getPosition());
            if (d < bestDist) {
                bestDist = d;
                best = f;
            }
        }
        return best;
    }

    private Enemy nearestEnemy(Level level, Position from) {
        List<Enemy> enemies = level.getEnemies();
        Enemy best = null;
        int bestDist = Integer.MAX_VALUE;

        for (Enemy e : enemies) {
            int d = manhattan(from, e.getPosition());
            if (d < bestDist) {
                bestDist = d;
                best = e;
            }
        }
        return best;
    }

    // =========================
    // Movement helpers
    // =========================

    private int manhattan(Position a, Position b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }

    /**
     * Greedy hacia goal, pero penaliza volver a posiciones recientes.
     */
    private Direction stepToward(Board board, Position from, Position goal, Player me) {
        int bestScore = Integer.MAX_VALUE;
        Direction bestDir = Direction.NONE;

        for (Direction d : new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT}) {
            Position next = from.translated(d.getDRow(), d.getDCol());
            if (!board.isInside(next)) continue;
            if (!board.isWalkable(next)) continue;

            int dist = manhattan(next, goal);
            int penaltyLoop = wasRecentlyThere(me, next) ? 3 : 0;
            int score = dist + penaltyLoop;

            if (score < bestScore) {
                bestScore = score;
                bestDir = d;
            }
        }
        return bestDir;
    }

    /**
     * Alejarse del threat, penalizando loops y callejones.
     */
    private Direction stepAway(Board board, Position from, Position threat, Player me) {
        int bestScore = Integer.MIN_VALUE;
        Direction bestDir = Direction.NONE;

        for (Direction d : new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT}) {
            Position next = from.translated(d.getDRow(), d.getDCol());
            if (!board.isInside(next)) continue;
            if (!board.isWalkable(next)) continue;

            int dist = manhattan(next, threat);
            int penaltyLoop = wasRecentlyThere(me, next) ? 2 : 0;
            int penaltyDeadEnd = (freeNeighbors(board, next) <= 1) ? 2 : 0;

            int score = dist - penaltyLoop - penaltyDeadEnd;
            if (score > bestScore) {
                bestScore = score;
                bestDir = d;
            }
        }
        return bestDir;
    }

    private int freeNeighbors(Board board, Position p) {
        int count = 0;
        for (Direction d : new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT}) {
            Position n = p.translated(d.getDRow(), d.getDCol());
            if (board.isInside(n) && board.isWalkable(n)) count++;
        }
        return count;
    }

    /**
     * Random, pero evita (cuando puede) posiciones recientes.
     */
    private Direction randomWalk(Board board, Position from, Player me) {
        List<Direction> options = new ArrayList<>();
        List<Direction> safeOptions = new ArrayList<>();

        for (Direction d : new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT}) {
            Position next = from.translated(d.getDRow(), d.getDCol());
            if (!board.isInside(next)) continue;
            if (!board.isWalkable(next)) continue;

            options.add(d);
            if (!wasRecentlyThere(me, next)) safeOptions.add(d);
        }

        if (!safeOptions.isEmpty()) {
            return safeOptions.get(rng.nextInt(safeOptions.size()));
        }
        if (!options.isEmpty()) {
            return options.get(rng.nextInt(options.size()));
        }
        return Direction.NONE;
    }

    // =========================
    // Memory + stuck detection
    // =========================

    private void remember(Player me) {
        lastPositions.putIfAbsent(me, new ArrayDeque<>());
        ArrayDeque<Position> q = lastPositions.get(me);
        q.addLast(me.getPosition());
        while (q.size() > MEMORY) q.removeFirst();
    }

    private boolean wasRecentlyThere(Player me, Position p) {
        ArrayDeque<Position> q = lastPositions.get(me);
        if (q == null) return false;
        for (Position past : q) {
            if (past.equals(p)) return true;
        }
        return false;
    }

    /**
     * Detecta loops típicos:
     *  - repetición inmediata
     *  - ping-pong A-B-A-B
     */
    private boolean isStuck(Player me) {
        ArrayDeque<Position> q = lastPositions.get(me);
        if (q == null || q.size() < 6) return false;

        Position[] arr = q.toArray(new Position[0]);
        int n = arr.length;

        // Ping-pong A-B-A-B...
        if (arr[n - 1].equals(arr[n - 3]) && arr[n - 2].equals(arr[n - 4])) return true;

        // Repeticiones seguidas
        int repeats = 0;
        for (int i = 1; i < n; i++) {
            if (arr[i].equals(arr[i - 1])) repeats++;
        }
        return repeats >= 2;
    }

    // =========================
    // BFS rescue (first step)
    // =========================

    /**
     * BFS para devolver SOLO el primer paso desde start hacia goal.
     * Se usa como rescate cuando la IA está atascada.
     */
    private Direction bfsFirstStep(Board board, Position start, Position goal) {
        if (start.equals(goal)) return Direction.NONE;

        int rows = board.getRows();
        int cols = board.getCols();

        boolean[][] vis = new boolean[rows][cols];
        Position[][] parent = new Position[rows][cols];
        Direction[][] parentDir = new Direction[rows][cols];

        ArrayDeque<Position> q = new ArrayDeque<>();
        q.add(start);
        vis[start.getRow()][start.getCol()] = true;

        Direction[] dirs = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};

        while (!q.isEmpty()) {
            Position cur = q.poll();
            if (cur.equals(goal)) break;

            for (Direction d : dirs) {
                Position nxt = cur.translated(d.getDRow(), d.getDCol());
                if (!board.isInside(nxt)) continue;
                if (vis[nxt.getRow()][nxt.getCol()]) continue;

                // Permitimos llegar a goal incluso si no es walkable (por si goal está en una celda especial)
                if (!board.isWalkable(nxt) && !nxt.equals(goal)) continue;

                vis[nxt.getRow()][nxt.getCol()] = true;
                parent[nxt.getRow()][nxt.getCol()] = cur;
                parentDir[nxt.getRow()][nxt.getCol()] = d;
                q.add(nxt);
            }
        }

        if (!vis[goal.getRow()][goal.getCol()]) return Direction.NONE;

        // reconstruir: caminar hacia atrás hasta que el padre sea start
        Position cur = goal;
        while (parent[cur.getRow()][cur.getCol()] != null &&
                !parent[cur.getRow()][cur.getCol()].equals(start)) {
            cur = parent[cur.getRow()][cur.getCol()];
        }

        Direction first = parentDir[cur.getRow()][cur.getCol()];
        return (first != null) ? first : Direction.NONE;
    }
}
