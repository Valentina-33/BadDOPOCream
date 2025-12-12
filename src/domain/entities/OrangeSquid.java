package domain.entities;

import domain.behavior.OrangeSquidMovement;
import domain.model.Position;
import domain.utils.Direction;
import java.util.EnumMap;
import java.util.Map;

/**
 * Calamar Naranja: Enemigo que persigue al jugador y destruye hielo en su camino
 */
public class OrangeSquid extends Enemy {

    private static final AnimatedSprite SQUID_SPRITE;

    static {
        Map<Direction, String> squidSprites = new EnumMap<>(Direction.class);
        squidSprites.put(Direction.LEFT, "/orange-squid-left.gif");
        squidSprites.put(Direction.RIGHT, "/orange-squid-right.gif");
        squidSprites.put(Direction.UP, "/orange-squid-up.gif");
        squidSprites.put(Direction.DOWN, "/orange-squid-down.gif");

        SQUID_SPRITE = new AnimatedSprite(squidSprites);
    }

    public OrangeSquid(Position position, Direction initialDirection, OrangeSquidMovement movement) {
        super(position, initialDirection, movement);
        setAnimatedSprite(SQUID_SPRITE);
    }
}