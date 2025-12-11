package persistence;

import domain.game.Level;
import domain.game.LevelLoader;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Importa niveles personalizados desde archivos .txt externos
 */
public class LevelImporter {

    /**
     * Importa un nivel desde un archivo .txt externo
     * El formato debe ser igual al de los niveles internos
     */
    public static Level importFromFile(File file) throws BadIceException, IOException {
        if (!file.exists()) {
            throw new BadIceException("El archivo no existe: " + file.getName());
        }

        if (!file.getName().endsWith(".txt")) {
            throw new BadIceException("El archivo debe ser .txt");
        }

        // Validar el archivo antes de cargarlo
        validateLevelFile(file);

        // Cargar el nivel usando el LevelLoader existente
        return LevelLoader.loadFromFile(file, new ArrayList<>());
    }

    /**
     * Valida que el archivo tenga el formato correcto
     */
    private static void validateLevelFile(File file) throws BadIceException, IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line);
                }
            }
        }

        if (lines.isEmpty()) {
            throw new BadIceException("El archivo está vacío");
        }

        int expectedCols = lines.getFirst().length();
        int expectedRows = lines.size();

        // Validar que todas las filas tengan el mismo ancho
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.length() != expectedCols) {
                throw new BadIceException(
                        String.format("Error en línea %d: esperaba %d caracteres pero tiene %d",
                                i + 1, expectedCols, line.length())
                );
            }
        }

        // Validar dimensiones mínimas
        if (expectedRows < 10 || expectedCols < 10) {
            throw new BadIceException(
                    String.format("Dimensiones muy pequeñas: %dx%d (mínimo 10x10)",
                            expectedRows, expectedCols)
            );
        }

        // Validar que tenga al menos un jugador
        boolean hasPlayer = false;
        for (String line : lines) {
            if (line.contains("P")) {
                hasPlayer = true;
                break;
            }
        }

        if (!hasPlayer) {
            throw new BadIceException("El nivel debe tener al menos un jugador (P)");
        }
    }
}