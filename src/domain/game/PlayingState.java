package domain.game;

import domain.entities.*;
import domain.mechanics.IceMechanic;
import domain.model.*;
import domain.utils.Direction;
import presentation.LevelRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayingState implements GameState {

    private final Game game;
    private final Level level;

    private final LevelRenderer renderer;
    private final IceMechanic iceMechanic;

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

    private Direction p1Dir = Direction.NONE;
    private Direction p2Dir = Direction.NONE;

    protected PlayingState(PlayingStateBuilder builder) {
        this.game = builder.getGame();
        this.currentLevelNumber = builder.getLevelNumber();
        this.mode = builder.getMode();

        if (builder.getCustomLevel() != null) {
            this.level = builder.getCustomLevel();
        } else {
            this.level = LevelFactory.createLevel(this.currentLevelNumber, this.mode);
        }

        this.renderer = new LevelRenderer();
        this.iceMechanic = new IceMechanic();

        this.p1Profile = builder.getP1AI();
        this.p2Profile = builder.getP2AI();
        this.flavourP1 = builder.getFlavourP1();
        this.flavourP2 = builder.getFlavourP2();

        this.aiP1 = (p1Profile != null) ? new AIController(p1Profile) : null;
        this.aiP2 = (p2Profile != null) ? new AIController(p2Profile) : null;

        applyFlavoursToPlayers();
    }

    private void applyFlavoursToPlayers() {
        List<Player> players = level.getPlayers();
        if (!players.isEmpty()) players.get(0).setFlavour(flavourP1);
        if (players.size() > 1) players.get(1).setFlavour(flavourP2);
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

        List<Player> players = level.getPlayers();
        for (Player p : players) p.update();

        Map<Player, Direction> inputs = new HashMap<>();

        if (!players.isEmpty()) {
            Player p1 = players.get(0);
            if (mode == GameMode.MVM) {
                inputs.put(p1, (aiP1 != null) ? aiP1.decide(level, p1) : Direction.NONE);
            } else {
                inputs.put(p1, p1Dir);
            }
        }

        if (players.size() > 1) {
            Player p2 = players.get(1);
            if (mode == GameMode.MVM || mode == GameMode.PVM) {
                inputs.put(p2, (aiP2 != null) ? aiP2.decide(level, p2) : Direction.NONE);
            } else {
                inputs.put(p2, p2Dir);
            }
        }

        level.update(inputs);

        checkGameStatus(players);
    }

    private void checkGameStatus(List<Player> players) {
        boolean anyAlive = false;
        boolean allDeathAnimFinished = true;

        for (Player p : players) {
            if (!p.isDead()) anyAlive = true;
            if (p.isDead() && !p.isDeathAnimationFinished()) allDeathAnimFinished = false;
        }

        if (!anyAlive && allDeathAnimFinished) {
            game.setState(new GameOverState(game, this, currentLevelNumber));
        } else if (level.isLevelCompleted()) {
            game.setState(new WinState(game, this, currentLevelNumber));
        }
    }

    private void handleTimeUp() {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                if (isCustomLevel()) {
                    JOptionPane.showMessageDialog(null, "Tiempo agotado", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    game.setState(new PlayingStateBuilder(game)
                            .withLevelNumber(currentLevelNumber)
                            .withMode(mode)
                            .withPlayer1AI(p1Profile).withPlayer2AI(p2Profile)
                            .withPlayer1Flavour(flavourP1).withPlayer2Flavour(flavourP2)
                            .build());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void render(Graphics2D g) {
        renderer.render(g, level, timerTicks, TIME_LIMIT, timeUp);
    }

    private void placeOrBreakIce(int playerIndex) {
        List<Player> players = level.getPlayers();
        if (players.size() <= playerIndex) return;

        Player p = players.get(playerIndex);
        iceMechanic.triggerIceAction(level, p);
    }

    @Override
    public void keyPressed(Integer keyCode) {
        if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_P) {
            game.setState(new PauseState(game, this));
            return;
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

    @Override
    public void mouseClicked(Integer x, Integer y) {
        if (isInside(x, y, LevelRenderer.BTN_PAUSE_X, LevelRenderer.BTN_Y, LevelRenderer.BTN_SIZE)) {
            game.setState(new PauseState(game, this));
            return;
        }

        if (isInside(x, y, LevelRenderer.BTN_RESTART_X, LevelRenderer.BTN_Y, LevelRenderer.BTN_SIZE)) {
            restartLevel();
        }
    }

    private boolean isInside(int mouseX, int mouseY, int btnX, int btnY, int size) {
        return mouseX >= btnX && mouseX <= btnX + size &&
                mouseY >= btnY && mouseY <= btnY + size;
    }

    private void restartLevel() {
        if (isCustomLevel()) {
            JOptionPane.showMessageDialog(null, "Reiniciar nivel custom...", "Info", JOptionPane.INFORMATION_MESSAGE);
            game.setState(new PlayingStateBuilder(game)
                    .withCustomLevel(this.level)
                    .withMode(mode)
                    .build());
            return;
        }

        PlayingState newState = new PlayingStateBuilder(game)
                .withLevelNumber(currentLevelNumber)
                .withMode(mode)
                .withPlayer1AI(p1Profile)
                .withPlayer2AI(p2Profile)
                .withPlayer1Flavour(flavourP1)
                .withPlayer2Flavour(flavourP2)
                .build();

        game.setState(newState);
    }

    public int getTimerTicks() { return timerTicks; }
    public void setTimerTicks(int ticks) { this.timerTicks = ticks; }
    public Level getLevel() { return level; }
    public int getCurrentLevelNumber() { return this.currentLevelNumber; }
    public boolean isCustomLevel() { return this.currentLevelNumber == -1; }
    public GameMode getMode() { return mode; }
    public AIProfile getP1Profile() { return p1Profile; }
    public AIProfile getP2Profile() { return p2Profile; }
    public Flavour getFlavourP1() { return flavourP1; }
    public Flavour getFlavourP2() { return flavourP2; }
}
