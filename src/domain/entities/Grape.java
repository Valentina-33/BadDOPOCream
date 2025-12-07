package domain.entities;
import domain.model.Position;
import domain.game.Level;

public class Grape extends Fruit{
    private static final int GRAPE_SCORE = 50;
    private static final Sprite SPRITE = new Sprite("/grape.jpg");

    public Grape(Position position) {
        super(position, GRAPE_SCORE, SPRITE);
    }
    @Override
    public void update(Level level) {
        // Uva estática por ahora
    }
    @Override
    public int getPoints() {return GRAPE_SCORE;}
}
