package domain.entities;

import domain.behavior.MovementBehavior;
import domain.model.Position;
import domain.utils.Direction;

public class Maceta extends Enemy{
    public Maceta(Position position, Direction initialDirection, MovementBehavior movementBehavior) {
        super(position, initialDirection, movementBehavior);
    }
}
