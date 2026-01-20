package com.rossrao.libs.imagecropper;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * CropOverlay renders a darkened overlay with a transparent crop window.
 * This class is used internally by {@link ImageCropper}.
 *
 * <p>The crop area is fixed in size and centered within the overlay.</p>
 *
 * @author RossRao
 * @since 26.1.0
 */
final class CropOverlay extends Canvas {

    private final double overlayWidth;
    private final double overlayHeight;
    private final double cropWidth;
    private final double cropHeight;

    private boolean drawStroke = true;

    CropOverlay(double overlayWidth, double overlayHeight, double cropWidth, double cropHeight) {
        super(overlayWidth, overlayHeight);
        this.overlayWidth = overlayWidth;
        this.overlayHeight = overlayHeight;
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;

        setMouseTransparent(true);
        draw();
    }

    public void setDrawStroke(boolean draw) {
        if (drawStroke != draw) {
            drawStroke = draw;
            draw();
        }
    }

    public Rectangle2D getCropRect() {
        double x = (overlayWidth - cropWidth) / 2;
        double y = (overlayHeight - cropHeight) / 2;
        return new Rectangle2D(x, y, cropWidth, cropHeight);
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, overlayWidth, overlayHeight);

        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRect(0, 0, overlayWidth, overlayHeight);

        Rectangle2D crop = getCropRect();
        gc.clearRect(crop.getMinX(), crop.getMinY(), crop.getWidth(), crop.getHeight());

        if (drawStroke) {
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeRect(
                    crop.getMinX(),
                    crop.getMinY(),
                    crop.getWidth(),
                    crop.getHeight()
            );
        }
    }
}
