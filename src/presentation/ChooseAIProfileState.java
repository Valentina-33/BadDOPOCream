package presentation;

import domain.game.AIProfile;
import domain.game.Game;
import domain.game.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Permite escoger el perfil de IA para PvM y MvM.
 *
 * optionSelected:
 *  2 = PvM (Player vs Machine) -> 1 IA (jugador 2)
 *  3 = MvM (Machine vs Machine) -> 2 IA (jugador 1 y jugador 2)
 */
public class ChooseAIProfileState implements GameState {

    private final Game game;
    private final int optionSelected;

    // Selecciones reales
    private AIProfile selectedProfileP1 = null; // usado en MvM
    private AIProfile selectedProfileP2 = null; // usado en PvM y MvM

    private Image backgroundGif;
    private Image buttonBackBg;
    private Image backButton;
    private Image playerBg;
    private Image machine1Title;
    private Image machine2Title;
    private Image hungryImg;
    private Image fearfulImg;
    private Image expertImg;
    private Image iceCreams;

    // Caja superior
    private final int topBoxX = 32;
    private final int topBoxY = 24;
    private final int topBoxWidth = 512;
    private final int topBoxHeight = 320;

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

    // Opciones de perfil
    private final int optX = topBoxX + 40;
    private final int optY = topBoxY + 100;
    private final int optSpacing = 55;

    // Helados (3 scoops)
    private final int icW = 220;
    private final int icH = 220;
    private final int icX = topBoxX + topBoxWidth - icW - 20;
    private final int icY = topBoxY + 80;

    public ChooseAIProfileState(Game game, int optionSelected) {
        this.game = game;
        this.optionSelected = optionSelected;

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

            this.machine1Title = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/machine-1.png"))
            ).getImage();

            this.machine2Title = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/machine-2.png"))
            ).getImage();

            this.hungryImg = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/hungry.png"))
            ).getImage();

            this.fearfulImg = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/fearful.png"))
            ).getImage();

            this.expertImg = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/expert.png"))
            ).getImage();

            this.iceCreams = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/3-icecreams.png"))
            ).getImage();

        } catch (Exception e) {
            System.err.println("Error cargando recursos: " + e.getMessage());
        }
    }

    @Override
    public void update() {}

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

        // Título (Machine 1 o Machine 2 dependiendo del estado)
        Image titleImg = getTitleImage();
        if (titleImg != null) {
            int titleWidth = 400;
            int titleHeight = 50;
            int titleX = topBoxX + (topBoxWidth - titleWidth) / 2;
            int titleY = topBoxY + 20;
            g.drawImage(titleImg, titleX, titleY, titleWidth, titleHeight, null);
        }

        // Opciones con imágenes (Hungry, Fearful, Expert)
        int optionWidth = 150;
        int optionHeight = 50;

        if (hungryImg != null) {
            g.drawImage(hungryImg, optX, optY, optionWidth, optionHeight, null);
        }
        if (fearfulImg != null) {
            g.drawImage(fearfulImg, optX, optY + optSpacing, optionWidth, optionHeight, null);
        }
        if (expertImg != null) {
            g.drawImage(expertImg, optX, optY + optSpacing * 2, optionWidth, optionHeight, null);
        }

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

    private Image getTitleImage() {
        // PvM: solo eliges IA para el jugador 2
        if (optionSelected == 2) {
            return machine1Title;
        }

        // MvM: eliges IA para máquina 1 y luego para máquina 2
        if (selectedProfileP1 == null) {
            return machine1Title;
        }
        return machine2Title;
    }

    @Override
    public void mouseClicked(Integer x, Integer y) {

        // Click BACK
        if (x >= backBtnX && x <= backBtnX + backBtnWidth &&
                y >= backBtnY && y <= backBtnY + backBtnHeight) {
            game.setState(new SelectModeState(game));
            return;
        }

        // Click en perfiles
        AIProfile clickedProfile = getClickedProfile(x, y);
        if (clickedProfile == null) return;

        // PvM: solo setea perfil para P2 (máquina) y avanza
        if (optionSelected == 2) {
            selectedProfileP1 = null;              // humano
            selectedProfileP2 = clickedProfile;    // IA
            game.setState(new ChooseFlavourState(game, optionSelected, selectedProfileP1, selectedProfileP2));
            return;
        }

        // MvM: primero P1, luego P2
        if (selectedProfileP1 == null) {
            selectedProfileP1 = clickedProfile;
            return;
        }

        selectedProfileP2 = clickedProfile;
        game.setState(new ChooseFlavourState(game, optionSelected, selectedProfileP1, selectedProfileP2));
    }

    private AIProfile getClickedProfile(int x, int y) {
        int optionWidth = 150;
        int optionHeight = 50;

        if (x < optX || x > optX + optionWidth) return null;

        if (y >= optY && y <= optY + optionHeight) return AIProfile.HUNGRY;
        if (y >= optY + optSpacing && y <= optY + optSpacing + optionHeight) return AIProfile.FEARFUL;
        if (y >= optY + optSpacing * 2 && y <= optY + optSpacing * 2 + optionHeight) return AIProfile.EXPERT;

        return null;
    }

    @Override
    public void keyPressed(Integer keyCode) {}

    @Override
    public void keyReleased(Integer keyCode) {}
}