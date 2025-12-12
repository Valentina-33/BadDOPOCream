package presentation;

import domain.game.Game;
import domain.game.GameState;
import domain.game.PlayingState;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Pantalla de menú principal con GIF de fondo y botón "CLICK TO LICK".
 */
public class MenuState implements GameState {

    private final Game game;

    private Image introGif;
    private Image bgButtonImage;     // Imagen del marco del botón
    private Image textButtonImage;   // Imagen del texto

    private Rectangle playButtonRect;

    private long lastBlinkTime = 0;
    private boolean textVisible = true;

    public MenuState(Game game) {
        this.game = game;

        try {
            this.introGif = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/home-animation.gif"))
            ).getImage();

            this.bgButtonImage = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/button-empty.png"))
            ).getImage();

            this.textButtonImage = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/click-button.png"))
            ).getImage();

        } catch (Exception e) {
            System.err.println("Error cargando recursos: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        long currentTime = System.currentTimeMillis();
        int BLINK_INTERVAL = 300;
        if (currentTime - lastBlinkTime > BLINK_INTERVAL) {
            textVisible = !textVisible;
            lastBlinkTime = currentTime;
        }
    }

    @Override
    public void render(Graphics2D g) {
        int width = GamePanel.WIDTH;
        int height = GamePanel.HEIGHT;

        if (introGif != null) {
            g.drawImage(introGif, 0, 0, width, height, null);
        } else {
            g.setColor(Color.MAGENTA);
            g.fillRect(0, 0, width, height);
        }

        int buttonWidth = 250;
        int btnX = (width - buttonWidth) / 2;
        int buttonHeight = 65;
        int btnY = height - buttonHeight - 50;

        playButtonRect = new Rectangle(btnX, btnY, buttonWidth, buttonHeight);

        if (bgButtonImage != null) {
            g.drawImage(bgButtonImage, btnX, btnY, buttonWidth, buttonHeight, null);
        }

        if (textButtonImage != null && textVisible) {

            int textScale = 60;
            int txtW = (buttonWidth * textScale) / 100;

            int originalW = textButtonImage.getWidth(null);
            int originalH = textButtonImage.getHeight(null);
            int txtH = (txtW * originalH) / originalW;

            int txtX = btnX + (buttonWidth - txtW) / 2;
            int txtY = btnY + (buttonHeight - txtH) / 2 + 2;

            g.drawImage(textButtonImage, txtX, txtY, txtW, txtH, null);
        }
    }

    @Override
    public void keyPressed(Integer key) {}

    @Override
    public void keyReleased(Integer key) {}

    @Override
    public void mouseClicked(Integer x, Integer y) {
        if (playButtonRect != null && playButtonRect.contains(x, y)) {
            game.setState(new PlayingState(game,2));
        }
    }
}
