package nano.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        dialog.setWrapText(true);
        dialog.setMaxHeight(Double.MAX_VALUE);
        dialog.setPrefHeight(Region.USE_COMPUTED_SIZE);
        dialog.setMinHeight(Region.USE_COMPUTED_SIZE);
        setMaxHeight(Double.MAX_VALUE);
        setMinHeight(Region.USE_PREF_SIZE);
        displayPicture.setImage(img);

        // Reserve space for the speaker image and let the text bubble use the
        // remaining width. This keeps both user and Nano messages responsive
        // when the main window is resized.
        var availableWidth = Bindings.max(0, widthProperty().subtract(120));
        dialog.maxWidthProperty().bind(availableWidth);
        dialog.prefWidthProperty().bind(availableWidth);
        fitTextWidth();
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
        dialog.setWrapText(true);
        dialog.setTextOverrun(OverrunStyle.CLIP);
    }

    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    public static DialogBox getNanoDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        return db;
    }

    /**
     * Provides access to the text label so callers can customise a dialog's
     * presentation, such as using a monospace font for ASCII art.
     *
     * @return the dialog text label.
     */
    public Label getDialogLabel() {
        return dialog;
    }

    /**
     * Sizes the text bubble to its content instead of the available row width.
     */
    public void fitTextWidth() {
        dialog.prefWidthProperty().unbind();
        dialog.setPrefWidth(Region.USE_COMPUTED_SIZE);
    }
}
