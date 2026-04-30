package persistence;

import domain.game.Game;
import domain.game.PlayingState;
import domain.game.PlayingStateBuilder; // Importamos el Builder
import java.io.*;

/**
 * Carga el estado guardado del juego desde un archivo .dat
 */
public class GameLoader {

    public static PlayingState load(Game game, File file) throws BadIceException, IOException {
        if (!file.exists()) {
            throw new BadIceException("El archivo no existe: " + file.getName());
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            GameSaver.SaveData data = (GameSaver.SaveData) ois.readObject();

            // --- CORRECCIÓN AQUÍ ---
            // Usamos el Builder para crear la instancia base del nivel guardado
            PlayingState state = new PlayingStateBuilder(game)
                    .withLevelNumber(data.levelNumber)
                    // Si tu SaveData tuviera el modo, haríamos: .withMode(data.gameMode)
                    .build();

            // Restauramos los datos dinámicos (Tiempo y Puntaje)
            state.setTimerTicks(data.timerTicks);

            // Aseguramos que el nivel tenga jugadores antes de setear score
            if (!state.getLevel().getPlayers().isEmpty()) {
                state.getLevel().getPlayers().getFirst().setScore(data.playerScore);
            }

            return state;

        } catch (ClassNotFoundException e) {
            throw new BadIceException("Formato de archivo incompatible", e);
        }
    }
}