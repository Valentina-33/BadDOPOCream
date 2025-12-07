package domain.behavior;
import domain.game.Level;
import domain.entities.Entity;

public interface MovementBehavior {
    void move(Level level, Entity object);
}
