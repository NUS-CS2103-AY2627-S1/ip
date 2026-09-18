package chris.ui;

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

/**
 * Displays a chatbot message together with its sender's image.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String message, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load dialog box layout.", exception);
        }

        dialog.setText(message);
        displayPicture.setImage(image);
        if (image == null) {
            displayPicture.setManaged(false);
            displayPicture.setVisible(false);
        }
    }

    /**
     * Creates a right-aligned dialog for a user message.
     *
     * @param message User's message.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String message) {
        DialogBox dialogBox = new DialogBox(message, null);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for a Chris response.
     *
     * @param message Chris's response.
     * @param image Chris's display image.
     * @return Chris dialog box.
     */
    public static DialogBox getChrisDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("chris-dialog");
        return dialogBox;
    }

    /**
     * Creates a visually highlighted dialog for an error reported by Chris.
     *
     * @param message Error explanation.
     * @param image Chris's display image.
     * @return Highlighted Chris error dialog box.
     */
    public static DialogBox getChrisErrorDialog(String message, Image image) {
        DialogBox dialogBox = getChrisDialog(message, image);
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }

    /**
     * Creates a Chris welcome dialog that preserves ASCII-art alignment.
     *
     * @param message Chris's welcome message.
     * @param image Chris's display image.
     * @return Chris welcome dialog box.
     */
    public static DialogBox getChrisWelcomeDialog(String message, Image image) {
        DialogBox dialogBox = getChrisDialog(message, image);
        dialogBox.getStyleClass().add("welcome-dialog");
        dialogBox.dialog.setStyle("-fx-font-family: monospace;");
        return dialogBox;
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
