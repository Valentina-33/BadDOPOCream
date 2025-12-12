package domain.behavior;

import domain.entities.MovingFruit;
import domain.game.Level;
import domain.model.*;
import domain.utils.Direction;

import java.util.Random;

/**
 * Comportamiento de la Piña:
 * Se mueve alternando entre ejes vertical y horizontal, cambiando de eje
 * aleatoriamente o al encontrar obstáculos. Puede saltar sobre bloques de hielo
 * individuales en una animación de dos fases.
 */
public class PineappleMovement implements FruitMovementBehavior {

    private enum Axis { VERTICAL, HORIZONTAL }

    private Axis axis = Axis.VERTICAL;
    private Direction dir = Direction.UP;

    private int tickCounter = 0;
    private static final int TICKS_PER_MOVE = 16;

    private boolean jumping = false;
    private Position jumpOver;
    private Position jumpLanding;
    private int jumpPhase = 0;

    private final Random rng = new Random();
    private int movementsInCurrentAxis = 0;
    private static final int MIN_MOVEMENTS_BEFORE_CHANGE = 3;
    private static final int MAX_MOVEMENTS_BEFORE_FORCE = 8;
    private static final double CHANCE_TO_CHANGE_AXIS = 0.15;

    @Override
    public void move(Level level, MovingFruit fruit) {
        Board board = level.getBoard();

        tickCounter++;
        if (tickCounter % TICKS_PER_MOVE != 0) {
            return;
        }

        Position current = fruit.getPosition();

        if (jumping) {
            continueJump(fruit);
            return;
        }

        movementsInCurrentAxis++;

        if (movementsInCurrentAxis >= MAX_MOVEMENTS_BEFORE_FORCE) {
            switchAxis();
        } else if (movementsInCurrentAxis >= MIN_MOVEMENTS_BEFORE_CHANGE
                && rng.nextDouble() < CHANCE_TO_CHANGE_AXIS) {
            switchAxis();
        }

        if (axis == Axis.VERTICAL) {
            if (tryMoveOrPrepareJump(board, fruit, current, dir)) {
                return;
            }

            Direction opposite = dir.opposite();
            if (tryMoveOrPrepareJump(board, fruit, current, opposite)) {
                dir = opposite;
                return;
            }

            switchAxis();

            if (tryMoveOrPrepareJump(board, fruit, current, dir)) {
                return;
            }
            Direction horOpp = dir.opposite();
            if (tryMoveOrPrepareJump(board, fruit, current, horOpp)) {
                dir = horOpp;
            }

        } else {
            if (tryMoveOrPrepareJump(board, fruit, current, dir)) {
                return;
            }

            Direction opposite = dir.opposite();
            if (tryMoveOrPrepareJump(board, fruit, current, opposite)) {
                dir = opposite;
                return;
            }

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
     * Cambia el eje de movimiento actual y elige una dirección aleatoria en el nuevo eje.
     * Resetea el contador de movimientos realizados.
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
     * Intenta mover la fruta en la dirección especificada.
     * Si el movimiento normal está bloqueado, intenta preparar un salto sobre un bloque de hielo.
     * Retorna true si la fruta se movió o comenzó un salto, false si no pudo realizar ninguna acción.
     */
    private boolean tryMoveOrPrepareJump(Board board, MovingFruit fruit, Position from, Direction d) {

        if (d == null || d == Direction.NONE) return false;

        Position next = from.translated(d.getDRow(), d.getDCol());

        if (board.isInside(next) && board.isWalkable(next)) {
            fruit.setPosition(next);
            return true;
        }

        Position landing = next.translated(d.getDRow(), d.getDCol());

        if (!board.isInside(next) || !board.isInside(landing)) {
            return false;
        }

        CellType overType = board.getCellType(next);
        boolean isIce = overType == CellType.ICE_BLOCK || overType == CellType.PLAYER_ICE;
        boolean landingWalkable = board.isWalkable(landing);

        if (isIce && landingWalkable) {
            jumping = true;
            jumpOver = next;
            jumpLanding = landing;
            jumpPhase = 0;
            return true;
        }

        return false;
    }

    /**
     * Ejecuta la siguiente fase del salto actual.
     * Fase 0: La piña se posiciona sobre el bloque de hielo.
     * Fase 1: La piña aterriza en la casilla final y termina el salto.
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