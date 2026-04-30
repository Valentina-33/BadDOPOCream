package domain.ai;

import domain.entities.Enemy;
import domain.entities.Fruit;
import domain.game.Level;
import domain.model.Board;
import domain.model.Position;
import domain.utils.Direction;

import java.util.*;

/**
 * Clase que
 */
public abstract class AbstractAIStrategy implements AIMovementStrategy {

    protected final Random rng = new Random();

    private final ArrayDeque<Position> memory = new ArrayDeque<>();
    private static final int MEMORY_SIZE = 6;


    protected void remember(Position pos) {
        memory.addLast(pos);
        while (memory.size() > MEMORY_SIZE) memory.removeFirst();
    }

    protected boolean wasRecentlyThere(Position p) {
        for (Position past : memory) {
            if (past.equals(p)) return true;
        }
        return false;
    }

    protected boolean isStuck() {
        if (memory.size() < 6) return false;
        Position[] arr = memory.toArray(new Position[0]);
        int n = arr.length;

        if (arr[n - 1].equals(arr[n - 3]) && arr[n - 2].equals(arr[n - 4])) return true;

        int repeats = 0;
        for (int i = 1; i < n; i++) {
            if (arr[i].equals(arr[i - 1])) repeats++;
        }
        return repeats >= 2;
    }


    protected int manhattan(Position a, Position b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }

    protected Direction[] getDirections() {
        return new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
    }


    protected Fruit nearestActiveFruit(Level level, Position from) {
        List<Fruit> fruits = level.getFruitManager().getActiveFruits();
        Fruit best = null;
        int bestDist = Integer.MAX_VALUE;

        for (Fruit f : fruits) {
            if (f.isCollected() || f.isFrozen()) continue;

            int d = manhattan(from, f.getPosition());
            if (d < bestDist) {
                bestDist = d;
                best = f;
            }
        }
        return best;
    }

    protected Enemy nearestEnemy(Level level, Position from) {
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


    protected Direction randomWalk(Board board, Position from) {
        List<Direction> options = new ArrayList<>();
        List<Direction> safeOptions = new ArrayList<>();

        for (Direction d : getDirections()) {
            Position next = from.translated(d.getDRow(), d.getDCol());
            if (!board.isInside(next) || !board.isWalkable(next)) continue;

            options.add(d);
            if (!wasRecentlyThere(next)) safeOptions.add(d);
        }

        if (!safeOptions.isEmpty()) {
            return safeOptions.get(rng.nextInt(safeOptions.size()));
        }
        if (!options.isEmpty()) {
            return options.get(rng.nextInt(options.size()));
        }
        return Direction.NONE;
    }

    protected Direction stepToward(Board board, Position from, Position goal) {
        int bestScore = Integer.MAX_VALUE;
        Direction bestDir = Direction.NONE;

        for (Direction d : getDirections()) {
            Position next = from.translated(d.getDRow(), d.getDCol());
            if (!board.isInside(next) || !board.isWalkable(next)) continue;

            int dist = manhattan(next, goal);
            int penaltyLoop = wasRecentlyThere(next) ? 3 : 0;
            int score = dist + penaltyLoop;

            if (score < bestScore) {
                bestScore = score;
                bestDir = d;
            }
        }
        return bestDir;
    }

    protected Direction stepAway(Board board, Position from, Position threat) {
        int bestScore = Integer.MIN_VALUE;
        Direction bestDir = Direction.NONE;

        for (Direction d : getDirections()) {
            Position next = from.translated(d.getDRow(), d.getDCol());
            if (!board.isInside(next) || !board.isWalkable(next)) continue;

            int dist = manhattan(next, threat);
            int penaltyLoop = wasRecentlyThere(next) ? 2 : 0;
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
        for (Direction d : getDirections()) {
            Position n = p.translated(d.getDRow(), d.getDCol());
            if (board.isInside(n) && board.isWalkable(n)) count++;
        }
        return count;
    }


    protected Direction bfsRescue(Board board, Position start, Position goal) {
        if (start.equals(goal)) return Direction.NONE;

        int rows = board.getRows();
        int cols = board.getCols();

        boolean[][] vis = new boolean[rows][cols];
        Position[][] parent = new Position[rows][cols];
        Direction[][] parentDir = new Direction[rows][cols];

        ArrayDeque<Position> q = new ArrayDeque<>();
        q.add(start);
        vis[start.getRow()][start.getCol()] = true;

        while (!q.isEmpty()) {
            Position cur = q.poll();
            if (cur.equals(goal)) break;

            for (Direction d : getDirections()) {
                Position nxt = cur.translated(d.getDRow(), d.getDCol());
                if (!board.isInside(nxt)) continue;
                if (vis[nxt.getRow()][nxt.getCol()]) continue;
                if (!board.isWalkable(nxt) && !nxt.equals(goal)) continue;

                vis[nxt.getRow()][nxt.getCol()] = true;
                parent[nxt.getRow()][nxt.getCol()] = cur;
                parentDir[nxt.getRow()][nxt.getCol()] = d;
                q.add(nxt);
            }
        }

        if (!vis[goal.getRow()][goal.getCol()]) return Direction.NONE;

        Position cur = goal;
        while (parent[cur.getRow()][cur.getCol()] != null &&
                !parent[cur.getRow()][cur.getCol()].equals(start)) {
            cur = parent[cur.getRow()][cur.getCol()];
        }

        Direction first = parentDir[cur.getRow()][cur.getCol()];
        return (first != null) ? first : Direction.NONE;
    }
}