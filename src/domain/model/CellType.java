package domain.model;

/**
 * Los tipos de celdas son constantes y definitivos. Algunos se pueden atravesar.
 */
public enum CellType {

    EMPTY(true),
    PILE_SNOW(true),
    METALLIC_WALL(false),
    RED_WALL(false),
    YELLOW_WALL(false),
    ICE_BLOCK(false),
    IGLOO_AREA(false),
    FLOOR(true),
    PLAYER_ICE(false);

    private final Boolean traversable;

    CellType(Boolean traversable) {
        this.traversable = traversable; }

    public Boolean isTraversable() {return traversable; }
}
