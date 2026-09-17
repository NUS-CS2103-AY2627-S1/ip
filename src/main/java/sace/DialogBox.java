package sace;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Displays one chat message together with the speaker's avatar and name.
 */
public class DialogBox extends HBox {
    private static final String DIALOG_BOX_FXML = "/view/DialogBox.fxml";

    @FXML
    private Label dialog;
    @FXML
    private Label speakerName;
    @FXML
    private ImageView displayPicture;
    @FXML
    private StackPane avatarFrame;
    @FXML
    private VBox bubbleContainer;

    /**
     * Loads the reusable dialog layout and configures it for one speaker.
     */
    private DialogBox(String text, Image image, boolean isUser) {
        FXMLLoader fxmlLoader = new FXMLLoader(
                DialogBox.class.getResource(DIALOG_BOX_FXML));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load DialogBox.fxml", e);
        }

        dialog.setText(text);
        configureSpeaker(image, isUser);
    }

    /**
     * Creates a right-aligned dialog for a command entered by the user.
     *
     * @param text user's command.
     * @param image portrait displayed beside the command.
     * @return configured user dialog.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image, true);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for Sace's response.
     *
     * @param text Sace's response.
     * @param image portrait displayed beside the response.
     * @return configured bot dialog.
     */
    public static DialogBox getBotDialog(String text, Image image) {
        return new DialogBox(text, image, false);
    }

    /**
     * Creates a visually prominent dialog for a command error.
     *
     * @param text Sace's explanation of the command error.
     * @param image portrait displayed beside the response.
     * @return configured error dialog.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image, false);
        dialogBox.speakerName.setText("SACE  -  COMMAND ALERT");
        dialogBox.bubbleContainer.getStyleClass().add("error-bubble");
        return dialogBox;
    }

    /**
     * Selects the correct avatar, label, and color treatment for a speaker.
     */
    private void configureSpeaker(Image image, boolean isUser) {
        displayPicture.setImage(image);
        if (isUser) {
            speakerName.setText("YOU");
            avatarFrame.getStyleClass().add("user-avatar");
            bubbleContainer.getStyleClass().add("user-bubble");
        } else {
            speakerName.setText("SACE  -  MOONLIT STRATEGIST");
            avatarFrame.getStyleClass().add("bot-avatar");
            bubbleContainer.getStyleClass().add("bot-bubble");
        }
    }

    /**
     * Reverses the row so user messages and their avatar sit on the right.
     */
    private void flip() {
        ObservableList<Node> contents = FXCollections.observableArrayList(getChildren());
        Collections.reverse(contents);
        getChildren().setAll(contents);
        setAlignment(Pos.TOP_RIGHT);
    }
}
