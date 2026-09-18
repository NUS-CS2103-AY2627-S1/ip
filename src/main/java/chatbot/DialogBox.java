package chatbot;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private VBox dialogContainer;
    @FXML
    private ImageView displayPicture;

    /**
     * Loads the dialog-box layout and populates it with the supplied message and image.
     *
     * @param text Message to display.
     * @param img Image representing the speaker.
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load DialogBox.fxml", e);
        }

        Label message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(500);
        message.setTextFill(Color.WHITE);
        message.getStyleClass().add("message-text");
        dialogContainer.getChildren().add(message);
        dialogContainer.setMinHeight(Region.USE_PREF_SIZE);
        dialogContainer.setMaxHeight(Region.USE_PREF_SIZE);
        if (text.startsWith("\\ \\ / /|")) {
            int bodyStart = text.indexOf("\n\n");
            dialogContainer.getChildren().clear();
            Label banner = new Label(text.substring(0, bodyStart + 2));
            banner.getStyleClass().add("banner-text");
            Label intro = new Label(text.substring(bodyStart + 2));
            intro.setWrapText(true);
            intro.setMaxWidth(500);
            intro.getStyleClass().add("message-text");
            dialogContainer.getChildren().addAll(banner, intro);
        }
        displayPicture.setImage(img);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialogContainer.getStyleClass().add("reply-label");
    }

    /**
     * Returns a dialog box aligned for a user's message.
     *
     * @param text Message to display.
     * @param img Image representing the user.
     * @return Dialog box containing the user's message and image.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Returns a dialog box aligned for the chatbot's message.
     *
     * @param text Message to display.
     * @param img Image representing the chatbot.
     * @return Dialog box containing the chatbot's message and image.
     */
    public static DialogBox getYunDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        return db;
    }
}
