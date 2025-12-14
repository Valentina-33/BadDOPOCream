package domain.game;

import presentation.GamePanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import presentation.MenuState;

/**
 * Estado de victoria cuando el jugador completa el nivel.
 */
public class WinState implements GameState {

    private final Game game;
    private final GameState previousState;
    private final int levelNumber;

    public WinState(Game game, GameState prev, int levelNumber) {
        this.game = game;
        this.previousState = prev;
        this.levelNumber = levelNumber;
    }

    @Override
    public void update() {}

    @Override
    public void render(Graphics2D g) {
        previousState.render(g); // pinta el juego debajo

        Integer width = GamePanel.WIDTH;
        Integer height = GamePanel.HEIGHT;

        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, width, height);

        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 42));
        g.drawString("YOU WIN!", 145, 210);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Press N for next level", 135, 260);
        g.drawString("Press ESC to go to menu", 130, 290);
    }

    @Override
    public void keyPressed(Integer key) {
        if (key == KeyEvent.VK_N) {
            if (levelNumber == -1) return; // nivel importado, no hay siguiente definido
            game.setState(new PlayingState(game, levelNumber + 1));
        }

        if (key == KeyEvent.VK_ESCAPE) {
            game.setState(new MenuState(game));
        }
    }

    @Override public void keyReleased(Integer keyCode) {}
}
