package domain.game;

import domain.behavior.TrollTurnRightMovement;
import domain.entities.Enemy;
import domain.entities.Player;
import domain.entities.Troll;
import domain.model.Board;
import domain.model.CellType;
import domain.model.Position;
import domain.utils.Direction;
import presentation.GamePanel;
import domain.entities.Fruit;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static domain.game.CollisionDetector.sameCell;

/**
 * Estado de juego activo:
 * contiene un Level y maneja el movimiento de jugadores y enemigos.
 */
public class PlayingState implements GameState {

    private final Game game;
    private final Level level;

    //Dirección actual del jugador 1 (más adelante añadimos jugador 2)
    private Direction p1Dir = Direction.NONE;

    public PlayingState(Game game, int levelNumber) {
        this.game = game;

        //Crear tablero según el tamaño lógico del GamePanel
        Board board = new Board(GamePanel.ROWS, GamePanel.COLS);

        //Crear el Level
        this.level = LevelFactory.createLevel(levelNumber);
    }

    @Override
    public void update() {
        //Construimos el mapa de inputs para los jugadores
        Map<Player, Direction> inputs = new HashMap<>();

        //Por ahora solo hay un jugador
        Player p1 = level.getPlayers().get(0);
        inputs.put(p1, p1Dir);

        // Actualizamos la lógica del nivel
        level.update(inputs);
    }

    @Override
    public void render(Graphics2D g) {
        Board board = level.getBoard();
        int tile = GamePanel.TILE_SIZE;

        // Fondo
        g.setColor(new Color(20, 20, 40));
        g.fillRect(0, 0, board.getCols() * tile, board.getRows() * tile);

        // ===== Dibujar tablero (paredes + hielo + piso) =====
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Position p = new Position(r, c);
                CellType cell = board.getCellType(p);

                int x = c * tile;
                int y = r * tile;

                switch (cell) {
                    case METALLIC_WALL -> {
                        g.setColor(new Color(70, 70, 90));
                        g.fillRect(x, y, tile, tile);
                    }
                    case ICE_BLOCK -> {
                        // Aquí luego puedes usar sprite de hielo
                        g.setColor(new Color(100, 170, 220));
                        g.fillRect(x, y, tile, tile);
                    }
                    case PLAYER_ICE -> {
                        g.setColor(new Color(128,128,128 ));
                    }
                    default -> {
                        // Piso normal
                        g.setColor(new Color(255, 255, 255));
                        g.fillRect(x, y, tile, tile);
                    }
                }
            }
        }

        // ===== Dibujar frutas =====
        for (Fruit f : level.getFruitManager().getActiveFruits()) {
            if (!f.isCollected()) {
                f.render(g, tile);
            }
        }

        // ===== Dibujar jugadores =====
        List<Player> players = level.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            int x = p.getPosition().getCol() * tile;
            int y = p.getPosition().getRow() * tile;

            g.setColor(i == 0 ? Color.CYAN : Color.GREEN);
            g.fillOval(x + 4, y + 4, tile - 8, tile - 8);
        }

        // ===== Dibujar enemigos (Trolls) =====
        g.setColor(Color.RED);
        for (Enemy e : level.getEnemies()) {
            int x = e.getPosition().getCol() * tile;
            int y = e.getPosition().getRow() * tile;
            g.fillOval(x + 4, y + 4, tile - 8, tile - 8);
        }
    }


    @Override
    public void keyPressed(Integer keyCode) {
        // Jugador 1 con flechas
        if (keyCode == KeyEvent.VK_UP)    p1Dir = Direction.UP;
        if (keyCode == KeyEvent.VK_DOWN)  p1Dir = Direction.DOWN;
        if (keyCode == KeyEvent.VK_LEFT)  p1Dir = Direction.LEFT;
        if (keyCode == KeyEvent.VK_RIGHT) p1Dir = Direction.RIGHT;
        if (keyCode == KeyEvent.VK_SPACE) placeOrBreakIce();

        // luego puedes añadir:
        // WASD para jugador 2, y mapear a otra Direction p2Dir
    }

    private void placeOrBreakIce() {
        Player p = level.getPlayers().get(0);
        Direction dir = p.getDirection();

        if (dir == null || dir == Direction.NONE) return;

        Board board = level.getBoard();
        Position start = p.getPosition();
        Position next = start.translated(dir.getDRow(), dir.getDCol());

        if (!board.isInside(next)) return;

        CellType firstCell = board.getCellType(next);

        // Si hay una hilera de hielo del jugador, primero descongelamos, luego rompemos
        if (firstCell == CellType.PLAYER_ICE || firstCell == CellType.ICE_BLOCK) {
            unfreezeFruitsInRay(level.getFruitManager().getAllFruits(), next, dir, board);
            breakIceRay(board, next, dir);
            return;
        }

        // Si no, creamos el rayo
        createIceRay(level, start, dir);
    }


    private void createIceRay(Level level, Position from, Direction dir) {
        Board board = level.getBoard();
        List<Fruit> allFruits = level.getFruitManager().getAllFruits();
        List<Enemy> enemies = level.getEnemies();

        Position current = from.translated(dir.getDRow(), dir.getDCol());

        while (board.isInside(current)) {
            boolean enemyThere = false;

            for (Enemy e : enemies) {
                if (sameCell(e.getPosition(), current)) {
                    enemyThere = true;
                    break;
                }
            }
            if (enemyThere) break;

            //Si encontramos un obstáculo (pared, hielo, etc.), paramos
            if (CollisionDetector.isBlocked(board, current)) {
                break;
            }
            if (board.getCellType(current) == CellType.FLOOR) {
                board.setCellType(current, CellType.PLAYER_ICE);
            }

            // i hay fruta en esa celda, la congelamos
            for (Fruit f : allFruits) {
                if (!f.isCollected() && !f.isFrozen() && sameCell(f.getPosition(), current)) {
                    f.freeze();
                }
            }

            current = current.translated(dir.getDRow(), dir.getDCol());
        }
    }

    private void breakIceRay(Board board, Position from, Direction dir) {
        Position current = from;

        while (board.isInside(current)) {
            CellType cell = board.getCellType(current);

            if (cell == CellType.PLAYER_ICE || cell == CellType.ICE_BLOCK) {
                board.setCellType(current, CellType.FLOOR);
                current = current.translated(dir.getDRow(), dir.getDCol());
            } else {
                // Si ya no hay hielo del jugador, paramos
                break;
            }
        }
    }


    private void unfreezeFruitsInRay(List<Fruit> fruits, Position from, Direction dir, Board board) {

        Position current = from;

        while (board.isInside(current)) {
            CellType cell = board.getCellType(current);

            // Solo recorremos mientras haya hielo del jugador
            if (cell != CellType.PLAYER_ICE) break;

            for (Fruit f : fruits) {
                if (f.isFrozen() && sameCell(f.getPosition(), current)) {
                    f.unfreeze();
                }
            }

            current = current.translated(dir.getDRow(), dir.getDCol());
        }
    }




    @Override
    public void keyReleased(Integer keyCode) {
        // Si sueltas una tecla que corresponde a la dirección actual, paramos
        if ((keyCode == KeyEvent.VK_UP    && p1Dir == Direction.UP) ||
                (keyCode == KeyEvent.VK_DOWN  && p1Dir == Direction.DOWN) ||
                (keyCode == KeyEvent.VK_LEFT  && p1Dir == Direction.LEFT) ||
                (keyCode == KeyEvent.VK_RIGHT && p1Dir == Direction.RIGHT)) {
            p1Dir = Direction.NONE;
        }
    }

    public Level getLevel() {
        return level;
    }
}
