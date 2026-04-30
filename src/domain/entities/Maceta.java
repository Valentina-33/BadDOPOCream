package domain.entities;

import domain.behavior.MacetaChaseMovement;
import domain.model.Position;
import domain.utils.Direction;
import presentation.SpriteFactory;

public class Maceta extends Enemy {
    public Maceta(Position position, Direction initialDirection, MacetaChaseMovement movement) {
        super(position, initialDirection, movement);
        setAnimatedSprite(SpriteFactory.getMacetaSprite());
    }
}