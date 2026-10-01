package bany.gui;

import java.io.IOException;

import bany.commands.MessageLevel;
import bany.commands.ResponseMessage;
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

/**
 * Represents one chat message with its text and sender image.
 *
 * <p>Each instance loads the shared dialog FXML layout and can reverse its
 * children to place the avatar on the opposite side of the message.</p>
 */
public class DialogBox extends HBox {
    /** Path to the FXML layout used for Bany responses. */
    private static final String FXML_PATH = "/bany/gui/view/DialogBox.fxml";

    /** Label that displays the message text. */
    @FXML
    private Label dialog;
    /** Image view that displays the sender avatar. */
    @FXML
    private ImageView displayImage;

    /**
     * Creates a dialog box from the FXML layout.
     *
     * @param text message text to display.
     * @param image avatar associated with the message.
     */
    private DialogBox(String text, Image image, String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource(fxmlPath));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayImage.setImage(image);
    }

    /** Reverses the dialog contents so the avatar appears on the left. */
    public void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
    }

    private void setUserStyle() {
        dialog.getStyleClass().add("user-label");
    }

    private void setBotStyle() {
        dialog.getStyleClass().add("bot-label");
    }

    private void setBotStyle(MessageLevel level) {
        dialog.getStyleClass().add("bot-label");
        switch (level) {
            case WARNING -> dialog.getStyleClass().add("warning-label");
            case ERROR -> dialog.getStyleClass().add("error-label");
            default -> dialog.getStyleClass().add("bot-label");
        }
    }

    /**
     * Creates a right-aligned dialog box for a user message.
     *
     * @param text message text to display.
     * @param image user avatar.
     * @return user-message dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        var db = new DialogBox(text, image, FXML_PATH);
        db.setUserStyle();
        return db;
    }

    /**
     * Creates a left-aligned dialog box for a Bany response.
     *
     * @param response response text to display.
     * @param image Bany avatar.
     * @return Bany-response dialog box.
     */
    public static DialogBox getBotDialog(ResponseMessage response, Image image) {
        var db = new DialogBox(response.text(), image, FXML_PATH);
        db.setBotStyle(response.level());
        db.flip();
        return db;
    }

}
