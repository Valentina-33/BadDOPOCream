package domain.ai;

import domain.entities.Enemy;
import domain.entities.Fruit;
import domain.entities.Player;
import domain.game.Level;
import domain.utils.Direction;

public class FearfulStrategy extends AbstractAIStrategy {

    @Override
    public Direction decideNextMove(Level level, Player me) {
        remember(me.getPosition());

        Enemy threat = nearestEnemy(level, me.getPosition());

        // Si no hay enemigos cerca, se comporta como un recolector normal
        if (threat == null) {
            Fruit target = nearestActiveFruit(level, me.getPosition());
            if (target != null) {
                if (isStuck()) {
                    Direction bfs = bfsRescue(level.getBoard(), me.getPosition(), target.getPosition());
                    if (bfs != Direction.NONE) return bfs;
                }
                return stepToward(level.getBoard(), me.getPosition(), target.getPosition());
            }
            return randomWalk(level.getBoard(), me.getPosition());
        }

        // Si hay enemigo, prioriza huir
        Direction away = stepAway(level.getBoard(), me.getPosition(), threat.getPosition());
        if (away != Direction.NONE) return away;

        return randomWalk(level.getBoard(), me.getPosition());
    }
}