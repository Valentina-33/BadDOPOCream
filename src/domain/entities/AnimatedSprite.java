package domain.entities;

import domain.utils.Direction;
import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

/**
 * Maneja sprites animados con direcciones
 * Cada enemigo puede tener diferentes sprites según su dirección de movimiento
 */
public class AnimatedSprite {

    private final Map<Direction, Image> sprites;
    private Direction currentDirection;

    /**
     * Constructor para sprites con 4 direcciones
     */
    public AnimatedSprite(String basePath, String entityName) {
        sprites = new EnumMap<>(Direction.class);
        currentDirection = Direction.DOWN;

        // Cargar sprites para cada dirección
        loadSprite(Direction.UP, basePath + entityName + "-up.gif");
        loadSprite(Direction.DOWN, basePath + entityName + "-down.gif");
        loadSprite(Direction.LEFT, basePath + entityName + "-left.gif");
        loadSprite(Direction.RIGHT, basePath + entityName + "-right.gif");
    }

    /**
     * Constructor para sprites con rutas personalizadas
     */
    public AnimatedSprite(Map<Direction, String> spritePaths) {
        sprites = new EnumMap<>(Direction.class);
        currentDirection = Direction.DOWN;

        for (Map.Entry<Direction, String> entry : spritePaths.entrySet()) {
            loadSprite(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Carga un sprite desde un archivo de recursos
     */
    private void loadSprite(Direction direction, String path) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            sprites.put(direction, icon.getImage());
        } catch (Exception e) {
            System.err.println("Error cargando sprite: " + path);
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la dirección actual del sprite
     */
    public void setDirection(Direction direction) {
        if (direction != Direction.NONE && sprites.containsKey(direction)) {
            this.currentDirection = direction;
        }
    }

    /**
     * Dibuja el sprite actual según la dirección
     */
    public void draw(Graphics2D g, int x, int y, int width, int height, Direction direction) {
        setDirection(direction);
        draw(g, x, y, width, height);
    }

    /**
     * Dibuja el sprite actual
     */
    public void draw(Graphics2D g, int x, int y, int width, int height) {
        Image currentSprite = sprites.get(currentDirection);
        if (currentSprite != null) {
            g.drawImage(currentSprite, x, y, width, height, null);
        }
    }
}