package domain.game;

import presentation.GamePanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import presentation.MenuState;

/**
 * Estado de derrota (Game Over) cuando el jugador muere o se acaba el tiempo.
 */
public class GameOverState implements GameState {

    private final Game game;
    private final GameState previousState;
    private final int levelNumber;

    public GameOverState(Game game, GameState prev, int levelNumber) {
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

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, width, height);

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 42));
        g.drawString("GAME OVER", 120, 210);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Press R to restart", 150, 260);
        g.drawString("Press ESC to go to menu", 130, 290);
    }

    @Override
    public void keyPressed(Integer key) {
        if (key == KeyEvent.VK_R) {
            if (levelNumber == -1) return; // nivel importado, aquí no sabemos reiniciarlo por número
            game.setState(new PlayingState(game, levelNumber));
        }

        if (key == KeyEvent.VK_ESCAPE) {
            game.setState(new MenuState(game));
        }
    }

    @Override public void keyReleased(Integer keyCode) {}
}
