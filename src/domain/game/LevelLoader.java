package domain.game;

import domain.behavior.*;
import domain.entities.*;
import domain.model.*;
import domain.utils.Direction;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class LevelLoader {

    public static Level loadFromResource(String path, List<Class<? extends Fruit>> fruitPhases) {
        List<String> lines = readLines(path);

        int rows = lines.size();
        int cols = lines.get(0).length();

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

                    case 'I':
                        board.setCellType(pos, CellType.ICE_BLOCK);

                        break;

                    case 'G': // grape fruit
                        board.setCellType(pos, CellType.FLOOR);
                        fruits.add(new Grape(pos));
                        break;

                    case 'B': // banana
                        board.setCellType(pos, CellType.FLOOR);
                        fruits.add(new Banana(pos));
                        break;

                    case 'T': // enemy (troll por ahora)
                        board.setCellType(pos, CellType.FLOOR);
                        enemies.add(new Troll(
                                pos,
                                Direction.LEFT,
                                new TrollTurnRightMovement()
                        ));
                        break;

                    case 'M': // Maceta enemy
                        board.setCellType(pos, CellType.FLOOR);
                        enemies.add(new Maceta(
                                pos,
                                Direction.LEFT,
                                new MacetaChaseMovement()
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

                    case '.':
                    default:
                        board.setCellType(pos, CellType.FLOOR);
                }
            }
        }

        // Construir el nivel
        Level level = new Level(board, players, enemies, fruits, fruitPhases);
        return level;
    }

    private static List<String> readLines(String path) {
        try (InputStream is = LevelLoader.class.getResourceAsStream(path);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            if (is == null) throw new RuntimeException("Mapa no encontrado: " + path);

            List<String> lines = new ArrayList<>();
            String l;

            while ((l = br.readLine()) != null) {
                if (!l.isBlank()) lines.add(l);
            }

            return lines;

        } catch (Exception e) {
            throw new RuntimeException("Error leyendo mapa " + path, e);
        }
    }
}
