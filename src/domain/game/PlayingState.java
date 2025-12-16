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

public class PlayingState implements GameState {

    private final Game game;
    private final Level level;

    private int timerTicks = 0;
    private static final int TIME_LIMIT = 10800;
    private boolean timeUp = false;

    private final int currentLevelNumber;

    private final GameMode mode;
    private final AIProfile p1Profile;
    private final AIProfile p2Profile;

    private final Flavour flavourP1;
    private final Flavour flavourP2;

    private final AIController aiP1;
    private final AIController aiP2;

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

    private static final Sprite CACTUS_SAFE_SPRITE = new Sprite("/cactus-safe.png");
    private static final Sprite CACTUS_DANGEROUS_SPRITE = new Sprite("/cactus-not-safe.png");

    private Direction p1Dir = Direction.NONE;
    private Direction p2Dir = Direction.NONE;

    public PlayingState(Game game, int levelNumber) {
        this(game, levelNumber, GameMode.PLAYER, null, null, Flavour.VANILLA, Flavour.VANILLA);
    }

    public PlayingState(Game game, int levelNumber, GameMode mode, AIProfile p1AI, AIProfile p2AI) {
        this(game, levelNumber, mode, p1AI, p2AI, Flavour.VANILLA, Flavour.VANILLA);
    }

    public PlayingState(Game game, int levelNumber, GameMode mode, AIProfile p1AI, AIProfile p2AI, Flavour flavourP1, Flavour flavourP2) {
        this.game = game;
        this.currentLevelNumber = levelNumber;

        this.mode = (mode != null) ? mode : GameMode.PLAYER;

        this.level = LevelFactory.createLevel(levelNumber, this.mode);

        this.p1Profile = p1AI;
        this.p2Profile = p2AI;

        this.flavourP1 = (flavourP1 != null) ? flavourP1 : Flavour.VANILLA;
        this.flavourP2 = (flavourP2 != null) ? flavourP2 : Flavour.VANILLA;

        this.aiP1 = (p1AI != null) ? new AIController(p1AI) : null;
        this.aiP2 = (p2AI != null) ? new AIController(p2AI) : null;

        applyFlavoursToPlayers();
    }

    public PlayingState(Game game, Level customLevel, GameMode mode, AIProfile p1AI, AIProfile p2AI, Flavour flavourP1, Flavour flavourP2) {
        this.game = game;
        this.currentLevelNumber = -1;

        this.mode = (mode != null) ? mode : GameMode.PLAYER;

        this.level = customLevel;

        this.p1Profile = p1AI;
        this.p2Profile = p2AI;

        this.flavourP1 = (flavourP1 != null) ? flavourP1 : Flavour.VANILLA;
        this.flavourP2 = (flavourP2 != null) ? flavourP2 : Flavour.VANILLA;

        this.aiP1 = (p1AI != null) ? new AIController(p1AI) : null;
        this.aiP2 = (p2AI != null) ? new AIController(p2AI) : null;

        applyFlavoursToPlayers();
    }

    public PlayingState(Game game, Level customLevel) {
        this(game, customLevel, GameMode.PLAYER, null, null, Flavour.VANILLA, Flavour.VANILLA);
    }

    private void applyFlavoursToPlayers() {
        List<Player> players = level.getPlayers();
        if (!players.isEmpty()) {
            players.get(0).setFlavour(flavourP1);
        }
        if (players.size() > 1) {
            players.get(1).setFlavour(flavourP2);
        }
    }

    @Override
    public void update() {
        if (timeUp) return;

        timerTicks++;

        if (timerTicks >= TIME_LIMIT) {
            timeUp = true;
            handleTimeUp();
            return;
        }

        // Actualizar animaciones de muerte
        List<Player> players = level.getPlayers();
        for (Player p : players) {
            p.update();
        }

        Map<Player, Direction> inputs = new HashMap<>();

        if (!players.isEmpty()) {
            Player p1 = players.get(0);

            if (mode == GameMode.MVM) {
                inputs.put(p1, aiP1 != null ? aiP1.decide(level, p1) : Direction.NONE);
            } else {
                inputs.put(p1, p1Dir);
            }
        }

        if (players.size() > 1) {
            Player p2 = players.get(1);

            if (mode == GameMode.MVM || mode == GameMode.PVM) {
                inputs.put(p2, aiP2 != null ? aiP2.decide(level, p2) : Direction.NONE);
            } else {
                inputs.put(p2, p2Dir);
            }
        }

        level.update(inputs);

        // Verificar game over
        boolean anyAlive = false;
        boolean allDeathAnimationsFinished = true;

        for (Player p : players) {
            if (!p.isDead()) {
                anyAlive = true;
            }
            // Si un jugador está muerto pero su animación NO ha terminado
            if (p.isDead() && !p.isDeathAnimationFinished()) {
                allDeathAnimationsFinished = false;
            }
        }

        // Mostrar GameOver cuando todos mueran y termine animación
        if (!anyAlive && allDeathAnimationsFinished) {
            game.setState(new GameOverState(game, this, currentLevelNumber));
            return;
        }

        if (level.isLevelCompleted()) {
            game.setState(new WinState(game, this, currentLevelNumber));
        }
    }

    private void handleTimeUp() {
        new Thread(() -> {
            try {
                Thread.sleep(2000);

                if (isCustomLevel()) {
                    JOptionPane.showMessageDialog(null,
                            "Tiempo agotado en nivel personalizado",
                            "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                game.setState(new PlayingState(game, currentLevelNumber, mode, p1Profile, p2Profile, flavourP1, flavourP2));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void render(Graphics2D g) {
        Board board = level.getBoard();
        int tile = GamePanel.TILE_SIZE;

        g.setColor(new Color(240, 248, 255));
        g.fillRect(0, 0, board.getCols() * tile, board.getRows() * tile);

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

        for (Campfire campfire : level.getCampfires()) {
            Position p = campfire.getPosition();
            if (level.getBoard().getCellType(p) == CellType.PLAYER_ICE) continue;

            int x = p.getCol() * tile;
            int y = p.getRow() * tile;

            if (campfire.isLit()) CAMPFIRE_ON_SPRITE.draw(g, x, y, tile, tile);
            else CAMPFIRE_OFF_SPRITE.draw(g, x, y, tile, tile);
        }

        for (Fruit f : level.getFruitManager().getActiveFruits()) {
            if (!f.isCollected()) {
                int x = f.getPosition().getCol() * tile;
                int y = f.getPosition().getRow() * tile;

                if (f instanceof Cactus cactus) {
                    Sprite cactusSprite = cactus.isDangerous() ? CACTUS_DANGEROUS_SPRITE : CACTUS_SAFE_SPRITE;
                    cactusSprite.draw(g, x, y, tile, tile);
                } else {
                    f.render(g, tile);
                }

                if (f.isFrozen()) {
                    g.setColor(new Color(100, 150, 255, 100));
                    g.fillRect(x, y, tile, tile);
                }
            }
        }

        List<Player> players = level.getPlayers();
        for (Player p : players) p.render(g, tile);

        for (Enemy e : level.getEnemies()) e.render(g, tile);

        if (!players.isEmpty()) {
            g.setFont(new Font("Arial", Font.BOLD, 18));

            g.setColor(Color.WHITE);
            g.drawString("P1: " + players.get(0).getScore(), 10, 25);

            if (players.size() > 1) g.drawString("P2: " + players.get(1).getScore(), 10, 50);

            int remainingTicks = TIME_LIMIT - timerTicks;
            int minutes = remainingTicks / 3600;
            int seconds = (remainingTicks % 3600) / 60;

            String timeText = String.format("Time: %d:%02d", minutes, seconds);

            if (remainingTicks < 600) g.setColor(Color.RED);
            else if (remainingTicks < 1800) g.setColor(Color.YELLOW);
            else g.setColor(Color.WHITE);

            g.drawString(timeText, 200, 25);

            if (timeUp) {
                g.setColor(new Color(0, 0, 0, 180));
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

    private void placeOrBreakIce(int playerIndex) {
        List<Player> players = level.getPlayers();
        if (players.size() <= playerIndex) return;

        Player p = players.get(playerIndex);
        Direction dir = p.getDirection();
        if (dir == null || dir == Direction.NONE) return;

        Board board = level.getBoard();
        Position start = p.getPosition();
        Position next = start.translated(dir.getDRow(), dir.getDCol());
        if (!board.isInside(next)) return;

        CellType firstCell = board.getCellType(next);

        if (firstCell == CellType.PLAYER_ICE || firstCell == CellType.ICE_BLOCK) {
            unfreezeFruitsInRay(level.getFruitManager().getAllFruits(), next, dir, board);
            breakIceRay(board, next, dir);
            return;
        }

        createIceRay(level, start, dir);
    }

    private Campfire getCampfireAt(Position pos) {
        for (Campfire c : level.getCampfires()) {
            if (sameCell(c.getPosition(), pos)) return c;
        }
        return null;
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

            Campfire cf = getCampfireAt(current);
            if (cf != null) {
                if (cf.isLit()) cf.extinguish(board);
                current = current.translated(dir.getDRow(), dir.getDCol());
                continue;
            }

            CellType cellType = board.getCellType(current);

            if (cellType == CellType.HOT_TILE) {
                current = current.translated(dir.getDRow(), dir.getDCol());
                continue;
            }

            if (CollisionDetector.isBlocked(board, current)) break;

            if (cellType == CellType.FLOOR || cellType == CellType.PILE_SNOW) {
                board.setCellType(current, CellType.PLAYER_ICE);
            }

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
            Campfire cf = getCampfireAt(current);
            if (cf != null) {
                if (cf.isLit()) cf.extinguish(board);
                current = current.translated(dir.getDRow(), dir.getDCol());
                continue;
            }

            CellType cell = board.getCellType(current);

            if (cell == CellType.PLAYER_ICE || cell == CellType.ICE_BLOCK) {
                board.setCellType(current, CellType.FLOOR);
                current = current.translated(dir.getDRow(), dir.getDCol());
            } else {
                break;
            }
        }
    }

    private void unfreezeFruitsInRay(List<Fruit> fruits, Position from, Direction dir, Board board) {
        Position current = from;

        while (board.isInside(current)) {
            CellType cell = board.getCellType(current);
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
        if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_P) {
            game.setState(new PauseState(game, this));
        }

        if (mode != GameMode.MVM) {
            if (keyCode == KeyEvent.VK_UP)    p1Dir = Direction.UP;
            if (keyCode == KeyEvent.VK_DOWN)  p1Dir = Direction.DOWN;
            if (keyCode == KeyEvent.VK_LEFT)  p1Dir = Direction.LEFT;
            if (keyCode == KeyEvent.VK_RIGHT) p1Dir = Direction.RIGHT;
            if (keyCode == KeyEvent.VK_SPACE) placeOrBreakIce(0);
        }

        if (mode == GameMode.PVP) {
            if (keyCode == KeyEvent.VK_W) p2Dir = Direction.UP;
            if (keyCode == KeyEvent.VK_S) p2Dir = Direction.DOWN;
            if (keyCode == KeyEvent.VK_A) p2Dir = Direction.LEFT;
            if (keyCode == KeyEvent.VK_D) p2Dir = Direction.RIGHT;
            if (keyCode == KeyEvent.VK_V) placeOrBreakIce(1);
        }
    }

    @Override
    public void keyReleased(Integer keyCode) {
        if (mode != GameMode.MVM) {
            if ((keyCode == KeyEvent.VK_UP && p1Dir == Direction.UP) ||
                    (keyCode == KeyEvent.VK_DOWN && p1Dir == Direction.DOWN) ||
                    (keyCode == KeyEvent.VK_LEFT && p1Dir == Direction.LEFT) ||
                    (keyCode == KeyEvent.VK_RIGHT && p1Dir == Direction.RIGHT)) {
                p1Dir = Direction.NONE;
            }
        }

        if (mode == GameMode.PVP) {
            if ((keyCode == KeyEvent.VK_W && p2Dir == Direction.UP) ||
                    (keyCode == KeyEvent.VK_S && p2Dir == Direction.DOWN) ||
                    (keyCode == KeyEvent.VK_A && p2Dir == Direction.LEFT) ||
                    (keyCode == KeyEvent.VK_D && p2Dir == Direction.RIGHT)) {
                p2Dir = Direction.NONE;
            }
        }
    }

    public int getTimerTicks() { return timerTicks; }
    public void setTimerTicks(int ticks) { this.timerTicks = ticks; }
    public Level getLevel() { return level; }
    public int getCurrentLevelNumber() { return this.currentLevelNumber; }
    public boolean isCustomLevel() { return this.currentLevelNumber == -1; }
}