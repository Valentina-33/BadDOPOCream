package domain.entities;

import domain.behavior.TrollTurnRightMovement;
import domain.model.Position;
import domain.utils.Direction;

import java.util.EnumMap;
import java.util.Map;

/**
 * Enemigo que se mueve en línea recta y gira 90° a la derecha al chocar
 */
public class Troll extends Enemy {

    private static final AnimatedSprite TROLL_SPRITE;

    static {
        Map<Direction, String> trollSprites = new EnumMap<>(Direction.class);
        trollSprites.put(Direction.LEFT, "/troll-left.gif");
        trollSprites.put(Direction.RIGHT, "/troll-right.gif");
        trollSprites.put(Direction.UP, "/troll-up.gif");
        trollSprites.put(Direction.DOWN, "/troll-down.gif");

        TROLL_SPRITE = new AnimatedSprite(trollSprites);
    }

    public Troll(Position position, Direction initialDirection, TrollTurnRightMovement movement) {
        super(position, initialDirection, movement);
        setAnimatedSprite(TROLL_SPRITE);
    }
}
