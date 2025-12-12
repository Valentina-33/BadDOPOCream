package domain.entities;
import domain.model.Position;

/**
 * Establece las características básicas de objetos presentes en los mapas.
 */
public abstract class Entity {
    protected Position position;
    protected AnimatedSprite animatedSprite;

    protected Entity(Position position) {
        this.position = position;
    }
    public void setAnimatedSprite(AnimatedSprite sprite) {this.animatedSprite = sprite;}
    public Position getPosition() {return this.position;}
    public void setPosition(Position position) { this.position = position;}
}
