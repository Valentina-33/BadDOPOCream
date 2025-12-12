package domain.behavior;

import domain.entities.Fruit;
import domain.game.Level;
import domain.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Comportamiento de la Cereza:
 * Permanece estática durante 20 segundos
 * Se teletransporta a una posición aleatoria libre
 */
public class CherryMovement {

    private static final int TICKS_PER_TELEPORT = 1200; // 20 segundos a 60 FPS (20 * 60)

    private int tickCounter = 0;
    private final Random random = new Random();

    /**
     * Actualiza el comportamiento de la cereza
     */
    public void update(Level level, Fruit cherry) {
        tickCounter++;

        if (tickCounter >= TICKS_PER_TELEPORT) {
            teleportToRandomPosition(level.getBoard(), cherry);
            tickCounter = 0;
        }
    }

    /**
     * Teletransporta la fruta a una posición aleatoria válida en el tablero
     */
    private void teleportToRandomPosition(Board board, Fruit fruit) {
        List<Position> freePositions = findAllFreePositions(board);

        // Si no hay posiciones libres, nos quedamos donde estamos
        if (freePositions.isEmpty()) {
            return;
        }

        // Elegir una posición aleatoria de las disponibles
        Position newPosition = freePositions.get(random.nextInt(freePositions.size()));
        fruit.setPosition(newPosition);
    }

    /**
     * Encuentra todas las posiciones libres (caminables) en el tablero
     */
    private List<Position> findAllFreePositions(Board board) {
        List<Position> freePositions = new ArrayList<>();

        // Recorre el tablero excluyendo los bordes (que son paredes metálicas)
        for (int row = 1; row < board.getRows() - 1; row++) {
            for (int col = 1; col < board.getCols() - 1; col++) {
                Position pos = new Position(row, col);

                // Solo agregamos posiciones que sean caminables
                if (board.isWalkable(pos)) {
                    freePositions.add(pos);
                }
            }
        }
        return freePositions;
    }

    /**
     * Ticks restantes hasta el próximo teletransporte
     */
    public int getTicksUntilTeleport() {
        return TICKS_PER_TELEPORT - tickCounter;
    }

    /**
     * Progreso hasta el próximo teletransporte de 0.0 a 1.0
     */
    public float getTeleportProgress() {
        return (float) tickCounter / TICKS_PER_TELEPORT;
    }

    /**
     * Segundos restantes hasta el próximo teletransporte
     */
    public int getSecondsUntilTeleport() {
        return (TICKS_PER_TELEPORT - tickCounter) / 60;
    }
}
