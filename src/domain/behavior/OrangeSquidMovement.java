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
    private static final int TICKS_PER_MOVE = 16;

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

        // Calculamos la dirección hacia el jugador
        Direction bestDirection = calculateDirectionToPlayer(enemy.getPosition(), nearestPlayer.getPosition());

        if (bestDirection == Direction.NONE) {
            return; // Ya estamos en la misma posición (no debería pasar)
        }

        // Calculamos la siguiente posición
        Position current = enemy.getPosition();
        Position next = current.translated(bestDirection.getDRow(), bestDirection.getDCol());

        Board board = level.getBoard();

        // Verificamos si la siguiente posición es un bloque de hielo
        if (board.isInside(next) && isIceBlock(board, next)) {
            // Encontramos hielo, empezamos a romperlo
            startBreakingIce(next, bestDirection, enemy);
            return;
        }

        // Si la celda es caminable, nos movemos
        if (board.isWalkable(next)) {
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
            int distance = manhattanDistance(enemy.getPosition(), player.getPosition());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = player;
            }
        }

        return nearest;
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
                board.setCellType(iceBeingBroken, CellType.EMPTY);
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

        int rowDiff = playerPos.getRow() - current.getRow();
        int colDiff = playerPos.getCol() - current.getCol();

        // Si estábamos intentando movernos verticalmente, probamos horizontal
        if (Math.abs(rowDiff) >= Math.abs(colDiff)) {
            Direction altDir = colDiff > 0 ? Direction.RIGHT : (colDiff < 0 ? Direction.LEFT : Direction.NONE);
            if (altDir != Direction.NONE) {
                Position altNext = current.translated(altDir.getDRow(), altDir.getDCol());
                if (board.isWalkable(altNext)) {
                    enemy.setPosition(altNext);
                    enemy.setDirection(altDir);
                }
            }
        } else {
            // Estábamos intentando movernos horizontalmente, probamos vertical
            Direction altDir = rowDiff > 0 ? Direction.DOWN : (rowDiff < 0 ? Direction.UP : Direction.NONE);
            if (altDir != Direction.NONE) {
                Position altNext = current.translated(altDir.getDRow(), altDir.getDCol());
                if (board.isWalkable(altNext)) {
                    enemy.setPosition(altNext);
                    enemy.setDirection(altDir);
                }
            }
        }
    }
}