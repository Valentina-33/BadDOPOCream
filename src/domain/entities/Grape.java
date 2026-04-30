package domain.entities;
import domain.model.Position;
import domain.game.Level;
import presentation.SpriteFactory;

/**
 * Fruta estática.
 */
public class Grape extends Fruit{
    private static final int GRAPE_SCORE = 50;

    public Grape(Position position) {
        super(position, GRAPE_SCORE, SpriteFactory.getStaticSprite("/grape.jpg"));
    }

    @Override
    // Las uvas son estáticas
    public void update(Level level) { }
    @Override
    public int getPoints() {return GRAPE_SCORE;}
}
