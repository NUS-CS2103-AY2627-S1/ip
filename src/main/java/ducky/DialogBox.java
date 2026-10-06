package ducky;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * A chat bubble showing a speaker's display picture next to their message.
 */
public class DialogBox extends HBox {
    private static final String WELCOME_HEADING = "DUCKY";
    private static final double DIALOG_CONTENT_WIDTH = 280;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load the dialog box", e);
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        displayPicture.setClip(new Circle(22.5, 22.5, 22.5));
        dialog.getStyleClass().add("user-label");
        dialog.maxWidthProperty().bind(widthProperty().subtract(55));
    }

    /**
     * Reverses the order of the dialog box's children and aligns them to the top left,
     * so that the display picture is shown on the left instead of the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().remove("user-label");
        dialog.getStyleClass().add("ducky-label");
    }

    /**
     * Creates a dialog box for a message from the user.
     *
     * @param text the message text.
     * @param img the user's display picture.
     * @return the dialog box.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a dialog box for a message from Ducky.
     *
     * @param text the message text.
     * @param img Ducky's display picture.
     * @return the dialog box.
     */
    public static DialogBox getDuckyDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates Ducky's welcome dialog with a bold heading.
     *
     * @param text the welcome message text.
     * @param img Ducky's display picture.
     * @return the welcome dialog box.
     */
    public static DialogBox getWelcomeDialog(String text, Image img) {
        DialogBox dialogBox = getDuckyDialog(text, img);
        Text heading = new Text(WELCOME_HEADING);
        heading.getStyleClass().add("welcome-heading");
        Text body = new Text(text.substring(WELCOME_HEADING.length()));
        body.getStyleClass().add("welcome-body");
        TextFlow welcomeText = new TextFlow(heading, body);
        welcomeText.setPrefWidth(DIALOG_CONTENT_WIDTH);
        welcomeText.setMaxWidth(DIALOG_CONTENT_WIDTH);
        dialogBox.dialog.setText(null);
        dialogBox.dialog.setGraphic(welcomeText);
        dialogBox.dialog.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        return dialogBox;
    }

    /**
     * Creates an error dialog box for a message from Ducky.
     *
     * @param text the error message text.
     * @param img Ducky's display picture.
     * @return the error dialog box.
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox dialogBox = getDuckyDialog(text, img);
        dialogBox.dialog.getStyleClass().remove("ducky-label");
        dialogBox.dialog.getStyleClass().add("error-label");
        return dialogBox;
    }

    /**
     * Creates a successful-action dialog box for a message from Ducky.
     *
     * @param text the response text.
     * @param img Ducky's display picture.
     * @return the successful-action dialog box.
     */
    public static DialogBox getSuccessDialog(String text, Image img) {
        DialogBox dialogBox = getDuckyDialog(text, img);
        dialogBox.dialog.getStyleClass().remove("ducky-label");
        dialogBox.dialog.getStyleClass().add("success-label");
        return dialogBox;
    }
}
