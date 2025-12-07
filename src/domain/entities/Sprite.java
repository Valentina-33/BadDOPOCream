package domain.entities;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Sprite {

    private final Image image;

    public Sprite(String resourcePath) {
        this.image = new ImageIcon(
                Objects.requireNonNull(getClass().getResource(resourcePath))
        ).getImage();
    }

    public void draw(Graphics2D g, int x, int y, int w, int h) {
        g.drawImage(image, x, y, w, h, null);
    }
}
