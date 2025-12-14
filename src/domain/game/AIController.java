package domain.game;

import domain.entities.Enemy;
import domain.entities.Fruit;
import domain.entities.Player;
import domain.model.Board;
import domain.model.Position;
import domain.utils.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Decide el próximo movimiento de un jugador controlado por máquina.
 */
public class AIController {

    private final AIProfile profile;
    private final Random rng = new Random();

    public AIController(AIProfile profile) {
        this.profile = profile;
    }

    public AIProfile getProfile() {
        return profile;
    }

    public Direction decide(Level level, Player me) {
        if (me == null || me.isDead()) return Direction.NONE;

        return switch (profile) {
            case HUNGRY -> hungryMove(level, me);
            case FEARFUL -> fearfulMove(level, me);
            case EXPERT -> expertMove(level, me);
        };
    }

    private Direction hungryMove(Level level, Player me) {
        Fruit target = nearestActiveFruit(level, me.getPosition());
        if (target == null) {
            return randomWalk(level.getBoard(), me.getPosition());
        }
        return stepToward(level.getBoard(), me.getPosition(), target.getPosition());
    }

    private Direction fearfulMove(Level level, Player me) {
        Enemy threat = nearestEnemy(level, me.getPosition());
        if (threat == null) {
            Fruit target = nearestActiveFruit(level, me.getPosition());
            if (target != null) return stepToward(level.getBoard(), me.getPosition(), target.getPosition());
            return randomWalk(level.getBoard(), me.getPosition());
        }
        return stepAway(level.getBoard(), me.getPosition(), threat.getPosition());
    }

    private Direction expertMove(Level level, Player me) {
        Board board = level.getBoard();
        Position mePos = me.getPosition();

        // Si hay enemigo cerca, primero aléjate
        Enemy threat = nearestEnemy(level, mePos);
        if (threat != null) {
            int d = manhattan(mePos, threat.getPosition());
            if (d <= 3) {
                Direction away = stepAway(board, mePos, threat.getPosition());
                if (away != Direction.NONE) return away;
            }
        }

        // Si no hay amenaza inmediata, ve por fruta
        Fruit target = nearestActiveFruit(level, mePos);
        if (target != null) {
            Direction toward = stepToward(board, mePos, target.getPosition());
            if (toward != Direction.NONE) return toward;
        }

        // Si no hay nada, camina random
        return randomWalk(board, mePos);
    }

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

    private int manhattan(Position a, Position b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }

    private Direction stepToward(Board board, Position from, Position goal) {
        int bestDist = Integer.MAX_VALUE;
        Direction bestDir = Direction.NONE;

        for (Direction d : new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT}) {
            Position next = from.translated(d.getDRow(), d.getDCol());
            if (!board.isInside(next)) continue;
            if (!board.isWalkable(next)) continue;

            int dist = manhattan(next, goal);
            if (dist < bestDist) {
                bestDist = dist;
                bestDir = d;
            }
        }
        return bestDir;
    }

    private Direction stepAway(Board board, Position from, Position threat) {
        int bestDist = Integer.MIN_VALUE;
        Direction bestDir = Direction.NONE;

        for (Direction d : new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT}) {
            Position next = from.translated(d.getDRow(), d.getDCol());
            if (!board.isInside(next)) continue;
            if (!board.isWalkable(next)) continue;

            int dist = manhattan(next, threat);
            if (dist > bestDist) {
                bestDist = dist;
                bestDir = d;
            }
        }
        return bestDir;
    }

    private Direction randomWalk(Board board, Position from) {
        List<Direction> options = new ArrayList<>();

        for (Direction d : new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT}) {
            Position next = from.translated(d.getDRow(), d.getDCol());
            if (board.isInside(next) && board.isWalkable(next)) options.add(d);
        }

        if (options.isEmpty()) return Direction.NONE;
        return options.get(rng.nextInt(options.size()));
    }
}
