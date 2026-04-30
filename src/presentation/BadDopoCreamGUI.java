package presentation;

import domain.game.*;
import persistence.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Ventana principal del juego con menú de opciones
 */
public class BadDopoCreamGUI extends JFrame {

    private final Game game;

    public BadDopoCreamGUI() {
        setTitle("Bad DOPO Cream");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.game = new Game();
        GamePanel panel = new GamePanel(game);

        createMenuBar();
        add(panel);
        setVisible(true);

        panel.start();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("Archivo");

        JMenuItem openItem = new JMenuItem("Abrir");
        JMenuItem saveItem = new JMenuItem("Guardar");
        JMenuItem importItem = new JMenuItem("Importar Nivel (.txt)");
        JMenuItem exitItem = new JMenuItem("Salir");

        openItem.addActionListener(e -> openGame());
        saveItem.addActionListener(e -> saveGame());
        importItem.addActionListener(e -> importLevel());
        exitItem.addActionListener(e -> exitGame());

        menuFile.add(openItem);
        menuFile.add(saveItem);
        menuFile.addSeparator();
        menuFile.add(importItem);
        menuFile.addSeparator();
        menuFile.add(exitItem);

        menuBar.add(menuFile);
        setJMenuBar(menuBar);
    }

    /**
     * Abrir partida guardada (.dat)
     */
    private void openGame() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Abrir Partida Guardada");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Partidas Guardadas (*.dat)", "dat"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                PlayingState state = GameLoader.load(game, chooser.getSelectedFile());
                game.setState(state);
                JOptionPane.showMessageDialog(this,
                        "Partida cargada correctamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (BadIceException | IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al cargar: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Guardar partida actual (.dat)
     */
    private void saveGame() {
        if (!(game.getState() instanceof PlayingState)) {
            JOptionPane.showMessageDialog(this,
                    "Debes estar jugando para guardar la partida",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Partida");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Partidas Guardadas (*.dat)", "dat"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.endsWith(".dat")) {
                file = new File(path + ".dat");
            }

            try {
                GameSaver.save((PlayingState) game.getState(), file);
                JOptionPane.showMessageDialog(this,
                        "Partida guardada correctamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (BadIceException | IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Importar nivel personalizado desde archivo .txt
     */
    private void importLevel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Importar Nivel Personalizado");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos de Nivel (*.txt)", "txt"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Level level = LevelImporter.importFromFile(chooser.getSelectedFile());

                // 1. Preguntar al usuario qué modo desea para este nivel
                GameMode[] modes = {GameMode.PLAYER, GameMode.PVP, GameMode.PVM, GameMode.MVM};
                int response = JOptionPane.showOptionDialog(
                        this,
                        "Selecciona el modo de juego para este nivel:",
                        "Configuración de Nivel",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        modes,
                        modes[0] // Opción por defecto
                );

                // Si cierra la ventana sin elegir, cancelamos
                if (response < 0) return;

                GameMode selectedMode = modes[response];

                // 2. Usar el Builder con el modo seleccionado
                PlayingState state = new PlayingStateBuilder(game)
                        .withCustomLevel(level)
                        .withMode(selectedMode)
                        // Aquí podrías preguntar también por IAs si es PvM/MvM,
                        // pero para simplificar, usaremos defaults o podrías expandir este diálogo.
                        .build();

                game.setState(state);

                JOptionPane.showMessageDialog(this,
                        "Nivel importado correctamente en modo " + selectedMode,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al importar nivel:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Salir del juego con confirmación
     */
    private void exitGame() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que quieres salir?",
                "Confirmar Salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BadDopoCreamGUI::new);
    }
}