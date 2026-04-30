package domain.ai;

import domain.game.Level;
import domain.entities.Player;
import domain.utils.Direction;

public interface AIMovementStrategy {
    Direction decideNextMove(Level level, Player me);
}