package domain.entities;

import domain.behavior.PineappleMovement;
import domain.model.Position;

public class Pineapple extends MovingFruit {

    public static final int PINEAPPLE_SCORE = 70;
    public static final Sprite SPRITE = new Sprite("/pineapple.jpg");

    public Pineapple(Position position) {
        super(position,
                PINEAPPLE_SCORE,
                SPRITE,
                new PineappleMovement()); // 🍍 comportamiento por defecto
    }

    public int getPoints() { return PINEAPPLE_SCORE; }
}
