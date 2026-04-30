package domain.entities;

import domain.behavior.NarvalMovement;
import domain.model.Position;
import domain.utils.Direction;
import presentation.SpriteFactory;

public class Narval extends Enemy {
    public Narval(Position position, Direction initialDirection, NarvalMovement movement) {
        super(position, initialDirection, movement);
        setAnimatedSprite(SpriteFactory.getNarvalSprite());
    }
}