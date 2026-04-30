package domain.entities;

import domain.model.Position;
import domain.utils.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    public void jugadorIniciaVivo() {
        Player player = new Player(new Position(1, 1));
        assertFalse(player.isDead());
    }

    @Test
    public void addScoreIncrementaElPuntaje() {
        Player player = new Player(new Position(2, 2));
        player.addScore(10);
        assertEquals(10, player.getScore());
    }

    @Test
    public void setPositionActualizaLaPosicion() {
        Player player = new Player(new Position(0, 0));
        player.setPosition(new Position(3, 4));

        assertEquals(3, player.getPosition().getRow());
        assertEquals(4, player.getPosition().getCol());
    }


    @Test
    public void addScoreAcumulaPuntaje() {
        Player player = new Player(new Position(2, 2));
        player.addScore(5);
        player.addScore(7);
        assertEquals(12, player.getScore());
    }

    @Test
    public void getPositionRetornaLaPosicionInicial() {
        Position pos = new Position(4, 5);
        Player player = new Player(pos);

        assertEquals(pos.getRow(), player.getPosition().getRow());
        assertEquals(pos.getCol(), player.getPosition().getCol());
    }

}
