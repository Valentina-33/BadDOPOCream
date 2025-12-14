package domain.game;

import domain.behavior.*;
import domain.entities.*;
import domain.model.*;
import domain.utils.Direction;

import java.io.*;
import java.util.*;

/**
 * Se encarga de leer los mapas e identifica las entidades en ella.
 */
public class LevelLoader {

    // Crea una lista de las clases de frutas
    public static Level loadFromResource(String path, List<Class<? extends Fruit>> fruitPhases) {
        List<String> lines = readLines(path);
        List<Campfire> campfires = new ArrayList<>();

        int rows = lines.size();
        int cols = lines.getFirst().length();

        Board board = new Board(rows, cols);
        List<Player> players = new ArrayList<>();
        List<Enemy> enemies = new ArrayList<>();
        List<Fruit> fruits = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            String line = lines.get(r);
            for (int c = 0; c < cols; c++) {

                char ch = line.charAt(c);
                Position pos = new Position(r, c);

                switch (ch) {

                    case 'W':
                        board.setCellType(pos, CellType.METALLIC_WALL);
                        break;

                    case 'Z':
                        board.setCellType(pos, CellType.PILE_SNOW);
                        break;

                    case 'R':
                        board.setCellType(pos, CellType.RED_WALL);
                        break;

                    case 'X':
                        board.setCellType(pos, CellType.YELLOW_WALL);
                        break;

                    case 'I':
                        board.setCellType(pos, CellType.ICE_BLOCK);
                        break;

                    case 'L':
                        board.setCellType(pos, CellType.IGLOO_AREA);
                        break;

                    case 'H': // Baldose caliente
                        board.setCellType(pos, CellType.HOT_TILE);
                        break;

                    case 'G': // Uvas - grape
                        board.setCellType(pos, CellType.FLOOR);
                        fruits.add(new Grape(pos));
                        break;

                    case 'B': // Banana
                        board.setCellType(pos, CellType.FLOOR);
                        fruits.add(new Banana(pos));
                        break;

                    case 'T': // Troll
                        board.setCellType(pos, CellType.FLOOR);
                        enemies.add(new Troll(
                                pos,
                                Direction.LEFT,
                                new TrollTurnRightMovement()
                        ));
                        break;

                    case 'M': // Maceta
                        board.setCellType(pos, CellType.FLOOR);
                        enemies.add(new Maceta(
                                pos,
                                Direction.LEFT,
                                new MacetaChaseMovement()
                        ));
                        break;

                    case 'O': // Orange Squid - calamar naranja
                        board.setCellType(pos, CellType.FLOOR);
                        enemies.add(new OrangeSquid(
                                pos,
                                Direction.DOWN,
                                new OrangeSquidMovement()
                        ));
                        break;

                    case 'V': // Narval
                        board.setCellType(pos, CellType.FLOOR);
                        enemies.add(new Narval(
                                pos,
                                Direction.DOWN,
                                new NarvalMovement()
                        ));
                        break;

                    case 'N': // Pineapple
                        board.setCellType(pos, CellType.FLOOR);
                        fruits.add(new Pineapple(pos));
                        break;

                    case 'P': // player
                        board.setCellType(pos, CellType.FLOOR);
                        players.add(new Player(pos));
                        break;

                    case 'Q': // player 2
                        board.setCellType(pos, CellType.FLOOR);
                        players.add(new Player(pos));
                        break;

                    case 'C': // Cactus
                        board.setCellType(pos, CellType.FLOOR);
                        fruits.add(new Cactus(pos));
                        break;

                    case 'Y': // Cherry
                        board.setCellType(pos, CellType.FLOOR);
                        fruits.add(new Cherry(pos));
                        break;

                    case 'F': // Fogata (Fire/Campfire)
                        board.setCellType(pos, CellType.CAMPFIRE_ON);
                        Campfire campfire = new Campfire(pos);
                        campfires.add(campfire);
                        break;

                    case '.':
                    default:
                        board.setCellType(pos, CellType.FLOOR);
                }
            }
        }

        // Construir el nivel
        return new Level(board, players, enemies, fruits, campfires, fruitPhases);
    }

    private static List<String> readLines(String path) {
        try (InputStream is = LevelLoader.class.getResourceAsStream(path)) {
            assert is != null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

                List<String> lines = new ArrayList<>();
                String l;

                while ((l = br.readLine()) != null) {
                    if (!l.isBlank()) lines.add(l);
                }

                return lines;

            }
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo mapa " + path, e);
        }
    }

    // Para la opción de importar archivos .txt
    public static Level loadFromFile(File file, List<Class<? extends Fruit>> fruitPhases) {
        List<String> lines = readLinesFromFile(file);
        List<Campfire> campfires = new ArrayList<>();

        int rows = lines.size();
        int cols = lines.getFirst().length();

        Board board = new Board(rows, cols);
        List<Player> players = new ArrayList<>();
        List<Enemy> enemies = new ArrayList<>();
        List<Fruit> fruits = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            String line = lines.get(r);
            for (int c = 0; c < cols; c++) {

                char ch = line.charAt(c);
                Position pos = new Position(r, c);

                switch (ch) {

                    case 'W':
                        board.setCellType(pos, CellType.METALLIC_WALL);
                        break;

                    case 'Z':
                        board.setCellType(pos, CellType.PILE_SNOW);
                        break;

                    case 'R':
                        board.setCellType(pos, CellType.RED_WALL);
                        break;

                    case 'X':
                        board.setCellType(pos, CellType.YELLOW_WALL);
                        break;

                    case 'I':
                        board.setCellType(pos, CellType.ICE_BLOCK);
                        break;

                    case 'L':
                        board.setCellType(pos, CellType.IGLOO_AREA);
                        break;

                    case 'H':
                        board.setCellType(pos, CellType.HOT_TILE);
                        break;

                    case 'G':
                        board.setCellType(pos, CellType.FLOOR);
                        fruits.add(new Grape(pos));
                        break;

                    case 'B':
                        board.setCellType(pos, CellType.FLOOR);
                        fruits.add(new Banana(pos));
                        break;

                    case 'T':
                        board.setCellType(pos, CellType.FLOOR);
                        enemies.add(new Troll(pos, Direction.LEFT, new TrollTurnRightMovement()));
                        break;

                    case 'M':
                        board.setCellType(pos, CellType.FLOOR);
                        enemies.add(new Maceta(pos, Direction.LEFT, new MacetaChaseMovement()));
                        break;

                    case 'O':
                        board.setCellType(pos, CellType.FLOOR);
                        enemies.add(new OrangeSquid(pos, Direction.DOWN, new OrangeSquidMovement()));
                        break;

                    case 'V':
                        board.setCellType(pos, CellType.FLOOR);
                        enemies.add(new Narval(pos, Direction.DOWN, new NarvalMovement()));
                        break;

                    case 'N':
                        board.setCellType(pos, CellType.FLOOR);
                        fruits.add(new Pineapple(pos));
                        break;

                    case 'P':
                        board.setCellType(pos, CellType.FLOOR);
                        players.add(new Player(pos));
                        break;

                    case 'Q':
                        board.setCellType(pos, CellType.FLOOR);
                        players.add(new Player(pos));
                        break;

                    case 'C':
                        board.setCellType(pos, CellType.FLOOR);
                        fruits.add(new Cactus(pos));
                        break;

                    case 'Y':
                        board.setCellType(pos, CellType.FLOOR);
                        fruits.add(new Cherry(pos));
                        break;

                    case 'F':
                        board.setCellType(pos, CellType.CAMPFIRE_ON);
                        Campfire campfire = new Campfire(pos);
                        campfires.add(campfire);
                        break;

                    case '.':
                    default:
                        board.setCellType(pos, CellType.FLOOR);
                }
            }
        }
        return new Level(board, players, enemies, fruits, campfires, fruitPhases);
    }

    // helper para leer lineas de archivos .txt
    private static List<String> readLinesFromFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo mapa desde archivo: " + file.getName(), e);
        }
    }
}
