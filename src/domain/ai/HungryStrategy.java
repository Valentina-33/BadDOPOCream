package domain.ai;

import domain.entities.Fruit;
import domain.entities.Player;
import domain.game.Level;
import domain.utils.Direction;

public class HungryStrategy extends AbstractAIStrategy {

    @Override
    public Direction decideNextMove(Level level, Player me) {
        remember(me.getPosition());

        Fruit target = nearestActiveFruit(level, me.getPosition());

        // Si no hay frutas, caminar al azar
        if (target == null) {
            return randomWalk(level.getBoard(), me.getPosition());
        }

        // Si estamos atascados, usar BFS inteligente para salir del bloqueo
        if (isStuck()) {
            Direction bfs = bfsRescue(level.getBoard(), me.getPosition(), target.getPosition());
            if (bfs != Direction.NONE) return bfs;
        }

        // Movimiento Greedy normal hacia la fruta
        return stepToward(level.getBoard(), me.getPosition(), target.getPosition());
    }
}