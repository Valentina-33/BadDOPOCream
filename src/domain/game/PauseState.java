package domain.game;

import presentation.GamePanel;
import presentation.MenuState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Objects;

/**
 * Maneja el estado de pausado cuando el usuario oprima la tecla 'P' o ESC
 */
public class PauseState implements GameState {

    private final Game game;
    private final GameState previousState;

    private Image pauseImage;

    private final int boxWidth = 360;
    private final int boxHeight = 220;
    private final int boxX = (GamePanel.WIDTH - boxWidth) / 2;
    private final int boxY = (GamePanel.HEIGHT - boxHeight) / 2;

    public PauseState(Game game, GameState prev) {
        this.game = game;
        this.previousState = prev;
        loadAssets();
    }

    private void loadAssets() {
        try {
            pauseImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/pause-state.png"))).getImage();
        } catch (Exception e) {
            System.err.println("Error cargando imagen de victoria: " + e.getMessage());
        }
    }

    @Override
    public void update() {}

    @Override
    public void render(Graphics2D g) {
        previousState.render(g);

        if (pauseImage != null) {
            g.drawImage(pauseImage, boxX, boxY, boxWidth, boxHeight, null);
        }
    }

    @Override
    public void keyPressed(Integer key) {
        if (key == KeyEvent.VK_P) {
            game.setState(previousState);
        }

        if (key == KeyEvent.VK_ESCAPE) {
            game.setState(new MenuState(game));
        }
    }

    @Override public void keyReleased(Integer keyCode) {}
}
