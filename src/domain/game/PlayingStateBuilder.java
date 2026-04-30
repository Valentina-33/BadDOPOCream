package domain.game;

/**
 * Patrón Builder:
 * Facilita la creación de instancias complejas de PlayingState.
 * Evita tener múltiples constructores confusos (Telescoping Constructor Anti-pattern).
 */
public class PlayingStateBuilder {

    //  Parámetros Obligatorios 
    private final Game game;

    //  Parámetros Opcionales (con valores por defecto) 
    private int levelNumber = 1;
    private Level customLevel = null;
    private GameMode mode = GameMode.PLAYER;

    // Configuración de IA
    private AIProfile p1AI = null;
    private AIProfile p2AI = null;

    // Configuración de sabores
    private Flavour flavourP1 = Flavour.VANILLA;
    private Flavour flavourP2 = Flavour.VANILLA;

    /**
     * Constructor del Builder. Solo pide lo estrictamente necesario (el juego).
     */
    public PlayingStateBuilder(Game game) {
        this.game = game;
    }

    //  Métodos que retornan 'this' para encadenar llamadas

    public PlayingStateBuilder withLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
        this.customLevel = null;
        return this;
    }

    public PlayingStateBuilder withCustomLevel(Level level) {
        this.customLevel = level;
        this.levelNumber = -1; // -1 indica que es un nivel importado
        return this;
    }

    public PlayingStateBuilder withMode(GameMode mode) {
        this.mode = mode;
        return this;
    }

    public PlayingStateBuilder withPlayer1AI(AIProfile profile) {
        this.p1AI = profile;
        return this;
    }

    public PlayingStateBuilder withPlayer2AI(AIProfile profile) {
        this.p2AI = profile;
        return this;
    }

    public PlayingStateBuilder withPlayer1Flavour(Flavour flavour) {
        this.flavourP1 = flavour;
        return this;
    }

    public PlayingStateBuilder withPlayer2Flavour(Flavour flavour) {
        this.flavourP2 = flavour;
        return this;
    }

    /**
     * Construye la instancia final de PlayingState.
     * Este es el único punto donde se llama al constructor de PlayingState.
     */
    public PlayingState build() {
        return new PlayingState(this);
    }

    //  Getters (Para que PlayingState pueda leer la configuración) 

    public Game getGame() { return game; }
    public int getLevelNumber() { return levelNumber; }
    public Level getCustomLevel() { return customLevel; }
    public GameMode getMode() { return mode; }
    public AIProfile getP1AI() { return p1AI; }
    public AIProfile getP2AI() { return p2AI; }
    public Flavour getFlavourP1() { return flavourP1; }
    public Flavour getFlavourP2() { return flavourP2; }
}