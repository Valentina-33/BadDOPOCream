package domain.entities;

import domain.model.Position;
import domain.utils.Direction;

/**
 * Inicializa dirección, puntaje y usa helpers de los jugadores
 */
public class Player extends Entity {
    private int score = 0;
    // Útil si va a lanzar hielo
    private Direction direction =  Direction.DOWN;

    public Player(Position position) {
        super(position);
    }
    public Direction getDirection() {return this.direction;}
    public void setDirection(Direction direction) {this.direction = direction;}
    public int getScore() {return score;}
    public void addScore(int pts) {this.score += pts;}
    //Maneja la muerte del jugador
    public void onHitByEnemy(Enemy e) { }
    public void setScore(int playerScore) { this.score = playerScore;}
}
