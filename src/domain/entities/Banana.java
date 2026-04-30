package domain.entities;
import domain.model.Position;
import domain.game.Level;
import presentation.SpriteFactory;

public class Banana extends Fruit {
    private static final int BANANA_SCORE = 100;

    public Banana(Position position) {
        // Obtenemos el sprite de la fábrica
        super(position, BANANA_SCORE, SpriteFactory.getStaticSprite("/banana.jpg"));
    }

    @Override
    public void update(Level level) { /* Estática */ }

    @Override
    public int getPoints() { return BANANA_SCORE; }
}
