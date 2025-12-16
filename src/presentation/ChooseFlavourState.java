package presentation;

import domain.game.AIProfile;
import domain.game.Flavour;
import domain.game.Game;
import domain.game.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class ChooseFlavourState implements GameState {

    private final Game game;
    private final int optionSelected;

    private final AIProfile aiProfileP1;
    private final AIProfile aiProfileP2;

    private Flavour flavourP1 = Flavour.VANILLA;
    private Flavour flavourP2 = Flavour.VANILLA;

    private int selectingIndex = 1;

    private Image backgroundGif;
    private Image buttonBackBg;
    private Image backButton;
    private Image playerBg;
    private Image chooseFlavour;
    private Image threeIceCreams;

    private Image player1Icon;
    private Image player2Icon;

    private final int bottomBoxX = 32;
    private final int bottomBoxY = 360;
    private final int bottomBoxWidth = 512;
    private final int bottomBoxHeight = 160;

    private final int backBtnWidth = 168;
    private final int backBtnHeight = 64;
    private final int backBtnX = bottomBoxX + (bottomBoxWidth - backBtnWidth) / 2;
    private final int backBtnY = bottomBoxY + (bottomBoxHeight - backBtnHeight) / 2;


    public ChooseFlavourState(Game game, int optionSelected) {
        this(game, optionSelected, null, null);
    }

    public ChooseFlavourState(Game game, int optionSelected, AIProfile aiProfileP1, AIProfile aiProfileP2, Flavour flavourP1, Flavour flavourP2) {
        this.game = game;
        this.optionSelected = optionSelected;
        this.aiProfileP1 = aiProfileP1;
        this.aiProfileP2 = aiProfileP2;

        this.flavourP1 = (flavourP1 != null) ? flavourP1 : Flavour.VANILLA;
        this.flavourP2 = (flavourP2 != null) ? flavourP2 : Flavour.VANILLA;

        loadAssets();
    }

    public ChooseFlavourState(Game game, int optionSelected, AIProfile aiProfileP1, AIProfile aiProfileP2) {
        this.game = game;
        this.optionSelected = optionSelected;
        this.aiProfileP1 = aiProfileP1;
        this.aiProfileP2 = aiProfileP2;

        loadAssets();
    }

    private boolean needsTwoFlavours() {
        return optionSelected == 1 || optionSelected == 2 || optionSelected == 3;
    }

    private void loadAssets() {
        try {
            backgroundGif = new ImageIcon(Objects.requireNonNull(getClass().getResource("/home-animation.gif"))).getImage();
            buttonBackBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/back-button-bg.jpg"))).getImage();
            backButton = new ImageIcon(Objects.requireNonNull(getClass().getResource("/back-button.jpg"))).getImage();
            playerBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/player-bg.jpg"))).getImage();
            chooseFlavour = new ImageIcon(Objects.requireNonNull(getClass().getResource("/choose-flavour.png"))).getImage();
            threeIceCreams = new ImageIcon(Objects.requireNonNull(getClass().getResource("/joined-icecreams.png"))).getImage();

            player1Icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/player-1.png"))).getImage();
            player2Icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/player-2.png"))).getImage();

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

        int topBoxY = 24;
        int topBoxHeight = 320;
        int topBoxWidth = 512;
        int topBoxX = 32;
        if (playerBg != null) {
            g.drawImage(playerBg, topBoxX, topBoxY, topBoxWidth, topBoxHeight, null);
        }

        if (chooseFlavour != null) {
            int titleWidth = 330;
            int titleHeight = 40;
            int titleX = topBoxX + (topBoxWidth - titleWidth) / 2;
            int titleY = topBoxY + 20;
            g.drawImage(chooseFlavour, titleX, titleY, titleWidth, titleHeight, null);
        }

        if (threeIceCreams != null) {
            int iceCreamHeight = 230;
            int iceCreamWidth = 230;
            int iceCreamY = 90;
            int iceCreamX = 170;
            g.drawImage(threeIceCreams, iceCreamX, iceCreamY, iceCreamWidth, iceCreamHeight, null);
        }

        // Mostrar imagen del jugador que está seleccionando (abajo a la izquierda)
        if (needsTwoFlavours()) {
            Image currentPlayerIcon = (selectingIndex == 1) ? player1Icon : player2Icon;
            if (currentPlayerIcon != null) {
                int playerIconWidth = 120;
                int playerIconHeight = 40;
                int playerIconX = topBoxX + 40;
                int playerIconY = topBoxY + topBoxHeight - playerIconHeight - 20;
                g.drawImage(currentPlayerIcon, playerIconX, playerIconY, playerIconWidth, playerIconHeight, null);
            }
        }

        if (buttonBackBg != null) {
            g.drawImage(buttonBackBg, bottomBoxX, bottomBoxY, bottomBoxWidth, bottomBoxHeight, null);
        }

        if (backButton != null) {
            g.drawImage(backButton, backBtnX, backBtnY, backBtnWidth, backBtnHeight, null);
        }
    }

    private void goNext() {
        if (!needsTwoFlavours()) {
            flavourP2 = flavourP1;
        }
        game.setState(new SelectLevelState(game, optionSelected, aiProfileP1, aiProfileP2, flavourP1, flavourP2));
    }

    @Override
    public void keyPressed(Integer key) {
        if (key == KeyEvent.VK_ENTER) goNext();
    }

    @Override
    public void mouseClicked(Integer x, Integer y) {

        if (x >= backBtnX && x <= backBtnX + backBtnWidth &&
                y >= backBtnY && y <= backBtnY + backBtnHeight) {

            if (optionSelected == 2 || optionSelected == 3) {
                game.setState(new ChooseAIProfileState(game, optionSelected));
            } else {
                game.setState(new SelectModeState(game));
            }
            return;
        }

        // Detectar click en los helados
        Flavour clicked = clickedFlavour(x, y);
        if (clicked != null) {

            if (!needsTwoFlavours()) {
                flavourP1 = clicked;
                goNext();
            } else {
                if (selectingIndex == 1) {
                    flavourP1 = clicked;
                    selectingIndex = 2;
                } else {
                    flavourP2 = clicked;
                    goNext();
                }
            }
        }
    }

    private Flavour clickedFlavour(int x, int y) {
        // Detectar click en los helados de la imagen grande
        final int chocolateWidth = 75;
        final int vanillaX = 245;
        final int vanillaWidth = 80;
        final int strawberryX = 325;
        final int strawberryWidth = 75;
        final int iceCreamClickY = 90;
        final int iceCreamClickHeight = 180;

        if (y >= iceCreamClickY && y <= iceCreamClickY + iceCreamClickHeight) {
            // Rangos para detectar clicks en cada helado
            int chocolateX = 170;
            if (x >= chocolateX && x <= chocolateX + chocolateWidth) {
                return Flavour.CHOCOLATE;
            }
            if (x >= vanillaX && x <= vanillaX + vanillaWidth) {
                return Flavour.VANILLA;
            }
            if (x >= strawberryX && x <= strawberryX + strawberryWidth) {
                return Flavour.STRAWBERRY;
            }
        }
        return null;
    }

    @Override public void update() {}
    @Override public void keyReleased(Integer keyCode) {}
}