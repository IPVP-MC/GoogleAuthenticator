package org.originmc.googleauthenticator;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class QRCodeRenderer extends MapRenderer {

    private BufferedImage image;

    public QRCodeRenderer(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        if (image != null) {
            canvas.drawImage(0, 0, image);
            image = null;
        }
    }
}
