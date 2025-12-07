package domain.entities;
import domain.model.Position;
import domain.game.Level;
import java.awt.Graphics2D;

public abstract class Fruit extends Entity{
    protected final Sprite sprite;
    private final int scoreValue;
    private boolean collected = false;
    private boolean frozen = false;

    public Fruit(Position position, int scoreValue, Sprite sprite) {
        super(position);
        this.sprite = sprite;
        this.scoreValue = scoreValue;
    }

    public int getScoreValue() {return this.scoreValue;}
    public boolean isCollected() {return this.collected;}
    public void collect() {this.collected = true;}
    public void update(Level level) {

    }

    public abstract int getPoints();

    public boolean isFrozen() {return this.frozen;}
    public void freeze() {this.frozen = true;}
    public void unfreeze() {this.frozen = false;}

    //Size of the fruits
    public void render(Graphics2D g, int tileSize) {
        int baseX = position.getCol() * tileSize;
        int baseY = position.getRow() * tileSize;

        int size = (int) (tileSize * 0.8);
        int offset = (tileSize - size) / 2;

        int x = baseX + offset;
        int y = baseY + offset;

        if (!frozen) {
            sprite.draw(g, x, y, size, size);
        } else {
            sprite.draw(g, x, y, size, size);
        }
    }
}
