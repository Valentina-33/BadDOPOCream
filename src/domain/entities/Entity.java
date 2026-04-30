package domain.entities;

import domain.model.Position;

public abstract class Entity {
    protected Position position;
    // PROTECTED: Para que las subclases puedan acceder, pero no sea público
    protected AnimatedSprite animatedSprite;

    protected Entity(Position position) {
        this.position = position;
    }

    // Métodos para obtener/establecer posición
    public Position getPosition() { return this.position; }
    public void setPosition(Position position) { this.position = position; }

    // El método para asignar el sprite ahora será más genérico o manejado por la fábrica externa
    public void setAnimatedSprite(AnimatedSprite sprite) { this.animatedSprite = sprite; }
    public AnimatedSprite getAnimatedSprite() { return this.animatedSprite; }
}