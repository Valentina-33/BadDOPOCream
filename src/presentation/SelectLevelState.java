package presentation;

import domain.game.*;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class SelectLevelState implements GameState {

    private final Game game;
    private final int gameMode; // 0=PLAYER, 1=PvP, 2=PvM, 3=MvM

    private final AIProfile aiProfileP1;
    private final AIProfile aiProfileP2;

    private final Flavour flavourP1;
    private final Flavour flavourP2;

    private Image backgroundGif;
    private Image buttonBackBg;
    private Image backButton;
    private Image playerBg;
    private Image levelSelect;
    private Image levelOne;
    private Image levelTwo;
    private Image levelThree;

    private final int topBoxX = 32;
    private final int topBoxY = 24;
    private final int topBoxWidth = 512;
    private final int topBoxHeight = 320;

    private final int bottomBoxX = 32;
    private final int bottomBoxY = 360;
    private final int bottomBoxWidth = 512;
    private final int bottomBoxHeight = 160;

    private final int backBtnWidth = 168;
    private final int backBtnHeight = 64;
    private final int backBtnX = bottomBoxX + (bottomBoxWidth - backBtnWidth) / 2;
    private final int backBtnY = bottomBoxY + (bottomBoxHeight - backBtnHeight) / 2;

    private final int padding = 20;
    private final int levelBoxWidth = 114;
    private final int levelBoxHeight = 112;

    private final int levelBoxY = topBoxY + (topBoxHeight - levelBoxHeight) / 2;

    private final int level1BoxX = topBoxX + padding + 50;
    private final int level2BoxX = topBoxX + padding + levelBoxWidth + padding + 50;
    private final int level3BoxX = topBoxX + padding + (levelBoxWidth + padding) * 2 + 50;

    public SelectLevelState(Game game, int gameMode, AIProfile aiProfileP1, AIProfile aiProfileP2, Flavour flavourP1, Flavour flavourP2) {
        this.game = game;
        this.gameMode = gameMode;
        this.aiProfileP1 = aiProfileP1;
        this.aiProfileP2 = aiProfileP2;
        this.flavourP1 = flavourP1;
        this.flavourP2 = flavourP2;
        loadAssets();
    }

    private void loadAssets() {
        try {
            this.backgroundGif = new ImageIcon(Objects.requireNonNull(getClass().getResource("/home-animation.gif"))).getImage();
            this.buttonBackBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/back-button-bg.jpg"))).getImage();
            this.backButton = new ImageIcon(Objects.requireNonNull(getClass().getResource("/back-button.jpg"))).getImage();
            this.playerBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/player-bg.jpg"))).getImage();
            this.levelSelect = new ImageIcon(Objects.requireNonNull(getClass().getResource("/level-select.png"))).getImage();
            this.levelOne = new ImageIcon(Objects.requireNonNull(getClass().getResource("/level-1.png"))).getImage();
            this.levelTwo = new ImageIcon(Objects.requireNonNull(getClass().getResource("/level-2.png"))).getImage();
            this.levelThree = new ImageIcon(Objects.requireNonNull(getClass().getResource("/level-3.png"))).getImage();
        } catch (Exception e) {
            System.err.println("Error cargando recursos: " + e.getMessage());
        }
    }

    @Override
    public void render(Graphics2D g) {
        int width = GamePanel.WIDTH;
        int height = GamePanel.HEIGHT;

        if (backgroundGif != null) g.drawImage(backgroundGif, 0, 0, width, height, null);
        else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
        }

        if (playerBg != null) g.drawImage(playerBg, topBoxX, topBoxY, topBoxWidth, topBoxHeight, null);

        if (levelSelect != null) {
            int titleWidth = 330;
            int titleHeight = 40;
            int titleX = topBoxX + (topBoxWidth - titleWidth) / 2;
            int titleY = topBoxY + 20;
            g.drawImage(levelSelect, titleX, titleY, titleWidth, titleHeight, null);
        }

        if (levelOne != null) g.drawImage(levelOne, level1BoxX, levelBoxY, levelBoxWidth, levelBoxHeight, null);
        if (levelTwo != null) g.drawImage(levelTwo, level2BoxX, levelBoxY, levelBoxWidth, levelBoxHeight, null);
        if (levelThree != null) g.drawImage(levelThree, level3BoxX, levelBoxY, levelBoxWidth, levelBoxHeight, null);

        if (buttonBackBg != null) g.drawImage(buttonBackBg, bottomBoxX, bottomBoxY, bottomBoxWidth, bottomBoxHeight, null);
        if (backButton != null) g.drawImage(backButton, backBtnX, backBtnY, backBtnWidth, backBtnHeight, null);
    }

    @Override
    public void mouseClicked(Integer x, Integer y) {

        if (x >= backBtnX && x <= backBtnX + backBtnWidth &&
                y >= backBtnY && y <= backBtnY + backBtnHeight) {

            if (gameMode == 2 || gameMode == 3) {
                game.setState(new ChooseFlavourState(game, gameMode, aiProfileP1, aiProfileP2));
            } else {
                game.setState(new ChooseFlavourState(game, gameMode));
            }
            return;
        }

        if (x >= level1BoxX && x <= level1BoxX + levelBoxWidth &&
                y >= levelBoxY && y <= levelBoxY + levelBoxHeight) {
            startLevel(1);
            return;
        }

        if (x >= level2BoxX && x <= level2BoxX + levelBoxWidth &&
                y >= levelBoxY && y <= levelBoxY + levelBoxHeight) {
            startLevel(2);
            return;
        }

        if (x >= level3BoxX && x <= level3BoxX + levelBoxWidth &&
                y >= levelBoxY && y <= levelBoxY + levelBoxHeight) {
            startLevel(3);
        }
    }

    private void startLevel(int levelNumber) {
        // 1. Definimos los sabores por defecto si vienen nulos
        Flavour p1 = (flavourP1 != null) ? flavourP1 : Flavour.VANILLA;
        Flavour p2 = (flavourP2 != null) ? flavourP2 : Flavour.VANILLA;

        // 2. Iniciamos el Builder con la configuración base (Juego, Nivel, Sabores)
        PlayingStateBuilder builder = new PlayingStateBuilder(game)
                .withLevelNumber(levelNumber)
                .withPlayer1Flavour(p1)
                .withPlayer2Flavour(p2);

        // 3. Configuramos el Modo y las IAs según la opción seleccionada (gameMode int)
        switch (gameMode) {
            case 0: // PLAYER SOLO
                builder.withMode(GameMode.PLAYER);
                break;

            case 1: // PvP
                builder.withMode(GameMode.PVP);
                break;

            case 2: // PvM
                AIProfile machineAI = (aiProfileP2 != null) ? aiProfileP2 : AIProfile.HUNGRY;
                builder.withMode(GameMode.PVM)
                        .withPlayer2AI(machineAI);
                break;

            case 3: // MvM
                AIProfile m1 = (aiProfileP1 != null) ? aiProfileP1 : AIProfile.HUNGRY;
                AIProfile m2 = (aiProfileP2 != null) ? aiProfileP2 : AIProfile.FEARFUL;
                builder.withMode(GameMode.MVM)
                        .withPlayer1AI(m1)
                        .withPlayer2AI(m2);
                break;
        }

        // 4. ¡Construimos y cambiamos el estado!
        game.setState(builder.build());
    }

    @Override public void keyPressed(Integer key) {}
    @Override public void update() {}
    @Override public void keyReleased(Integer keyCode) {}
}
