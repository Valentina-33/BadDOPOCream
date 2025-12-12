package domain.entities;

import domain.behavior.MacetaChaseMovement;
import domain.model.Position;
import domain.utils.Direction;
import java.util.EnumMap;
import java.util.Map;

/**
 * Maceta: Enemigo que alterna entre movimiento aleatorio y persecución al jugador
 */
public class Maceta extends Enemy {

    private static final AnimatedSprite MACETA_SPRITE;

    static {
        Map<Direction, String> macetaSprites = new EnumMap<>(Direction.class);
        macetaSprites.put(Direction.LEFT, "/maceta-left.gif");
        macetaSprites.put(Direction.RIGHT, "/maceta-right.gif");
        macetaSprites.put(Direction.UP, "/maceta-up.gif");
        macetaSprites.put(Direction.DOWN, "/maceta-down.gif");

        MACETA_SPRITE = new AnimatedSprite(macetaSprites);
    }

    public Maceta(Position position, Direction initialDirection, MacetaChaseMovement movement) {
        super(position, initialDirection, movement);
        setAnimatedSprite(MACETA_SPRITE);
    }
}