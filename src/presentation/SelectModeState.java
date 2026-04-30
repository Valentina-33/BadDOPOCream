package presentation;

import domain.game.Game;
import domain.game.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class SelectModeState implements GameState {

    private final Game game;

    private Image backgroundGif;
    private Image buttonBackBg;
    private Image backButton;
    private Image playerBg;
    private Image kindScoop;
    private Image iceCreams;

    private Image P;
    private Image PvP;
    private Image PvM;
    private Image MvM;

    // Cuadro principal (superior)
    private final int topBoxX = 32;
    private final int topBoxY = 24;
    private final int topBoxWidth = 512;
    private final int topBoxHeight = 320;

    // Helados a la derecha
    private final int icW = 220;
    private final int icH = 220;
    private final int icX = topBoxX + topBoxWidth - icW - 38;
    private final int icY = topBoxY + 80;

    // Cuadro inferior (tiene el botón para devolver)
    private final int bottomBoxX = 32;
    private final int bottomBoxY = 360;
    private final int bottomBoxWidth = 512;
    private final int bottomBoxHeight = 160;

    // Botón BACK
    private final int backBtnWidth = 168;
    private final int backBtnHeight = 64;
    private final int backBtnX = bottomBoxX + (bottomBoxWidth - backBtnWidth) / 2;
    private final int backBtnY = bottomBoxY + (bottomBoxHeight - backBtnHeight) / 2;

    // Para posicionar los botones de modalidad
    private int optionsX;
    private int optionsY;
    private int optionWidth;
    private int optionHeight;
    private int optionSpacing;

    /**
     * Contiene l
     * @param game
     */
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

            this.P = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/P.png"))
            ).getImage();

            this.PvP = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/pvp.png"))
            ).getImage();

            this.PvM = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/pvm.png"))
            ).getImage();

            this.MvM = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/mvm.png"))
            ).getImage();

        } catch (Exception e) {
            System.err.println("Error cargando recursos: " + e.getMessage());
        }
    }

    @Override
    public void render(Graphics2D g) {

        // Fondo
        if (backgroundGif != null) {
            g.drawImage(backgroundGif, 0, 0, GamePanel.WIDTH, GamePanel.HEIGHT, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        }

        //  Parte superior (marco)
        if (playerBg != null) {
            g.drawImage(playerBg, topBoxX, topBoxY, topBoxWidth, topBoxHeight, null);
        }

        // Título centrado
        if (kindScoop != null) {
            int titleWidth = 320;
            int titleHeight = 38;
            int titleX = topBoxX + (topBoxWidth - titleWidth) / 2;
            int titleY = topBoxY + 25;
            g.drawImage(kindScoop, titleX, titleY, titleWidth, titleHeight, null);
        }

        // Tamaños generales para las opciones
        optionWidth = 95;
        optionHeight = 38;
        optionSpacing = 48;

        // Ancho para P
        int pWidth = 38;
        int pHeight = 42;

        // Espacio para el título
        int titleOffset = 50;

        // Imágenes de opciones
        Image[] options = { P, PvP, PvM, MvM };
        int optionCount = options.length;

        // Altura total del bloque
        int optionsBlockHeight = optionCount * optionHeight + (optionCount - 1) * (optionSpacing - optionHeight);
        optionsX = topBoxX + 80;
        optionsY = topBoxY + titleOffset + (topBoxHeight - titleOffset - optionsBlockHeight) / 2;

        // Posicionando las opciones
        for (int i = 0; i < optionCount; i++) {

            int y = optionsY + i * optionSpacing;

            int drawWidth = optionWidth;
            int drawHeight = optionHeight;
            int drawX = optionsX;

            // Caso especial: opción P
            if (i == 0) {
                drawWidth = pWidth;
                drawHeight = pHeight;
                drawX = optionsX + (optionWidth - pWidth) / 2 - 30;
            }

            if (options[i] != null) {
                g.drawImage(options[i], drawX, y, drawWidth, drawHeight, null);
            }
        }


        // Helados hacia la parte derecha
        if (iceCreams != null) {
            g.drawImage(iceCreams, icX, icY, icW, icH, null);
        }

        // Parte inferior dibuja el botón BACK
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

        // Click en opciones de modalidad
        for (int i = 0; i < 4; i++) {
            int optY = optionsY + i * optionSpacing;

            if (inside(x, y, optionsX, optY, optionWidth, optionHeight)) {
                switch (i) {
                    case 0 -> game.setState(new ChooseFlavourState(game, 0));       // P
                    case 1 -> game.setState(new ChooseFlavourState(game, 1));       // PvP
                    case 2 -> game.setState(new ChooseAIProfileState(game, 2));     // PvM
                    case 3 -> game.setState(new ChooseAIProfileState(game, 3));     // MvM
                }
                return;
            }
        }

        // Click en BACK
        if (inside(x, y, backBtnX, backBtnY, backBtnWidth, backBtnHeight)) {
            game.setState(new MenuState(game));
        }
    }

    private boolean inside(int x, int y, int bx, int by, int bw, int bh) {
        return x >= bx && x <= bx + bw && y >= by && y <= by + bh;
    }

    @Override public void update() {}
    @Override public void keyPressed(Integer keyCode) {}
    @Override public void keyReleased(Integer keyCode) {}
}