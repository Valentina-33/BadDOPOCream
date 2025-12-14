package domain.behavior;

import domain.entities.*;
import domain.game.Level;
import domain.model.*;
import domain.utils.Direction;

import java.util.List;
import java.util.Random;

/**
 * Comportamiento del Narval:
 * Modo PATRULLA: Se mueve en línea recta, rebotando en paredes
 * Modo EMBESTIDA: Detecta jugador alineado y embiste rápidamente, destruyendo hielo
 */
public class NarvalMovement implements MovementBehavior {

    // Estados del Narval
    private enum State {
        PATROL,    // Patrullando normalmente
        CHARGING   // Embestida
    }

    private State state = State.PATROL;

    // Velocidades diferentes según el estado
    private int tickCounter = 0;
    private static final int TICKS_PER_PATROL_MOVE = 20;  // Patrulla lenta
    private static final int TICKS_PER_CHARGE_MOVE = 6;   // Embestida rápida

    // Rango de detección (cuántas casillas puede ver)
    private static final int DETECTION_RANGE = 15;

    private final Random random = new Random();

    @Override
    public void move(Level level, Entity object) {
        Enemy narwhal = (Enemy) object;

        tickCounter++;

        // Control de velocidad según el estado
        int requiredTicks = (state == State.CHARGING) ? TICKS_PER_CHARGE_MOVE : TICKS_PER_PATROL_MOVE;

        if (tickCounter % requiredTicks != 0) {
            return;
        }

        // Verificar si hay un jugador alineado
        Player targetPlayer = detectAlignedPlayer(level, narwhal);

        if (targetPlayer != null && state == State.PATROL) {
            // Cambiar a modo embestida
            startCharging(narwhal, targetPlayer);
        }

        // Ejecutar comportamiento según el estado
        if (state == State.CHARGING) {
            chargeMove(level, narwhal);
        } else {
            patrolMove(level, narwhal);
        }
    }

    /**
     * Detecta si hay un jugador alineado horizontal o verticalmente
     * @return El jugador alineado más cercano, o null si no hay ninguno
     */
    private Player detectAlignedPlayer(Level level, Enemy narwhal) {
        Position narwhalPos = narwhal.getPosition();
        List<Player> players = level.getPlayers();
        Board board = level.getBoard();

        Player bestPlayer = null;
        int bestDistance = Integer.MAX_VALUE;

        for (Player player : players) {
            if (player.isDead()) continue;

            Position playerPos = player.getPosition();

            // Verificar alineación horizontal (misma fila)
            if (narwhalPos.getRow() == playerPos.getRow()) {
                int distance = Math.abs(narwhalPos.getCol() - playerPos.getCol());
                if (distance <= DETECTION_RANGE && distance < bestDistance &&
                        hasLineOfSight(board, narwhalPos, playerPos, true)) {
                    bestDistance = distance;
                    bestPlayer = player;
                }
            }

            // Verificar alineación vertical (misma columna)
            if (narwhalPos.getCol() == playerPos.getCol()) {
                int distance = Math.abs(narwhalPos.getRow() - playerPos.getRow());
                if (distance <= DETECTION_RANGE && distance < bestDistance &&
                        hasLineOfSight(board, narwhalPos, playerPos, false)) {
                    bestDistance = distance;
                    bestPlayer = player;
                }
            }
        }

        return bestPlayer;
    }

    /**
     * Verifica si hay línea de visión entre dos posiciones.
     * Solo considera paredes como obstáculos, el hielo se puede romper durante la embestida.
     */
    private boolean hasLineOfSight(Board board, Position from, Position to, boolean horizontal) {
        if (horizontal) {
            int row = from.getRow();
            int colStart = Math.min(from.getCol(), to.getCol());
            int colEnd = Math.max(from.getCol(), to.getCol());

            for (int col = colStart + 1; col < colEnd; col++) {
                Position pos = new Position(row, col);
                CellType cell = board.getCellType(pos);

                // Solo las paredes bloquean la visión
                if (cell == CellType.METALLIC_WALL ||
                        cell == CellType.RED_WALL ||
                        cell == CellType.YELLOW_WALL) {
                    return false;
                }
            }
            // Vertical
        } else {
            int col = from.getCol();
            int rowStart = Math.min(from.getRow(), to.getRow());
            int rowEnd = Math.max(from.getRow(), to.getRow());

            for (int row = rowStart + 1; row < rowEnd; row++) {
                Position pos = new Position(row, col);
                CellType cell = board.getCellType(pos);

                // Solo las paredes bloquean la visión
                if (cell == CellType.METALLIC_WALL ||
                        cell == CellType.RED_WALL ||
                        cell == CellType.YELLOW_WALL) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Inicia el modo de embestida hacia el jugador
     */
    private void startCharging(Enemy narwhal, Player target) {
        state = State.CHARGING;

        Position narwhalPos = narwhal.getPosition();
        Position playerPos = target.getPosition();

        // Determinar dirección de la embestida
        if (narwhalPos.getRow() == playerPos.getRow()) {
            // Alineación horizontal
            narwhal.setDirection(playerPos.getCol() > narwhalPos.getCol() ? Direction.RIGHT : Direction.LEFT);
        } else {
            // Alineación vertical
            narwhal.setDirection(playerPos.getRow() > narwhalPos.getRow() ? Direction.DOWN : Direction.UP);
        }
    }

    /**
     * Movimiento durante la embestida: avanza rápido y destruye hielo
     */
    private void chargeMove(Level level, Enemy narwhal) {
        Board board = level.getBoard();
        Position current = narwhal.getPosition();
        Direction dir = narwhal.getDirection();

        if (dir == null || dir == Direction.NONE) {
            state = State.PATROL;
            return;
        }

        Position next = current.translated(dir.getDRow(), dir.getDCol());

        if (!board.isInside(next)) {
            // Chocó con el borde, volver a patrulla
            state = State.PATROL;
            return;
        }

        CellType nextCell = board.getCellType(next);

        // Destruir hielo en el camino
        if (nextCell == CellType.ICE_BLOCK || nextCell == CellType.PLAYER_ICE) {
            board.setCellType(next, CellType.FLOOR);
            narwhal.setPosition(next);
            return;
        }

        // Si es caminable, avanzar
        if (board.isWalkable(next)) {
            narwhal.setPosition(next);
        } else {
            // Chocó con pared, volver a patrulla
            state = State.PATROL;
        }
    }

    /**
     * Movimiento de patrulla: avanza en línea recta y rebota en paredes
     */
    private void patrolMove(Level level, Enemy narwhal) {
        Board board = level.getBoard();
        Position current = narwhal.getPosition();
        Direction dir = narwhal.getDirection();

        if (dir == null || dir == Direction.NONE) {
            Direction newDir = pickRandomWalkableDirection(board, current);
            if (newDir == Direction.NONE) return;
            narwhal.setDirection(newDir);
            dir = newDir;
        }

        Position next = current.translated(dir.getDRow(), dir.getDCol());

        // Si puede avanzar, lo hace
        if (board.isInside(next) && board.isWalkable(next)) {
            narwhal.setPosition(next);
        } else {
            // Rebotar: girar 180 grados
            Direction opposite = dir.opposite();
            Position oppositeNext = current.translated(opposite.getDRow(), opposite.getDCol());

            if (board.isInside(oppositeNext) && board.isWalkable(oppositeNext)) {
                narwhal.setDirection(opposite);
            } else {
                Direction newDir = pickRandomWalkableDirection(board, current);
                if (newDir != Direction.NONE) {
                    narwhal.setDirection(newDir);
                }
            }
        }
    }

    private Direction pickRandomWalkableDirection(Board board, Position current) {
        Direction[] dirs = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};

        for (int i = dirs.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Direction tmp = dirs[i];
            dirs[i] = dirs[j];
            dirs[j] = tmp;
        }

        for (Direction d : dirs) {
            Position next = current.translated(d.getDRow(), d.getDCol());
            if (board.isInside(next) && board.isWalkable(next)) {
                return d;
            }
        }

        return Direction.NONE;
    }
}
