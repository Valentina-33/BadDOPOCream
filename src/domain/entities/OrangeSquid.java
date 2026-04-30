package domain.entities;

import domain.behavior.OrangeSquidMovement;
import domain.model.Position;
import domain.utils.Direction;
import presentation.SpriteFactory;

public class OrangeSquid extends Enemy {
    public OrangeSquid(Position position, Direction initialDirection, OrangeSquidMovement movement) {
        super(position, initialDirection, movement);
        setAnimatedSprite(SpriteFactory.getOrangeSquidSprite());
    }
}