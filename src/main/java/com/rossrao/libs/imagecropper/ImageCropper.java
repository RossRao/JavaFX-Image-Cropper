package com.rossrao.libs.imagecropper;

import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * ImageCropper is a reusable JavaFX UI component that allows users to
 * interactively crop an image using a fixed aspect ratio.
 *
 * <p>Features include:
 * <ul>
 *   <li>Drag-to-position image movement</li>
 *   <li>Zooming via slider</li>
 *   <li>Fixed aspect ratio crop area</li>
 *   <li>Transparent snapshot export</li>
 * </ul>
 *
 * <p>The cropped image can be retrieved using {@link #snap()}.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * Image image = new Image("file:photo.jpg");
 * ImageCropper cropper = new ImageCropper(image, 1, 1);
 * WritableImage result = cropper.snap();
 * }</pre>
 *
 * @author RossRao
 * @since 26.1.0
 */
public class ImageCropper extends VBox {

    private static final double PREVIEW_WIDTH = 580;
    private static final double PREVIEW_HEIGHT = 400;

    private final Image baseImage;

    private Pane previewPanel;
    private Pane layeredPane;
    private ImageView previewView;
    private CropOverlay cropOverlay;

    private double mouseX;
    private double mouseY;

    /**
     * Creates an ImageCropper with a default 1:1 (square) aspect ratio.
     *
     * @param baseImage the image to be cropped
     */
    public ImageCropper(Image baseImage) {
        this(baseImage, 1, 1);
    }

    /**
     * Creates an ImageCropper with a custom fixed aspect ratio.
     *
     * @param baseImage the image to be cropped
     * @param cropRatioWidth aspect ratio width
     * @param cropRatioHeight aspect ratio height
     */
    public ImageCropper(Image baseImage, double cropRatioWidth, double cropRatioHeight) {
        this.baseImage = scaleToFit(baseImage, PREVIEW_WIDTH, PREVIEW_HEIGHT);

        setSpacing(10);
        setPadding(new Insets(10));

        createPreviewPanel();
        createLayeredPane();
        createPreviewView();

        initializeImageScale();
        initializeCropOverlay(cropRatioWidth, cropRatioHeight);
        createZoomSlider();
    }

    /**
     * Captures and returns the currently visible cropped area as a WritableImage.
     *
     * @return cropped image snapshot
     */
    public WritableImage snap() {
        cropOverlay.setDrawStroke(false);

        try {
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            params.setViewport(cropOverlay.getCropRect());

            Rectangle2D rect = cropOverlay.getCropRect();
            WritableImage snapshot = new WritableImage(
                    (int) rect.getWidth(),
                    (int) rect.getHeight()
            );

            layeredPane.snapshot(params, snapshot);
            return snapshot;
        } finally {
            cropOverlay.setDrawStroke(true);
        }
    }

    /* --------------------------------------------------------------------- */
    /* Initialization                                                        */
    /* --------------------------------------------------------------------- */

    private void createPreviewPanel() {
        previewPanel = new StackPane();
        previewPanel.setPrefSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        previewPanel.setMaxSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        previewPanel.setMinSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        getChildren().add(previewPanel);
    }

    private void createLayeredPane() {
        layeredPane = new Pane();
        layeredPane.setPrefSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        previewPanel.getChildren().add(layeredPane);
    }

    private void createPreviewView() {
        previewView = new ImageView(baseImage);
        previewView.setPreserveRatio(true);
        layeredPane.getChildren().add(previewView);
        addDraggableFunctionality();
    }

    private void initializeImageScale() {
        double scale = Math.min(
                PREVIEW_WIDTH / baseImage.getWidth(),
                PREVIEW_HEIGHT / baseImage.getHeight()
        );

        previewView.setFitWidth(baseImage.getWidth() * scale);
        previewView.setFitHeight(baseImage.getHeight() * scale);

        previewView.setLayoutX((PREVIEW_WIDTH - previewView.getFitWidth()) / 2);
        previewView.setLayoutY((PREVIEW_HEIGHT - previewView.getFitHeight()) / 2);
    }

    private void initializeCropOverlay(double ratioW, double ratioH) {
        double displayW = previewView.getFitWidth();
        double displayH = previewView.getFitHeight();

        double cropRatio = ratioW / ratioH;
        double displayRatio = displayW / displayH;

        double cropW;
        double cropH;

        if (cropRatio > displayRatio) {
            cropW = displayW;
            cropH = displayW / cropRatio;
        } else {
            cropH = displayH;
            cropW = cropH * cropRatio;
        }

        cropOverlay = new CropOverlay(
                PREVIEW_WIDTH,
                PREVIEW_HEIGHT,
                cropW,
                cropH
        );

        layeredPane.getChildren().add(cropOverlay);
    }

    private void createZoomSlider() {
        Slider zoomSlider = new Slider(0, 100, 0);
        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> applyZoom(newVal.doubleValue()));
        getChildren().add(zoomSlider);
    }

    /* --------------------------------------------------------------------- */
    /* Interaction Logic                                                     */
    /* --------------------------------------------------------------------- */

    private void addDraggableFunctionality() {
        previewView.setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        previewView.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - mouseX;
            double dy = e.getSceneY() - mouseY;

            clampImagePosition(
                    previewView.getLayoutX() + dx,
                    previewView.getLayoutY() + dy
            );

            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });
    }

    private void applyZoom(double zoomValue) {
        double oldW = previewView.getLayoutBounds().getWidth();
        double oldH = previewView.getLayoutBounds().getHeight();

        double scale = 1 + (zoomValue / 100.0);
        previewView.setFitWidth(baseImage.getWidth() * scale);
        previewView.setFitHeight(baseImage.getHeight() * scale);

        double newW = previewView.getLayoutBounds().getWidth();
        double newH = previewView.getLayoutBounds().getHeight();

        clampImagePosition(
                previewView.getLayoutX() - (newW - oldW) / 2,
                previewView.getLayoutY() - (newH - oldH) / 2
        );
    }

    private void clampImagePosition(double x, double y) {
        Rectangle2D bounds = cropOverlay.getCropRect();
        double imgW = previewView.getLayoutBounds().getWidth();
        double imgH = previewView.getLayoutBounds().getHeight();

        if (x > bounds.getMinX()) x = bounds.getMinX();
        if (y > bounds.getMinY()) y = bounds.getMinY();
        if (x + imgW < bounds.getMaxX()) x = bounds.getMaxX() - imgW;
        if (y + imgH < bounds.getMaxY()) y = bounds.getMaxY() - imgH;

        previewView.setLayoutX(x);
        previewView.setLayoutY(y);
    }

    /* --------------------------------------------------------------------- */
    /* Utilities                                                             */
    /* --------------------------------------------------------------------- */

    private static Image scaleToFit(Image image, double maxW, double maxH) {
        double ratio = Math.min(maxW / image.getWidth(), maxH / image.getHeight());
        int w = (int) (image.getWidth() * ratio);
        int h = (int) (image.getHeight() * ratio);

        WritableImage result = new WritableImage(w, h);
        Canvas canvas = new Canvas(w, h);
        canvas.getGraphicsContext2D().drawImage(image, 0, 0, w, h);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        canvas.snapshot(params, result);

        return result;
    }
}
