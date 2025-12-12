package domain.game;

import presentation.GamePanel;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Maneja el estado de pausado cuando el usuario oprima la tecla 'P' o ESC
 */
public class PauseState implements GameState {

    private final Game game;
    private final GameState previousState;

    public PauseState(Game game, GameState prev) {
        this.game = game;
        this.previousState = prev;
    }

    @Override
    public void update() {}

    @Override
    public void render(Graphics2D g) {
        previousState.render(g); // pinta el juego debajo

        Integer width = GamePanel.WIDTH;
        Integer height = GamePanel.HEIGHT;

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, width, height);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("PAUSED", 150, 200);
    }

    // Cuando jugador oprima P o ESC, se pausa el juego
    @Override
    public void keyPressed(Integer key) {
        if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_P) {
            game.setState(previousState);
        }
    }

    @Override public void keyReleased(Integer keyCode) {}
}
