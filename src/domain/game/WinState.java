package domain.game;

import presentation.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Objects;

import presentation.MenuState;

/**
 * Estado de victoria cuando el jugador completa el nivel.
 */
public class WinState implements GameState {

    private final Game game;
    private final GameState previousState;
    private final int levelNumber;

    private Image victoryImage;

    private final int boxWidth = 440;
    private final int boxHeight = 340;
    private final int boxX = (GamePanel.WIDTH - boxWidth) / 2;
    private final int boxY = (GamePanel.HEIGHT - boxHeight) / 2;

    public WinState(Game game, GameState prev, int levelNumber) {
        this.game = game;
        this.previousState = prev;
        this.levelNumber = levelNumber;
        loadAssets();
    }

    private void loadAssets() {
        try {
            victoryImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/victory-state.png"))).getImage();
        } catch (Exception e) {
            System.err.println("Error cargando imagen de victoria: " + e.getMessage());
        }
    }

    @Override
    public void update() {}

    @Override
    public void render(Graphics2D g) {
        previousState.render(g);

        // Dibujar la imagen de victoria centrada
        if (victoryImage != null) {
            g.drawImage(victoryImage, boxX, boxY, boxWidth, boxHeight, null);
        }
    }

    @Override
    public void keyPressed(Integer key) {
        if (key == KeyEvent.VK_N) {
            if (levelNumber == -1) return;
            game.setState(new PlayingState(game, levelNumber + 1));
        }

        if (key == KeyEvent.VK_ESCAPE) {
            game.setState(new MenuState(game));
        }
    }

    @Override public void keyReleased(Integer keyCode) {}
}