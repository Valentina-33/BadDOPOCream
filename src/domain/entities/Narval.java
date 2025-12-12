package domain.entities;

import domain.behavior.NarvalMovement;
import domain.model.Position;
import domain.utils.Direction;
import java.util.EnumMap;
import java.util.Map;


/**
 * Enemigo que patrulla y embiste cuando detecta al jugador alineado
 * Recorre el espacio sin perseguir directamente
 * Si detecta un jugador alineado horizontal o verticalmente, embiste y destruye hielos
 */
public class Narval extends Enemy {

    private static final AnimatedSprite SQUID_SPRITE;

    static {
        Map<Direction, String> narvalSprites = new EnumMap<>(Direction.class);
        narvalSprites.put(Direction.LEFT, "/narval-left.gif");
        narvalSprites.put(Direction.RIGHT, "/narval-right.gif");
        narvalSprites.put(Direction.UP, "/narval-up.gif");
        narvalSprites.put(Direction.DOWN, "/narval-down.gif");

        SQUID_SPRITE = new AnimatedSprite(narvalSprites);
    }

    public Narval(Position position, Direction initialDirection, NarvalMovement movement) {
        super(position, initialDirection, movement);
        setAnimatedSprite(SQUID_SPRITE);
    }
}
