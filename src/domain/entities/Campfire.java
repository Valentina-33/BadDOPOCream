package domain.entities;

import domain.game.Level;
import domain.model.*;

/**
 * La fogata elimina al jugador si lo toca
 * Puede ser apagada colocando hielo encima y rompiéndolo
 * Se vuelve a encender después de 10 segundos
 * Los enemigos no sufren daño al tocarla
 */
public class Campfire extends Entity {

    public static final Sprite SPRITE_ON = new Sprite("/campfire-on.png");
    public static final Sprite SPRITE_OFF = new Sprite("/campfire-off.png");

    private static final int TICKS_TO_RELIGHT = 600;

    private boolean isLit = true;
    private int relightCounter = 0;

    public Campfire(Position position) {
        super(position);
    }

    public void update(Level level) {
        Board board = level.getBoard();

        if (!isLit) {
            relightCounter++;

            if (relightCounter >= TICKS_TO_RELIGHT) {
                relight(board);
            }
        }
    }

    public void extinguish(Board board) {
        if (!isLit) return;

        isLit = false;
        relightCounter = 0;
        board.setCellType(position, CellType.CAMPFIRE_OFF);
    }

    private void relight(Board board) {
        isLit = true;
        relightCounter = 0;
        board.setCellType(position, CellType.CAMPFIRE_ON);
    }

    public void light(Board board) {
        relight(board);
    }

    public boolean isLit() {
        return isLit;
    }

    public int getTicksUntilRelight() {
        return isLit ? 0 : TICKS_TO_RELIGHT - relightCounter;
    }

    public int getSecondsUntilRelight() {
        return getTicksUntilRelight() / 60;
    }

    public float getRelightProgress() {
        return isLit ? 0f : (float) relightCounter / TICKS_TO_RELIGHT;
    }

    // Obtener el sprite correcto según el estado
    public Sprite getCurrentSprite() {
        return isLit ? SPRITE_ON : SPRITE_OFF;
    }
}
