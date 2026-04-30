package domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CellTypeTest {

    @Test
    public void emptyEsTransitable() {
        assertTrue(CellType.EMPTY.isTraversable());
    }

    @Test
    public void metallicWallNoEsTransitable() {
        assertFalse(CellType.METALLIC_WALL.isTraversable());
    }

    @Test
    public void iceBlockNoEsTransitable() {
        assertFalse(CellType.ICE_BLOCK.isTraversable());
    }
}
