package domain.entities;

import domain.behavior.PineappleMovement;
import domain.model.Position;
import presentation.SpriteFactory;

/**
 * Fruta dinámica
 */
public class Pineapple extends MovingFruit {

    public static final int PINEAPPLE_SCORE = 200;

    public Pineapple(Position position) {
        super(position, PINEAPPLE_SCORE, SpriteFactory.getStaticSprite("/pineapple.png"), new PineappleMovement());
    }

    public int getPoints() { return PINEAPPLE_SCORE; }
}
