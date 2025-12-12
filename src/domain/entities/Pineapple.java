package domain.entities;

import domain.behavior.PineappleMovement;
import domain.model.Position;

/**
 * Fruta dinámica
 */
public class Pineapple extends MovingFruit {

    public static final int PINEAPPLE_SCORE = 200;
    public static final Sprite SPRITE = new Sprite("/pineapple.png");

    public Pineapple(Position position) {
        super(position,
                PINEAPPLE_SCORE,
                SPRITE,
                new PineappleMovement());
    }

    public int getPoints() { return PINEAPPLE_SCORE; }
}
