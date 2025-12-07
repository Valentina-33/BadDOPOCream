package domain.behavior;

import domain.entities.MovingFruit;
import domain.game.Level;
import domain.model.Board;
import domain.model.CellType;
import domain.model.Position;
import domain.utils.Direction;

import java.util.Random;

public class PineappleMovement implements FruitMovementBehavior {

    // La piña se mueve por "ejes": primero vertical, luego horizontal
    private enum Axis { VERTICAL, HORIZONTAL }

    private Axis axis = Axis.VERTICAL;
    private Direction dir = Direction.UP;

    // Control de velocidad
    private int tickCounter = 0;
    private static final int TICKS_PER_MOVE = 3;

    // Estado del salto "tipo helicóptero"
    private boolean jumping = false;
    private Position jumpOver;
    private Position jumpLanding;
    private int jumpPhase = 0;

    // 🎲 Cambio aleatorio de eje
    private final Random rng = new Random();
    private int movementsInCurrentAxis = 0;
    private static final int MIN_MOVEMENTS_BEFORE_CHANGE = 3;  // Mínimo antes de considerar cambio
    private static final int MAX_MOVEMENTS_BEFORE_FORCE = 8;   // Máximo antes de forzar cambio
    private static final double CHANCE_TO_CHANGE_AXIS = 0.15;  // 15% de probabilidad de cambiar

    @Override
    public void move(Level level, MovingFruit fruit) {
        Board board = level.getBoard();

        tickCounter++;
        if (tickCounter % TICKS_PER_MOVE != 0) {
            return;
        }

        Position current = fruit.getPosition();

        // 1️⃣ Si está en medio de un salto, solo continuamos el salto
        if (jumping) {
            continueJump(fruit);
            return;
        }

        // 2️⃣ Verificar si deberíamos cambiar de eje aleatoriamente
        movementsInCurrentAxis++;

        // Cambio forzado si llevamos demasiados movimientos en el mismo eje
        if (movementsInCurrentAxis >= MAX_MOVEMENTS_BEFORE_FORCE) {
            switchAxis();
        }
        // Cambio aleatorio si ya llevamos el mínimo de movimientos
        else if (movementsInCurrentAxis >= MIN_MOVEMENTS_BEFORE_CHANGE
                && rng.nextDouble() < CHANCE_TO_CHANGE_AXIS) {
            switchAxis();
        }

        // 3️⃣ Intentar moverse según el eje actual
        if (axis == Axis.VERTICAL) {
            // Intentar moverse en la dirección vertical actual
            if (tryMoveOrPrepareJump(board, fruit, current, dir)) {
                return;
            }

            // Intentar en la dirección vertical opuesta
            Direction opposite = dir.opposite();
            if (tryMoveOrPrepareJump(board, fruit, current, opposite)) {
                dir = opposite;
                return;
            }

            // No pudo moverse verticalmente -> forzar cambio al eje horizontal
            switchAxis();

            // Intentar inmediatamente moverse en el nuevo eje
            if (tryMoveOrPrepareJump(board, fruit, current, dir)) {
                return;
            }
            Direction horOpp = dir.opposite();
            if (tryMoveOrPrepareJump(board, fruit, current, horOpp)) {
                dir = horOpp;
            }

        } else { // axis == HORIZONTAL
            // Intentar moverse en la dirección horizontal actual
            if (tryMoveOrPrepareJump(board, fruit, current, dir)) {
                return;
            }

            // Intentar en la dirección horizontal opuesta
            Direction opposite = dir.opposite();
            if (tryMoveOrPrepareJump(board, fruit, current, opposite)) {
                dir = opposite;
                return;
            }

            // No pudo moverse horizontalmente -> forzar cambio a eje vertical
            switchAxis();

            if (tryMoveOrPrepareJump(board, fruit, current, dir)) {
                return;
            }
            Direction vertOpp = dir.opposite();
            if (tryMoveOrPrepareJump(board, fruit, current, vertOpp)) {
                dir = vertOpp;
            }
        }
    }

    /**
     * Cambia el eje de movimiento y resetea el contador
     */
    private void switchAxis() {
        if (axis == Axis.VERTICAL) {
            axis = Axis.HORIZONTAL;
            dir = rng.nextBoolean() ? Direction.LEFT : Direction.RIGHT;
        } else {
            axis = Axis.VERTICAL;
            dir = rng.nextBoolean() ? Direction.UP : Direction.DOWN;
        }
        movementsInCurrentAxis = 0;
    }

    /**
     * Intenta:
     *  1) Avanzar un tile normal
     *  2) Si no, preparar un salto de 1 bloque de hielo con animación en 2 pasos.
     *
     * Devuelve true si se movió o comenzó un salto, false si no pudo.
     */
    private boolean tryMoveOrPrepareJump(Board board,
                                         MovingFruit fruit,
                                         Position from,
                                         Direction d) {

        if (d == null || d == Direction.NONE) return false;

        Position next = from.translated(d.getDRow(), d.getDCol());

        // 1️⃣ Intento de movimiento normal
        if (board.isInside(next) && board.isWalkable(next)) {
            fruit.setPosition(next);
            return true;
        }

        // 2️⃣ Intento de salto: un bloque de hielo + casilla libre detrás
        Position over    = next;
        Position landing = over.translated(d.getDRow(), d.getDCol());

        if (!board.isInside(over) || !board.isInside(landing)) {
            return false;
        }

        CellType overType    = board.getCellType(over);
        CellType landingType = board.getCellType(landing);

        boolean isIce =
                overType == CellType.ICE_BLOCK ||
                        overType == CellType.PLAYER_ICE;

        boolean landingWalkable = board.isWalkable(landing);

        if (isIce && landingWalkable) {
            // 🚁 Iniciar salto "tipo helicóptero" en 2 fases
            jumping      = true;
            jumpOver     = over;
            jumpLanding  = landing;
            jumpPhase    = 0;

            return true;
        }

        return false;
    }

    /**
     * Continúa un salto ya iniciado:
     *  - Fase 0: se dibuja sobre el bloque de hielo.
     *  - Fase 1: cae en la casilla de aterrizaje.
     */
    private void continueJump(MovingFruit fruit) {
        if (jumpPhase == 0) {
            fruit.setPosition(jumpOver);
            jumpPhase = 1;
        } else {
            fruit.setPosition(jumpLanding);
            jumping = false;
        }
    }
}