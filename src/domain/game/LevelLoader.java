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

    // Carga un nivel desde recursos internos
    public static Level loadFromResource(String path, List<Class<? extends Fruit>> fruitPhases) {
        List<String> lines = readLines(path);
        return parseLevel(lines, fruitPhases);
    }

    // Carga un nivel desde un archivo externo
    public static Level loadFromFile(File file, List<Class<? extends Fruit>> fruitPhases) {
        List<String> lines = readLinesFromFile(file);
        return parseLevel(lines, fruitPhases);
    }

    // Construye el nivel con sus entidades
    private static Level parseLevel(List<String> lines, List<Class<? extends Fruit>> fruitPhases) {
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

                parseCell(ch, pos, board, players, enemies, fruits, campfires);
            }
        }

        return new Level(board, players, enemies, fruits, campfires, fruitPhases);
    }

    // Construye el nivel con las letras de los mapas
    private static void parseCell(char ch, Position pos, Board board,
                                  List<Player> players, List<Enemy> enemies,
                                  List<Fruit> fruits, List<Campfire> campfires) {
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

            case 'H': // Baldosa caliente
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
                enemies.add(new Troll(pos, Direction.LEFT, new TrollTurnRightMovement()));
                break;

            case 'M': // Maceta
                board.setCellType(pos, CellType.FLOOR);
                enemies.add(new Maceta(pos, Direction.LEFT, new MacetaChaseMovement()));
                break;

            case 'O': // Orange Squid - calamar naranja
                board.setCellType(pos, CellType.FLOOR);
                enemies.add(new OrangeSquid(pos, Direction.DOWN, new OrangeSquidMovement()));
                break;

            case 'V': // Narval
                board.setCellType(pos, CellType.FLOOR);
                enemies.add(new Narval(pos, Direction.DOWN, new NarvalMovement()));
                break;

            case 'N': // Pineapple
                board.setCellType(pos, CellType.FLOOR);
                fruits.add(new Pineapple(pos));
                break;

            case 'P': // player 1
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
                campfires.add(new Campfire(pos));
                break;

            case '.':
            default:
                board.setCellType(pos, CellType.FLOOR);
        }
    }

    // Lee líneas desde recursos internos
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

    // Lee líneas desde archivo externo
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