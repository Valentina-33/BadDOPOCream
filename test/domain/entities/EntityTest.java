package domain.entities;

import domain.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {

    @Test
    public void entidadIniciaConPosicionCorrecta() {
        Position pos = new Position(1, 2);

        Entity entity = new Entity(pos) { };

        assertEquals(1, entity.getPosition().getRow());
        assertEquals(2, entity.getPosition().getCol());
    }

    @Test
    public void setPositionActualizaLaPosicion() {
        Entity entity = new Entity(new Position(0, 0)) { };

        entity.setPosition(new Position(3, 4));

        assertEquals(3, entity.getPosition().getRow());
        assertEquals(4, entity.getPosition().getCol());
    }

    @Test
    public void getPositionNoEsNula() {
        Entity entity = new Entity(new Position(2, 2)) { };

        assertNotNull(entity.getPosition());
    }
}
