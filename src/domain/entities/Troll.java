package domain.entities;

import domain.behavior.TrollTurnRightMovement;
import domain.model.Position;
import domain.utils.Direction;
import presentation.SpriteFactory;

public class Troll extends Enemy {

    public Troll(Position position, Direction initialDirection, TrollTurnRightMovement movement) {
        super(position, initialDirection, movement);
        // Pedimos el sprite a la fábrica
        setAnimatedSprite(SpriteFactory.getTrollSprite());
    }
}