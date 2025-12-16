package domain.game;

import presentation.GamePanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Objects;

import presentation.MenuState;

import javax.swing.*;

/**
 * Estado de derrota (Game Over) cuando el jugador muere o se acaba el tiempo.
 */
public class GameOverState implements GameState {

    private final Game game;
    private final GameState previousState;
    private final int levelNumber;

    private Image gameOverImage;

    private final int boxWidth = 440;
    private final int boxHeight = 340;
    private final int boxX = (GamePanel.WIDTH - boxWidth) / 2;
    private final int boxY = (GamePanel.HEIGHT - boxHeight) / 2;

    public GameOverState(Game game, GameState prev, int levelNumber) {
        this.game = game;
        this.previousState = prev;
        this.levelNumber = levelNumber;
        loadAssets();
    }

    private void loadAssets() {
        try {
            gameOverImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/game-over-state.png"))).getImage();
        } catch (Exception e) {
            System.err.println("Error cargando imagen de pérdida: " + e.getMessage());
        }
    }

    @Override
    public void update() {}

    @Override
    public void render(Graphics2D g) {
        previousState.render(g);

        if (gameOverImage != null) {
            g.drawImage(gameOverImage, boxX, boxY, boxWidth, boxHeight, null);
        }
    }

    @Override
    public void keyPressed(Integer key) {
        if (key == KeyEvent.VK_R) {
            if (levelNumber == -1) return; // nivel importado
            game.setState(new PlayingState(game, levelNumber));
        }

        if (key == KeyEvent.VK_ESCAPE) {
            game.setState(new MenuState(game));
        }
    }

    @Override public void keyReleased(Integer keyCode) {}
}
