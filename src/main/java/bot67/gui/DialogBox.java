package bot67.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/** Represents one chat message and its speaker's avatar. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box layout", e);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
        cropToSquare(image);
        displayPicture.setFitWidth(44);
        displayPicture.setFitHeight(44);
        Rectangle clip = new Rectangle(44, 44);
        clip.setArcWidth(14);
        clip.setArcHeight(14);
        displayPicture.setClip(clip);
        dialog.maxWidthProperty().bind(widthProperty().subtract(70).multiply(0.85));
    }

    /** Creates a right-aligned user message. */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /** Creates a left-aligned Bot67 response. */
    public static DialogBox getBotDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        box.flip();
        box.dialog.maxWidthProperty().unbind();
        box.dialog.maxWidthProperty().bind(box.widthProperty().subtract(70));
        return box;
    }

    /** Highlights errors without adding a heading to the response text. */
    public void highlightError() {
        dialog.getStyleClass().add("error-label");
    }

    /** Shows the original banner and personality art above the welcome text. */
    public void showWelcomeArt(String banner, String art) {
        Text bannerText = new Text(banner.stripTrailing());
        bannerText.setFont(Font.font("Monospaced", 12));
        bannerText.setFill(Color.web("#913553"));
        bannerText.setAccessibleText("Bot67 ASCII banner");
        VBox artwork = new VBox(12, bannerText, new PersonalityArt(art));
        artwork.setAlignment(Pos.CENTER);
        dialog.setGraphic(artwork);
        dialog.setContentDisplay(ContentDisplay.TOP);
        dialog.setGraphicTextGap(12);
    }

    /** Places the avatar on the left and applies the bot bubble style. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /** Crops a wide or tall source image around its center for an avatar-shaped view. */
    private void cropToSquare(Image image) {
        double side = Math.min(image.getWidth(), image.getHeight());
        double x = (image.getWidth() - side) / 2;
        double y = (image.getHeight() - side) / 2;
        displayPicture.setViewport(new Rectangle2D(x, y, side, side));
    }
}
