package domain.entities;

import domain.game.Level;
import domain.model.Board;
import domain.model.CellType;
import domain.model.Position;
import presentation.SpriteFactory;

public class Campfire extends Entity {

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

    public boolean isLit() { return isLit; }

    // Helper para que el Renderer pida el sprite
    public Sprite getCurrentSprite() {
        return isLit
                ? SpriteFactory.getStaticSprite("/campfire-on.png")
                : SpriteFactory.getStaticSprite("/campfire-off.png");
    }
}