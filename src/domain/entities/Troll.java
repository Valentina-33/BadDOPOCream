package domain.entities;

import domain.utils.Direction;
import domain.model.Position;
import domain.behavior.MovementBehavior;

public class Troll extends Enemy{
    public Troll(Position position, Direction initialDirection, MovementBehavior movementBehavior) {
        super(position, initialDirection, movementBehavior);
    }

}
