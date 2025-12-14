package presentation;

import domain.game.Game;
import domain.game.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Maneja el modo de juego que desee el usuario.
 */
public class SelectModeState implements GameState {

    private final Game game;

    // Imágenes
    private Image backgroundGif;
    private Image buttonBackBg;
    private Image backButton;
    private Image playerBg;
    private Image kindScoop;
    private Image iceCreams;
    private Image PvP;
    private Image PvM;
    private Image MvM;

    // Caja superior
    private final int topBoxX = 32;
    private final int topBoxY = 24;
    private final int topBoxWidth = 512;
    private final int topBoxHeight = 320;

    // Opciones (PvP / PvM / MvM)
    private final int optX = topBoxX + 40;
    private final int optY = topBoxY + 100;
    private final int optW = 120;
    private final int optH = 40;

    private final int optSpacing = 55; // separación entre opciones

    //  Helados (3 scoops)
    private final int icW = 220;
    private final int icH = 220;
    private final int icX = topBoxX + topBoxWidth - icW - 20;
    private final int icY = topBoxY + 80;

    // Caja inferior
    private final int bottomBoxX = 32;
    private final int bottomBoxY = 360;
    private final int bottomBoxWidth = 512;
    private final int bottomBoxHeight = 160;

    // Botón BACK
    private final int backBtnWidth = 168;
    private final int backBtnHeight = 64;
    private final int backBtnX = bottomBoxX + (bottomBoxWidth - backBtnWidth) / 2;
    private final int backBtnY = bottomBoxY + (bottomBoxHeight - backBtnHeight) / 2;


    public SelectModeState(Game game) {
        this.game = game;

        try {
            this.backgroundGif = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/home-animation.gif"))
            ).getImage();

            this.buttonBackBg = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/back-button-bg.jpg"))
            ).getImage();

            this.backButton = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/back-button.jpg"))
            ).getImage();

            this.playerBg = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/player-bg.jpg"))
            ).getImage();

            this.kindScoop = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/kind-question.png"))
            ).getImage();

            this.iceCreams = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/3-icecreams.png"))
            ).getImage();

            this.PvM = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/pvm.png"))
            ).getImage();

            this.MvM = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/mvm.png"))
            ).getImage();

            this.PvP = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/pvp.png"))
            ).getImage();

        } catch (Exception e) {
            System.err.println("Error cargando recursos: " + e.getMessage());
        }
    }


    @Override
    public void render(Graphics2D g) {

        // Fondo completo
        if (backgroundGif != null) {
            g.drawImage(backgroundGif, 0, 0, GamePanel.WIDTH, GamePanel.HEIGHT, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        }

        // Caja superior
        if (playerBg != null) {
            g.drawImage(playerBg, topBoxX, topBoxY, topBoxWidth, topBoxHeight, null);
        }

        // Título "kind of scoop?"
        if (kindScoop != null) {
            int titleWidth = 330;
            int titleHeight = 40;

            int titleX = topBoxX + (topBoxWidth - titleWidth) / 2;
            int titleY = topBoxY + 20;

            g.drawImage(kindScoop, titleX, titleY, titleWidth, titleHeight, null);
        }

        // Opciones PvP / PvM / MvM
        g.drawImage(PvP, optX, optY, optW, optH, null);
        g.drawImage(PvM, optX, optY + optSpacing, optW, optH, null);
        g.drawImage(MvM, optX, optY + optSpacing * 2, optW, optH, null);

        // Helados a la derecha
        if (iceCreams != null) {
            g.drawImage(iceCreams, icX, icY, icW, icH, null);
        }

        // Caja inferior BACK
        if (buttonBackBg != null) {
            g.drawImage(buttonBackBg, bottomBoxX, bottomBoxY, bottomBoxWidth, bottomBoxHeight, null);
        }

        // Botón BACK
        if (backButton != null) {
            g.drawImage(backButton, backBtnX, backBtnY, backBtnWidth, backBtnHeight, null);
        }
    }

    @Override
    public void mouseClicked(Integer x, Integer y) {

        // Click PvP
        if (x >= optX && x <= optX + optW &&
                y >= optY && y <= optY + optH) {
            int optionSelected = 1;
            game.setState(new ChooseFlavourState(game, optionSelected));
        }

        // Click PvM
        if (x >= optX && x <= optX + optW &&
                y >= optY + optSpacing && y <= optY + optSpacing + optH) {
            game.setState(new ChooseAIProfileState(game, 2));
        }

        // Click MvM
        if (x >= optX && x <= optX + optW &&
                y >= optY + optSpacing * 2 && y <= optY + optSpacing * 2 + optH) {
            game.setState(new ChooseAIProfileState(game, 3));
        }

        // Click BACK
        if (x >= backBtnX && x <= backBtnX + backBtnWidth &&
                y >= backBtnY && y <= backBtnY + backBtnHeight) {
            game.setState(new MenuState(game));
        }
    }

    @Override
    public void update() {}

    @Override
    public void keyPressed(Integer keyCode) {}

    @Override
    public void keyReleased(Integer keyCode) {}
}
