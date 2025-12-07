package domain.game;

import java.awt.Graphics2D;

/**
 * Interfaz que establece los mínimos que debe tener cada uno de los estados del juego.
 */
public interface GameState {
    void update();
    void render(Graphics2D g);
    void keyPressed(Integer keyCode);
    void keyReleased(Integer keyCode);
    default void mouseClicked(Integer x, Integer y) {}
}
