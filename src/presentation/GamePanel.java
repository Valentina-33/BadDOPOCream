package presentation;

import domain.game.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Inicializa la primera pantalla del juego, usando Listeners y Events.
 */
public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener {

    public static final Integer TILE_SIZE = 32;
    public static final Integer COLS = 18;
    public static final Integer ROWS = 18;

    public static final Integer WIDTH = COLS * TILE_SIZE;
    public static final Integer HEIGHT = ROWS * TILE_SIZE;

    private final Game game;
    private Thread gameThread;
    private boolean running;

    private float scaleFactor = 1.0f;
    private int xOffset = 0;
    private int yOffset = 0;

    public GamePanel(Game game) {
        this.game = game;

        setBackground(new Color(104,135,158));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    public void start() {
        if (gameThread == null) {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    @Override
    public void run() {
        final long frameTime = 250;

        while (running) {
            long start = System.currentTimeMillis();

            game.update();
            repaint();

            long elapsed = System.currentTimeMillis() - start;
            long sleep = frameTime - elapsed;
            if (sleep < 0) sleep = 2;

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int screenWidth = getWidth();
        int screenHeight = getHeight();

        float scaleX = (float) screenWidth / WIDTH;
        float scaleY = (float) screenHeight / HEIGHT;
        scaleFactor = Math.min(scaleX, scaleY);

        int scaledWidth = (int) (WIDTH * scaleFactor);
        int scaledHeight = (int) (HEIGHT * scaleFactor);
        xOffset = (screenWidth - scaledWidth) / 2;
        yOffset = (screenHeight - scaledHeight) / 2;

        var oldTransform = g2.getTransform();

        g2.translate(xOffset, yOffset);
        g2.scale(scaleFactor, scaleFactor);

        game.render(g2);

        g2.setTransform(oldTransform);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        game.keyPressed(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        game.keyReleased(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        int realX = (int) ((e.getX() - xOffset) / scaleFactor);
        int realY = (int) ((e.getY() - yOffset) / scaleFactor);

        if (realX >= 0 && realX <= WIDTH && realY >= 0 && realY <= HEIGHT) {
            game.mouseClicked(realX, realY);
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
