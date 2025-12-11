package persistence;

import domain.game.PlayingState;
import java.io.*;

/**
 * Guarda el estado completo del juego en un archivo .dat
 */
public class GameSaver {

    public static void save(PlayingState state, File file) throws BadIceException, IOException {
        if (state == null) {
            throw new BadIceException("No hay nivel activo para guardar");
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            // Guardar datos del nivel
            SaveData data = new SaveData();
            data.levelNumber = state.getCurrentLevelNumber();
            data.timerTicks = state.getTimerTicks();
            data.playerScore = state.getLevel().getPlayers().getFirst().getScore();
            data.playerRow = state.getLevel().getPlayers().getFirst().getPosition().getRow();
            data.playerCol = state.getLevel().getPlayers().getFirst().getPosition().getCol();

            oos.writeObject(data);
        }
    }

    /**
     * Clase interna para serializar datos del juego
     */
    static class SaveData implements Serializable {
        private static final long serialVersionUID = 1L;
        int levelNumber;
        int timerTicks;
        int playerScore;
        int playerRow;
        int playerCol;
    }
}
