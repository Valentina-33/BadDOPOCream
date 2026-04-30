package domain.mechanics;

import domain.entities.Campfire;
import domain.entities.Enemy;
import domain.entities.Fruit;
import domain.entities.Player;
import domain.game.CollisionDetector;
import domain.game.Level;
import domain.model.Board;
import domain.model.CellType;
import domain.model.Position;
import domain.utils.Direction;

import java.util.List;

public class IceMechanic {

    public void triggerIceAction(Level level, Player p) {
        Direction dir = p.getDirection();
        if (dir == null || dir == Direction.NONE) return;

        Board board = level.getBoard();
        Position start = p.getPosition();
        Position next = start.translated(dir.getDRow(), dir.getDCol());

        if (!board.isInside(next)) return;

        CellType firstCell = board.getCellType(next);

        // Si hay hielo enfrente: ROMPER
        if (firstCell == CellType.PLAYER_ICE || firstCell == CellType.ICE_BLOCK) {
            unfreezeFruitsInRay(level.getFruitManager().getAllFruits(), next, dir, board);
            breakIceRay(level, next, dir);
        } else {
            // Si hay espacio: CREAR
            createIceRay(level, start, dir);
        }
    }

    private void createIceRay(Level level, Position from, Direction dir) {
        Board board = level.getBoard();
        Position current = from.translated(dir.getDRow(), dir.getDCol());

        while (board.isInside(current)) {
            // Parar si hay enemigo
            if (isEnemyAt(level, current)) break;

            // Apagar fogata y parar (derrite el hielo)
            Campfire cf = getCampfireAt(level, current);
            if (cf != null) {
                if (cf.isLit()) cf.extinguish(board);
                current = current.translated(dir.getDRow(), dir.getDCol());
                continue;
            }

            CellType cellType = board.getCellType(current);

            // Saltar baldosas calientes
            if (cellType == CellType.HOT_TILE) {
                current = current.translated(dir.getDRow(), dir.getDCol());
                continue;
            }

            // Bloqueos físicos
            if (CollisionDetector.isBlocked(board, current)) break;

            // Poner hielo
            if (cellType == CellType.FLOOR || cellType == CellType.PILE_SNOW) {
                board.setCellType(current, CellType.PLAYER_ICE);
            }

            // Congelar frutas
            freezeFruitsAt(level, current);

            current = current.translated(dir.getDRow(), dir.getDCol());
        }
    }

    private void breakIceRay(Level level, Position from, Direction dir) {
        Board board = level.getBoard();
        Position current = from;

        while (board.isInside(current)) {
            Campfire cf = getCampfireAt(level, current);
            if (cf != null) {
                if (cf.isLit()) cf.extinguish(board);
                current = current.translated(dir.getDRow(), dir.getDCol());
                continue;
            }

            CellType cell = board.getCellType(current);
            if (cell == CellType.PLAYER_ICE || cell == CellType.ICE_BLOCK) {
                board.setCellType(current, CellType.FLOOR);
                current = current.translated(dir.getDRow(), dir.getDCol());
            } else {
                break;
            }
        }
    }

    private void unfreezeFruitsInRay(List<Fruit> fruits, Position from, Direction dir, Board board) {
        Position current = from;
        while (board.isInside(current)) {
            if (board.getCellType(current) != CellType.PLAYER_ICE) break;

            for (Fruit f : fruits) {
                if (f.isFrozen() && samePos(f.getPosition(), current)) {
                    f.unfreeze();
                }
            }
            current = current.translated(dir.getDRow(), dir.getDCol());
        }
    }

    private void freezeFruitsAt(Level level, Position pos) {
        for (Fruit f : level.getFruitManager().getAllFruits()) {
            if (!f.isCollected() && !f.isFrozen() && samePos(f.getPosition(), pos)) {
                f.freeze();
            }
        }
    }

    private boolean isEnemyAt(Level level, Position pos) {
        for (Enemy e : level.getEnemies()) {
            if (samePos(e.getPosition(), pos)) return true;
        }
        return false;
    }

    private Campfire getCampfireAt(Level level, Position pos) {
        for (Campfire c : level.getCampfires()) {
            if (samePos(c.getPosition(), pos)) return c;
        }
        return null;
    }

    private boolean samePos(Position a, Position b) {
        return a.getRow() == b.getRow() && a.getCol() == b.getCol();
    }
}