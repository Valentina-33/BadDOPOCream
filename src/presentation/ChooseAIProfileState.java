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
    private final int optX = topBoxX + 60;
    private final int optY = topBoxY + 110;
    private final int optW = 220;
    private final int optH = 45;
    private final int optSpacing = 55;

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

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 22));

        String title = getTitleText();
        g.drawString(title, topBoxX + 90, topBoxY + 60);

        drawProfileOption(g, AIProfile.HUNGRY,  "HUNGRY",  optY);
        drawProfileOption(g, AIProfile.FEARFUL, "FEARFUL", optY + optSpacing);
        drawProfileOption(g, AIProfile.EXPERT,  "EXPERT",  optY + optSpacing * 2);

        // Caja inferior BACK
        if (buttonBackBg != null) {
            g.drawImage(buttonBackBg, bottomBoxX, bottomBoxY, bottomBoxWidth, bottomBoxHeight, null);
        }

        // Botón BACK
        if (backButton != null) {
            g.drawImage(backButton, backBtnX, backBtnY, backBtnWidth, backBtnHeight, null);
        }
    }

    private String getTitleText() {
        // PvM: solo eliges IA para el jugador 2
        if (optionSelected == 2) {
            return "AI PROFILE FOR MACHINE";
        }

        // MvM: eliges IA para máquina 1 y luego para máquina 2
        if (selectedProfileP1 == null) {
            return "AI PROFILE FOR MACHINE 1";
        }
        return "AI PROFILE FOR MACHINE 2";
    }

    private void drawProfileOption(Graphics2D g, AIProfile profile, String label, int y) {
        boolean selected = isSelected(profile);

        g.setColor(new Color(255, 255, 255, 70));
        g.fillRect(optX, y, optW, optH);

        g.setColor(Color.WHITE);
        g.drawRect(optX, y, optW, optH);

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(label, optX + 20, y + 30);

        if (selected) {
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(optX, y, optW, optH);
            g.setColor(Color.YELLOW);
            g.drawRect(optX + 2, y + 2, optW - 4, optH - 4);
        }
    }

    private boolean isSelected(AIProfile profile) {
        if (optionSelected == 2) {
            return selectedProfileP2 == profile;
        }
        // MvM: resaltamos la que se está seleccionando actualmente
        if (selectedProfileP1 == null) {
            return selectedProfileP1 == profile;
        }
        return selectedProfileP2 == profile;
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
        if (x < optX || x > optX + optW) return null;

        if (y >= optY && y <= optY + optH) return AIProfile.HUNGRY;
        if (y >= optY + optSpacing && y <= optY + optSpacing + optH) return AIProfile.FEARFUL;
        if (y >= optY + optSpacing * 2 && y <= optY + optSpacing * 2 + optH) return AIProfile.EXPERT;

        return null;
    }

    @Override
    public void keyPressed(Integer keyCode) {}

    @Override
    public void keyReleased(Integer keyCode) {}
}
