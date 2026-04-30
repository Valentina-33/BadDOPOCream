package domain.entities;

import domain.behavior.CherryMovement;
import domain.game.Level;
import domain.model.Position;
import presentation.SpriteFactory;

public class Cherry extends Fruit {
    public static final int CHERRY_SCORE = 150;
    private final CherryMovement behavior;

    public Cherry(Position position) {
        super(position, CHERRY_SCORE, SpriteFactory.getStaticSprite("/cherry.png"));
        this.behavior = new CherryMovement();
    }

    @Override
    public void update(Level level) {
        if (isCollected() || isFrozen()) return;
        behavior.update(level, this);
    }

    @Override
    public int getPoints() { return CHERRY_SCORE; }
}