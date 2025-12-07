package presentation;

import domain.game.Game;
import presentation.MenuState;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del juego Bad DOPO Cream.
 */
public class BadDopoCreamGUI extends JFrame {

    private final Game game;
    private final GamePanel panel;

    public BadDopoCreamGUI() {
        setTitle("Bad DOPO Cream");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear el juego
        this.game = new Game();
        this.game.setState(new MenuState(game));

        // Crear el panel que dibuja y actualiza el juego
        this.panel = new GamePanel(game);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        // Opcional: tamaño base antes de poner fullscreen
        setSize(800, 800);
        setLocationRelativeTo(null);

        // Pantalla completa (si lo quieres siempre fullscreen)
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setVisible(true);

        panel.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BadDopoCreamGUI::new);
    }
}
