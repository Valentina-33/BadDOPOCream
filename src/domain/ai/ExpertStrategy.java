package domain.ai;

import domain.entities.Enemy;
import domain.entities.Fruit;
import domain.entities.Player;
import domain.game.Level;
import domain.utils.Direction;

public class ExpertStrategy extends AbstractAIStrategy {

    @Override
    public Direction decideNextMove(Level level, Player me) {
        remember(me.getPosition());

        // 1. Prioridad: Supervivencia. Si hay enemigo muy cerca, huir.
        Enemy threat = nearestEnemy(level, me.getPosition());
        if (threat != null) {
            int d = manhattan(me.getPosition(), threat.getPosition());
            if (d <= 3) {
                Direction away = stepAway(level.getBoard(), me.getPosition(), threat.getPosition());
                if (away != Direction.NONE) return away;
            }
        }

        // 2. Prioridad: Objetivo. Si está a salvo, ir por fruta.
        Fruit target = nearestActiveFruit(level, me.getPosition());
        if (target != null) {
            if (isStuck()) {
                Direction bfs = bfsRescue(level.getBoard(), me.getPosition(), target.getPosition());
                if (bfs != Direction.NONE) return bfs;
            }
            Direction toward = stepToward(level.getBoard(), me.getPosition(), target.getPosition());
            if (toward != Direction.NONE) return toward;
        }

        // 3. Fallback: Caminar aleatoriamente
        return randomWalk(level.getBoard(), me.getPosition());
    }
}