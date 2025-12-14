package domain.behavior;

import domain.entities.Enemy;
import domain.entities.Entity;
import domain.entities.Player;
import domain.game.Level;
import domain.model.Board;
import domain.model.CellType;
import domain.model.Position;
import domain.utils.Direction;

/**
 * Movimiento del Calamar Naranja
 * Persigue al jugador moviéndose hacia él
 * Si encuentra un bloque de hielo en su camino, se detiene y lo destruye
 * Solo rompe un bloque de hielo a la vez
 * Después de destruir el hielo, continúa persiguiendo
 */
public class OrangeSquidMovement implements MovementBehavior {

    private int tickCounter = 0;
    private static final int TICKS_PER_MOVE = 22;

    // Control de destrucción de hielo
    private boolean isBreakingIce = false;
    private int breakingTickCounter = 0;
    private static final int TICKS_TO_BREAK_ICE = 12;
    private Position iceBeingBroken = null;

    @Override
    public void move(Level level, Entity object) {
        Enemy enemy = (Enemy) object;

        // Control de velocidad
        tickCounter++;
        if (tickCounter % TICKS_PER_MOVE != 0) {
            return;
        }

        // Si estamos rompiendo hielo, continuamos con ese proceso
        if (isBreakingIce) {
            breakIce(level, enemy);
            return;
        }

        // Buscamos al jugador más cercano
        Player nearestPlayer = findNearestPlayer(level, enemy);
        if (nearestPlayer == null) {
            return; // No hay jugadores, nos quedamos quietos
        }

        Position current = enemy.getPosition();
        Position playerPos = nearestPlayer.getPosition();
        Board board = level.getBoard();

        // Calculamos la dirección hacia el jugador
        Direction bestDirection = calculateDirectionToPlayer(current, playerPos);

        if (bestDirection == Direction.NONE) {
            return; // Ya estamos en la misma posición (no debería pasar)
        }

        // Calculamos la siguiente posición
        Position next = current.translated(bestDirection.getDRow(), bestDirection.getDCol());

        // Verificamos si la siguiente posición es un bloque de hielo
        if (board.isInside(next) && isIceBlock(board, next)) {
            // Si hay una alternativa caminable que nos acerque, la preferimos
            if (!tryMoveWithoutBreakingIce(level, enemy, nearestPlayer)) {
                // Encontramos hielo, empezamos a romperlo
                startBreakingIce(next, bestDirection, enemy);
            }
            return;
        }

        // Si la celda es caminable, nos movemos
        if (board.isWalkable(next) || isPlayerCell(level, next)) {
            enemy.setPosition(next);
            enemy.setDirection(bestDirection);
        } else {
            // Si está bloqueado por algo que no es hielo, intentamos una ruta alternativa
            tryAlternativeRoute(level, enemy, nearestPlayer);
        }
    }

    /**
     * Encuentra el jugador más cercano al enemigo
     */
    private Player findNearestPlayer(Level level, Enemy enemy) {
        Player nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (Player player : level.getPlayers()) {
            if (player.isDead()) continue;

            int distance = manhattanDistance(enemy.getPosition(), player.getPosition());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = player;
            }
        }

        return nearest;
    }

    private boolean isPlayerCell(Level level, Position pos) {
        for (Player p : level.getPlayers()) {
            if (p.isDead()) continue;
            if (p.getPosition().equals(pos)) return true;
        }
        return false;
    }

    /**
     * Calcula la distancia de Manhattan entre dos posiciones
     */
    private int manhattanDistance(Position a, Position b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }

    /**
     * Calcula la mejor dirección para moverse hacia el jugador
     * Prioriza primero la dirección con mayor diferencia
     */
    private Direction calculateDirectionToPlayer(Position enemyPos, Position playerPos) {
        int rowDiff = playerPos.getRow() - enemyPos.getRow();
        int colDiff = playerPos.getCol() - enemyPos.getCol();

        // Si ya estamos en la misma posición
        if (rowDiff == 0 && colDiff == 0) {
            return Direction.NONE;
        }

        // Priorizamos la dirección con mayor diferencia
        if (Math.abs(rowDiff) > Math.abs(colDiff)) {
            // Moverse verticalmente
            return rowDiff > 0 ? Direction.DOWN : Direction.UP;
        } else if (Math.abs(colDiff) > Math.abs(rowDiff)) {
            // Moverse horizontalmente
            return colDiff > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            // Son iguales, elegimos vertical por defecto
            return rowDiff > 0 ? Direction.DOWN : Direction.UP;
        }
    }

    /**
     * Verifica si una posición contiene un bloque de hielo
     */
    private boolean isIceBlock(Board board, Position pos) {
        CellType cellType = board.getCellType(pos);
        return cellType == CellType.ICE_BLOCK || cellType == CellType.PLAYER_ICE;
    }

    /**
     * Inicia el proceso de romper un bloque de hielo
     */
    private void startBreakingIce(Position icePosition, Direction direction, Enemy enemy) {
        isBreakingIce = true;
        breakingTickCounter = 0;
        iceBeingBroken = icePosition;
        enemy.setDirection(direction);
    }

    /**
     * Continúa el proceso de romper el hielo
     */
    private void breakIce(Level level, Enemy enemy) {
        breakingTickCounter++;

        if (breakingTickCounter >= TICKS_TO_BREAK_ICE) {
            // Terminamos de romper el hielo
            Board board = level.getBoard();
            if (board.isInside(iceBeingBroken) && isIceBlock(board, iceBeingBroken)) {
                board.setCellType(iceBeingBroken, CellType.FLOOR);
            }

            // Reiniciamos el estado
            isBreakingIce = false;
            breakingTickCounter = 0;
            iceBeingBroken = null;
        }
    }

    /**
     * Intenta una ruta alternativa cuando el camino directo está bloqueado
     */
    private void tryAlternativeRoute(Level level, Enemy enemy, Player targetPlayer) {
        Position current = enemy.getPosition();
        Position playerPos = targetPlayer.getPosition();
        Board board = level.getBoard();

        Direction[] directions = {
                Direction.UP,
                Direction.DOWN,
                Direction.LEFT,
                Direction.RIGHT
        };

        int currentDist = manhattanDistance(current, playerPos);

        Direction bestWalkDir = Direction.NONE;
        int bestWalkDistance = Integer.MAX_VALUE;

        Direction bestIceDir = Direction.NONE;
        int bestIceDistance = Integer.MAX_VALUE;
        Position bestIcePos = null;

        for (Direction dir : directions) {
            Position next = current.translated(dir.getDRow(), dir.getDCol());

            if (!board.isInside(next)) continue;

            if (board.isWalkable(next) || isPlayerCell(level, next)) {
                int dist = manhattanDistance(next, playerPos);
                if (dist < bestWalkDistance) {
                    bestWalkDistance = dist;
                    bestWalkDir = dir;
                }
            } else if (isIceBlock(board, next)) {
                int dist = manhattanDistance(next, playerPos);
                if (dist < bestIceDistance) {
                    bestIceDistance = dist;
                    bestIceDir = dir;
                    bestIcePos = next;
                }
            }
        }

        if (bestWalkDir != Direction.NONE) {
            Position next = current.translated(bestWalkDir.getDRow(), bestWalkDir.getDCol());
            enemy.setPosition(next);
            enemy.setDirection(bestWalkDir);
            return;
        }

        if (bestIcePos != null && bestIceDistance <= currentDist) {
            startBreakingIce(bestIcePos, bestIceDir, enemy);
        }
    }

    private boolean tryMoveWithoutBreakingIce(Level level, Enemy enemy, Player targetPlayer) {
        Position current = enemy.getPosition();
        Position playerPos = targetPlayer.getPosition();
        Board board = level.getBoard();

        Direction[] directions = {
                Direction.UP,
                Direction.DOWN,
                Direction.LEFT,
                Direction.RIGHT
        };

        int currentDist = manhattanDistance(current, playerPos);

        Direction bestDir = Direction.NONE;
        int bestDistance = Integer.MAX_VALUE;

        for (Direction dir : directions) {
            Position next = current.translated(dir.getDRow(), dir.getDCol());

            if (!board.isInside(next)) continue;

            if (board.isWalkable(next) || isPlayerCell(level, next)) {
                int dist = manhattanDistance(next, playerPos);
                if (dist < bestDistance) {
                    bestDistance = dist;
                    bestDir = dir;
                }
            }
        }

        if (bestDir != Direction.NONE && bestDistance <= currentDist) {
            Position next = current.translated(bestDir.getDRow(), bestDir.getDCol());
            enemy.setPosition(next);
            enemy.setDirection(bestDir);
            return true;
        }

        return false;
    }
}
