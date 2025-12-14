package domain.behavior;
import domain.game.Level;
import domain.entities.Enemy;
import domain.entities.Entity;
import domain.model.*;
import domain.utils.Direction;

/**
 * Movimiento del Troll
 * Intenta avanzar en la dirección actual
 * Si choca con algo gira 90° a la derecha hasta encontrar un espacio donde pueda caminar
 * Intenta como máximo 4 direcciones, si todas están bloqueadas se queda quieto
 */
public class TrollTurnRightMovement implements MovementBehavior{
    //Para que no ande tan rápido
    private int tickCounter = 0;
    private static final int TICKS_PER_MOVE = 23;

    @Override
    public void move(Level level, Entity object) {
        Enemy enemy = (Enemy) object;
        //Controlamos la velocidad
        tickCounter++;
        if (tickCounter % TICKS_PER_MOVE != 0) {
            return;
        }

        Board board = level.getBoard();
        Direction dirPrueba = enemy.getDirection();

        //Probamos las 4 direcciones
        for (int i=0; i<4; i++) {
            Position current = enemy.getPosition();
            Position next = current.translated(dirPrueba.getDRow(),dirPrueba.getDCol());
            if (board.isWalkable((next))){
                enemy.setPosition(next);
                enemy.setDirection(dirPrueba);
                return;
            } else {
                dirPrueba = dirPrueba.turnRight();
            }
        }
    }
}
