package domain.game;
import java.awt.Graphics2D;

/**
 * Maneja y monitorea las dinámicas del juego entre acciones de diferentes pantallas
 */
public class Game {
    private GameState currentState;

    public void setState(GameState state) {
        this.currentState = state;
    }
    public GameState getState() { return this.currentState; }

    public void update() {
        if(this.currentState != null) {
            this.currentState.update();
        }
    }

    public void render(Graphics2D g) {
        if(this.currentState != null) {
            this.currentState.render(g);
        }
    }

    public void keyPressed(int keyCode) {
        if(this.currentState != null) {
            this.currentState.keyPressed(keyCode);
        }
    }

    public void keyReleased(int keyCode) {
        if(this.currentState != null) {
            this.currentState.keyReleased(keyCode);
        }
    }

    public void mouseClicked(Integer x, Integer y) {
        if (this.currentState != null) {
            this.currentState.mouseClicked(x,y);
        }
    }
}
