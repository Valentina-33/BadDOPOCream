package domain.game;

import domain.entities.*;
import domain.model.*;
import domain.utils.Direction;
import presentation.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
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
    private int timerTicks = 0;
    private static final int TIME_LIMIT = 10800; // 3 minutos a 60 FPS
    private boolean timeUp = false;
    private final int currentLevelNumber;

    // Sprites estáticos
    private static final Sprite FLOOR_SPRITE = new Sprite("/empty.jpg");
    private static final Sprite WALL_SPRITE = new Sprite("/wall.jpg");
    private static final Sprite RED_WALL_SPRITE = new Sprite("/red-wall.jpg");
    private static final Sprite YELLOW_WALL_SPRITE = new Sprite("/yellow-wall.jpg");
    private static final Sprite ICE_SPRITE = new Sprite("/ice.jpg");
    private static final Sprite PLAYER_ICE_SPRITE = new Sprite("/player-ice.png");
    private static final Sprite PILE_SNOW_SPRITE = new Sprite("/pile-of-snow.jpg");
    private static final Sprite IGLOO_SPRITE = new Sprite("/igloo.jpg");
    private static final Sprite HOT_TILE_SPRITE = new Sprite("/hot-tile.png");
    private static final Sprite CAMPFIRE_ON_SPRITE = new Sprite("/campfire-on.png");
    private static final Sprite CAMPFIRE_OFF_SPRITE = new Sprite("/campfire-off.png");

    // Frutas
    private static final Sprite CACTUS_SAFE_SPRITE = new Sprite("/cactus-safe.png");
    private static final Sprite CACTUS_DANGEROUS_SPRITE = new Sprite("/cactus-not-safe.png");

    // Dirección jugador 1
    private Direction p1Dir = Direction.NONE;

    /**
     * Constructor para crear un PlayingState desde un número de nivel predefinido
     */
    public PlayingState(Game game, int levelNumber) {
        this.game = game;
        this.currentLevelNumber = levelNumber;
        this.level = LevelFactory.createLevel(levelNumber);
    }

    /**
     * Constructor para crear un PlayingState desde un Level importado
     * Útil cuando se importa un nivel personalizado desde archivo .txt
     */
    public PlayingState(Game game, Level customLevel) {
        this.game = game;
        this.currentLevelNumber = -1; // -1 indica nivel personalizado
        this.level = customLevel;
    }


    @Override
    public void update() {
        // Verificar si el tiempo se acabó
        if (timeUp) {
            return; // No actualizar nada más
        }

        // Incrementar el timer
        timerTicks++;

        // Verificar si se acabó el tiempo
        if (timerTicks >= TIME_LIMIT) {
            timeUp = true;
            handleTimeUp();
            return;
        }

        // Construir el mapa de inputs para los jugadores
        Map<Player, Direction> inputs = new HashMap<>();

        // Para jugador 1
        Player p1 = level.getPlayers().getFirst();
        inputs.put(p1, p1Dir);

        // Actualizamos la lógica del nivel
        level.update(inputs);
    }

    private void handleTimeUp() {
        new Thread(() -> {
            try {
                Thread.sleep(2000);

                // Si es un nivel personalizado, no podemos reiniciarlo desde el número
                if (isCustomLevel()) {
                    // Volver al menú o mostrar mensaje
                    JOptionPane.showMessageDialog(null,
                            "Tiempo agotado en nivel personalizado",
                            "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Reiniciar nivel predefinido
                game.setState(new PlayingState(game, currentLevelNumber));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void render(Graphics2D g) {
        Board board = level.getBoard();
        int tile = GamePanel.TILE_SIZE;

        // Fondo
        g.setColor(new Color(240, 248, 255));
        g.fillRect(0, 0, board.getCols() * tile, board.getRows() * tile);

        // Dibujar tablero usando sprites
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Position p = new Position(r, c);
                CellType cell = board.getCellType(p);

                int x = c * tile;
                int y = r * tile;

                switch (cell) {
                    case METALLIC_WALL -> WALL_SPRITE.draw(g, x, y, tile, tile);

                    case RED_WALL -> RED_WALL_SPRITE.draw(g, x, y, tile, tile);

                    case YELLOW_WALL -> YELLOW_WALL_SPRITE.draw(g, x, y, tile, tile);

                    case ICE_BLOCK -> ICE_SPRITE.draw(g, x, y, tile, tile);

                    case PILE_SNOW -> PILE_SNOW_SPRITE.draw(g, x, y, tile, tile);

                    case IGLOO_AREA -> IGLOO_SPRITE.draw(g, x, y, tile, tile);

                    case HOT_TILE -> HOT_TILE_SPRITE.draw(g, x, y, tile, tile);

                    case PLAYER_ICE -> PLAYER_ICE_SPRITE.draw(g, x, y, tile, tile);

                    default -> FLOOR_SPRITE.draw(g, x, y, tile, tile);
                }
            }
        }

        // Dibujar fogatas (encima del piso)
        for (Campfire campfire : level.getCampfires()) {
            Position p = campfire.getPosition();
            int x = p.getCol() * tile;
            int y = p.getRow() * tile;

            if (campfire.isLit()) {
                // Fogata encendida con llamas dibujadas
                CAMPFIRE_ON_SPRITE.draw(g, x, y, tile, tile);
            } else {
                // Fogata apagada
                CAMPFIRE_OFF_SPRITE.draw(g, x, y, tile, tile);
            }
        }

        // Frutas
        for (Fruit f : level.getFruitManager().getActiveFruits()) {
            if (!f.isCollected()) {
                int x = f.getPosition().getCol() * tile;
                int y = f.getPosition().getRow() * tile;

                // Manejo del cactus
                if (f instanceof Cactus cactus) {
                    Sprite cactusSprite = cactus.isDangerous() ? CACTUS_DANGEROUS_SPRITE : CACTUS_SAFE_SPRITE;
                    cactusSprite.draw(g, x, y, tile, tile);
                } else {
                    // Otras frutas
                    f.render(g, tile);
                }

                // Indicador de congelado
                if (f.isFrozen()) {
                    g.setColor(new Color(100, 150, 255, 100));
                    g.fillRect(x, y, tile, tile);
                }
            }
        }

        // Jugadores
        List<Player> players = level.getPlayers();
        for (Player p : players) {
            p.render(g, tile);
        }

        //  Enemigos
        for (Enemy e : level.getEnemies()) {
            e.render(g, tile);
        }

        // Puntaje + Timer
        if (!players.isEmpty()) {
            g.setFont(new Font("Arial", Font.BOLD, 18));

            // Score
            g.setColor(Color.WHITE);
            g.drawString("Score: " + players.getFirst().getScore(), 10, 25);

            // Timer
            int remainingTicks = TIME_LIMIT - timerTicks;
            int minutes = remainingTicks / 3600;
            int seconds = (remainingTicks % 3600) / 60;

            String timeText = String.format("Time: %d:%02d", minutes, seconds);

            // Cambio de color por el paso el tiempo
            if (remainingTicks < 600) { // Menos de 10 segundos
                g.setColor(Color.RED);
            } else if (remainingTicks < 1800) { // Menos de 30 segundos
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }

            g.drawString(timeText, 200, 25);

            // Mensaje de "TIME UP!"
            if (timeUp) {

                g.setColor(new Color(0, 0, 0, 180)); // Fondo semi-transparente
                g.fillRect(0, 0, board.getCols() * tile, board.getRows() * tile);

                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 48));
                String message = "TIME UP!";
                FontMetrics fm = g.getFontMetrics();
                int x = (board.getCols() * tile - fm.stringWidth(message)) / 2;
                int y = (board.getRows() * tile) / 2;
                g.drawString(message, x, y);

                g.setFont(new Font("Arial", Font.PLAIN, 24));
                String restart = "Restarting...";
                fm = g.getFontMetrics();
                x = (board.getCols() * tile - fm.stringWidth(restart)) / 2;
                g.drawString(restart, x, y + 40);
            }
        }

    }

    private void placeOrBreakIce() {
        Player p = level.getPlayers().getFirst();
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

            // Si hay una fogata, detener el rayo
            CellType cellType = board.getCellType(current);
            if (cellType == CellType.CAMPFIRE_ON || cellType == CellType.CAMPFIRE_OFF) {
                break;
            }

            if (cellType == CellType.HOT_TILE) {
                current = current.translated(dir.getDRow(), dir.getDCol());
            }

            // Si encontramos un obstáculo, paramos
            if (CollisionDetector.isBlocked(board, current)) {
                break;
            }

            if (cellType == CellType.FLOOR) {
                board.setCellType(current, CellType.PLAYER_ICE);
            }

            // Si hay fruta en esa celda, se congela
            for (Fruit f : allFruits) {
                if (!f.isCollected() && !f.isFrozen() && sameCell(f.getPosition(), current)) {
                    f.freeze();
                }
            }
            current = current.translated(dir.getDRow(), dir.getDCol());
        }
    }

    /**
     * Maneja la extinción del fuego.
     */
    private void checkAndExtinguishCampfire(Position pos, Level level) {
        for (Campfire campfire : level.getCampfires()) {
            if (campfire.getPosition().equals(pos)) {
                campfire.extinguish(level.getBoard());
                break;
            }
        }
    }

    private void breakIceRay(Board board, Position from, Direction dir) {
        Position current = from;
        Level level = this.level;

        while (board.isInside(current)) {
            CellType cell = board.getCellType(current);

            if (cell == CellType.PLAYER_ICE || cell == CellType.ICE_BLOCK) {
                // Mira si hay una fogata en esta posición
                checkAndExtinguishCampfire(current, level);

                // Romper el hielo normalmente
                board.setCellType(current, CellType.FLOOR);
                current = current.translated(dir.getDRow(), dir.getDCol());
            } else {
                // Si ya no hay hielo, paramos
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
    public void keyPressed(Integer keyCode) {
        // Jugador 1 con flechas
        if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_P) {
            game.setState(new PauseState(game, this)); }
        if (keyCode == KeyEvent.VK_UP)    p1Dir = Direction.UP;
        if (keyCode == KeyEvent.VK_DOWN)  p1Dir = Direction.DOWN;
        if (keyCode == KeyEvent.VK_LEFT)  p1Dir = Direction.LEFT;
        if (keyCode == KeyEvent.VK_RIGHT) p1Dir = Direction.RIGHT;
        if (keyCode == KeyEvent.VK_SPACE) placeOrBreakIce();

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
    public int getTimerTicks() { return timerTicks; }
    public void setTimerTicks(int ticks) { this.timerTicks = ticks; }
    public Level getLevel() { return level; }
    public int getCurrentLevelNumber() { return this.currentLevelNumber; }
    public boolean isCustomLevel() { return this.currentLevelNumber == -1; }

}
