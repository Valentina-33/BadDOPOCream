package domain.entities;

import domain.model.Position;
import domain.utils.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyTest {

    @Test
    public void enemigoIniciaConPosicionCorrecta() {
        Position pos = new Position(2, 3);
        Enemy enemy = new Enemy(pos, Direction.UP, null);

        assertEquals(2, enemy.getPosition().getRow());
        assertEquals(3, enemy.getPosition().getCol());
    }

    @Test
    public void enemigoIniciaConDireccionAsignada() {
        Enemy enemy = new Enemy(new Position(1, 1), Direction.LEFT, null);

        assertEquals(Direction.LEFT, enemy.getDirection());
    }

    @Test
    public void setPositionActualizaLaPosicionDelEnemigo() {
        Enemy enemy = new Enemy(new Position(0, 0), Direction.DOWN, null);
        enemy.setPosition(new Position(4, 5));

        assertEquals(4, enemy.getPosition().getRow());
        assertEquals(5, enemy.getPosition().getCol());
    }
}
