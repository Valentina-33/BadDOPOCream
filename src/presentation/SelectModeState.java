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

    private final int topBoxX = 32;
    private final int topBoxY = 24;
    private final int topBoxWidth = 512;
    private final int topBoxHeight = 320;

    private final int optX = topBoxX + 40;
    private final int optY = topBoxY + 80;
    private final int optW = 120;
    private final int optH = 40;
    private final int optSpacing = 55;

    private final int icW = 220;
    private final int icH = 220;
    private final int icX = topBoxX + topBoxWidth - icW - 20;
    private final int icY = topBoxY + 80;

    private final int bottomBoxX = 32;
    private final int bottomBoxY = 360;
    private final int bottomBoxWidth = 512;
    private final int bottomBoxHeight = 160;

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

        if (backgroundGif != null) {
            g.drawImage(backgroundGif, 0, 0, GamePanel.WIDTH, GamePanel.HEIGHT, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        }

        if (playerBg != null) {
            g.drawImage(playerBg, topBoxX, topBoxY, topBoxWidth, topBoxHeight, null);
        }

        if (kindScoop != null) {
            int titleWidth = 330;
            int titleHeight = 40;
            int titleX = topBoxX + (topBoxWidth - titleWidth) / 2;
            int titleY = topBoxY + 20;
            g.drawImage(kindScoop, titleX, titleY, titleWidth, titleHeight, null);
        }

        if (P != null)   g.drawImage(P,   optX, optY, 45, optH, null);
        if (PvP != null) g.drawImage(PvP, optX, optY + optSpacing, optW, optH, null);
        if (PvM != null) g.drawImage(PvM, optX, optY + optSpacing * 2, optW, optH, null);
        if (MvM != null) g.drawImage(MvM, optX, optY + optSpacing * 3, optW, optH, null);

        if (iceCreams != null) {
            g.drawImage(iceCreams, icX, icY, icW, icH, null);
        }

        if (buttonBackBg != null) {
            g.drawImage(buttonBackBg, bottomBoxX, bottomBoxY, bottomBoxWidth, bottomBoxHeight, null);
        }

        if (backButton != null) {
            g.drawImage(backButton, backBtnX, backBtnY, backBtnWidth, backBtnHeight, null);
        }
    }

    @Override
    public void mouseClicked(Integer x, Integer y) {

        if (inside(x, y, optX, optY, optW, optH)) {
            game.setState(new ChooseFlavourState(game, 0));
            return;
        }

        if (inside(x, y, optX, optY + optSpacing, optW, optH)) {
            game.setState(new ChooseFlavourState(game, 1));
            return;
        }

        if (inside(x, y, optX, optY + optSpacing * 2, optW, optH)) {
            game.setState(new ChooseAIProfileState(game, 2));
            return;
        }

        if (inside(x, y, optX, optY + optSpacing * 3, optW, optH)) {
            game.setState(new ChooseAIProfileState(game, 3));
            return;
        }

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
