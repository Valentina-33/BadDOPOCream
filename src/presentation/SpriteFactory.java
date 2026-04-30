package presentation;

import domain.entities.AnimatedSprite;
import domain.entities.Sprite;
import domain.game.Flavour;
import domain.utils.Direction;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SpriteFactory {

    private static final Map<String, Sprite> staticSpriteCache = new HashMap<>();
    private static final Map<String, AnimatedSprite> animatedSpriteCache = new HashMap<>();
    private static final Map<String, Image> imageCache = new HashMap<>();

    //  SPRITES ESTÁTICOS 
    public static Sprite getStaticSprite(String resourcePath) {
        return staticSpriteCache.computeIfAbsent(resourcePath, Sprite::new);
    }

    public static Sprite getCactusSprite(boolean isDangerous) {
        if (isDangerous) {
            return getStaticSprite("/cactus-not-safe.png");
        } else {
            return getStaticSprite("/cactus-safe.png");
        }
    }

    //  SPRITES DE JUGADOR 
    public static AnimatedSprite getPlayerSprite(Flavour flavour) {
        String prefix = (flavour != null) ? flavour.prefix() : Flavour.VANILLA.prefix();
        return getAnimatedSprite("player", prefix);
    }

    //  SPRITES DE ENEMIGOS

    public static AnimatedSprite getTrollSprite() {
        return getAnimatedSprite("troll", null);
    }

    public static AnimatedSprite getMacetaSprite() {
        // Definimos las rutas AQUÍ, no en la entidad
        Map<Direction, String> paths = new EnumMap<>(Direction.class);
        paths.put(Direction.LEFT, "/maceta-left.gif");
        paths.put(Direction.RIGHT, "/maceta-right.gif");
        paths.put(Direction.UP, "/maceta-up.gif");
        paths.put(Direction.DOWN, "/maceta-down.gif");
        return getCustomAnimatedSprite("maceta", paths);
    }

    public static AnimatedSprite getNarvalSprite() {
        Map<Direction, String> paths = new EnumMap<>(Direction.class);
        paths.put(Direction.LEFT, "/narval-left.gif");
        paths.put(Direction.RIGHT, "/narval-right.gif");
        paths.put(Direction.UP, "/narval-up.gif");
        paths.put(Direction.DOWN, "/narval-down.gif");
        return getCustomAnimatedSprite("narval", paths);
    }

    public static AnimatedSprite getOrangeSquidSprite() {
        Map<Direction, String> paths = new EnumMap<>(Direction.class);
        paths.put(Direction.LEFT, "/orange-squid-left.gif");
        paths.put(Direction.RIGHT, "/orange-squid-right.gif");
        paths.put(Direction.UP, "/orange-squid-up.gif");
        paths.put(Direction.DOWN, "/orange-squid-down.gif");
        return getCustomAnimatedSprite("orange-squid", paths);
    }

    //  IMÁGENES DE MUERTE 
    public static Image getDeathGif(Flavour flavour) {
        String key = "death-gif-" + (flavour != null ? flavour.name() : "VANILLA");
        return imageCache.computeIfAbsent(key, k -> {
            String path = switch (flavour != null ? flavour : Flavour.VANILLA) {
                case STRAWBERRY -> "/strawberry-death.gif";
                case CHOCOLATE -> "/chocolate-death.gif";
                default -> "/vanilla-death.gif";
            };
            return loadImage(path);
        });
    }

    public static Image getDeathLastFrame(Flavour flavour) {
        String key = "death-frame-" + (flavour != null ? flavour.name() : "VANILLA");
        return imageCache.computeIfAbsent(key, k -> {
            String path = switch (flavour != null ? flavour : Flavour.VANILLA) {
                case STRAWBERRY -> "/strawberry-dead.png";
                case CHOCOLATE -> "/chocolate-dead.png";
                default -> "/vanilla-dead.png";
            };
            return loadImage(path);
        });
    }

    //  MÉTODOS PRIVADOS 

    private static AnimatedSprite getAnimatedSprite(String entityType, String prefix) {
        String cacheKey = entityType + (prefix != null ? "-" + prefix : "");

        return animatedSpriteCache.computeIfAbsent(cacheKey, k -> {
            if (entityType.equals("player")) {
                // Usamos 'prefix' (ej: vanilla)
                return buildAnimatedSprite("/", prefix);
            }

            // La clave es "troll", y el archivo es "/troll-down.gif".
            return buildAnimatedSprite("/", k);
        });
    }

    private static AnimatedSprite getCustomAnimatedSprite(String key, Map<Direction, String> paths) {
        // No cacheamos custom paths por simplicidad ahora, o usamos la key
        return animatedSpriteCache.computeIfAbsent(key, k -> new AnimatedSprite(paths));
    }

    private static AnimatedSprite buildAnimatedSprite(String basePath, String entityName) {
        Map<Direction, String> paths = new EnumMap<>(Direction.class);
        for (Direction dir : Direction.values()) {
            if (dir == Direction.NONE) continue;
            paths.put(dir, basePath + entityName + "-" + dir.name().toLowerCase() + ".gif");
        }
        return new AnimatedSprite(paths);
    }

    private static Image loadImage(String path) {
        try {
            return new ImageIcon(Objects.requireNonNull(SpriteFactory.class.getResource(path))).getImage();
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + path);
            return null;
        }
    }

    public static Sprite getRestartButtonSprite() {
        return getStaticSprite("/btn-restart.png");
    }

    public static Sprite getPauseButtonSprite() {
        return getStaticSprite("/btn-pause.png");
    }
}