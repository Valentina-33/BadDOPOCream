package domain.game;

import presentation.GamePanel;
import presentation.MenuState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class WinState implements GameState {

    private final Game game;
    private final GameState previousState;
    private final int levelNumber;

    private Image victoryImage;
    // ... (tus variables de dimensiones boxWidth, etc. siguen igual)
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
            System.err.println("Error cargando imagen: " + e.getMessage());
        }
    }

    @Override
    public void update() {}

    @Override
    public void render(Graphics2D g) {
        if (previousState != null) previousState.render(g);
        if (victoryImage != null) {
            g.drawImage(victoryImage, boxX, boxY, boxWidth, boxHeight, null);
        }
    }

    @Override
    public void keyPressed(Integer key) {

        // TECLA N: SIGUIENTE NIVEL
        if (key == KeyEvent.VK_N) {
            if (levelNumber == -1) return; // Niveles importados no tienen "siguiente"

            // 1. Preparamos el Builder para el Nivel + 1
            PlayingStateBuilder builder = new PlayingStateBuilder(game)
                    .withLevelNumber(levelNumber + 1);

            // 2. ¡AQUÍ ESTÁ EL TRUCO!
            // Recuperamos la configuración del estado anterior (el nivel que acabas de ganar)
            if (previousState instanceof PlayingState oldState) {

                // Le pasamos al nuevo nivel el MISMO modo, IAs y Sabores
                builder.withMode(oldState.getMode())
                        .withPlayer1AI(oldState.getP1Profile())
                        .withPlayer2AI(oldState.getP2Profile())
                        .withPlayer1Flavour(oldState.getFlavourP1())
                        .withPlayer2Flavour(oldState.getFlavourP2());

                // (Opcional) Si quieres mantener el puntaje acumulado:
                // Esto requeriría que el Builder acepte puntaje inicial, o setearlo después de build()
            }

            // 3. Construimos y cambiamos
            game.setState(builder.build());
        }

        if (key == KeyEvent.VK_ESCAPE) {
            game.setState(new MenuState(game));
        }
    }

    @Override public void keyReleased(Integer keyCode) {}
}