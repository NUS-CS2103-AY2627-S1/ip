package wwaffle.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/** Reusable compact artwork, attribution, and lyric component. */
public class MusicCard extends HBox {
    /**
     * Creates a compact track card using the established music styling.
     *
     * @param imagePath Classpath location of the artwork.
     * @param attribution Artist and song title.
     * @param excerpt User-supplied lyric excerpt.
     */
    public MusicCard(String imagePath, String attribution, String excerpt) {
        super(14);
        Font.loadFont(MusicCard.class.getResourceAsStream("/fonts/Manrope.ttf"), 14);
        Font.loadFont(MusicCard.class.getResourceAsStream("/fonts/Inter.ttf"), 15);
        getStyleClass().add("goodbye-music-card");
        setAlignment(Pos.CENTER_LEFT);
        setMinWidth(0);
        ImageView artwork = new ImageView(new Image(MusicCard.class
                .getResourceAsStream(imagePath)));
        artwork.setFitWidth(72);
        artwork.setFitHeight(72);
        artwork.setPreserveRatio(true);
        artwork.setAccessibleText(attribution + " artwork");
        Label metadata = new Label(attribution);
        metadata.getStyleClass().add("goodbye-music-metadata");
        metadata.setWrapText(true);
        metadata.setMinWidth(0);
        metadata.setMaxWidth(Double.MAX_VALUE);
        metadata.setMinHeight(Region.USE_PREF_SIZE);
        Label lyric = new Label(excerpt);
        lyric.getStyleClass().add("goodbye-music-lyric");
        lyric.setWrapText(true);
        lyric.setMinWidth(0);
        // Retain every wrapped line, including when the window becomes narrower.
        lyric.setMaxWidth(Double.MAX_VALUE);
        lyric.setMinHeight(Region.USE_PREF_SIZE);
        lyric.setMaxHeight(Double.MAX_VALUE);
        lyric.setTooltip(new Tooltip(excerpt));
        VBox copy = new VBox(6, metadata, lyric);
        copy.setMinWidth(0);
        copy.setMinHeight(Region.USE_PREF_SIZE);
        setMinHeight(Region.USE_PREF_SIZE);
        HBox.setHgrow(copy, Priority.ALWAYS);
        getChildren().addAll(artwork, copy);
    }
}
