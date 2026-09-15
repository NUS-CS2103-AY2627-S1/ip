package tianyi.ui;

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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tianyi.Response;

/**
 * Displays a speaker image together with an optional heading, message body, and footer.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private Label heading;

    @FXML
    private Label footer;

    @FXML
    private VBox messageContainer;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box containing the supplied message and display image.
     *
     * @param text Message displayed in the dialog box.
     * @param image Display image for the speaker.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    DialogBox.class.getResource("/view/DialogBox.fxml")
            );
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box layout.", e);
        }

        dialog.setText(text);
        dialog.setMaxWidth(Double.MAX_VALUE);
        footer.setMaxWidth(Double.MAX_VALUE);

        displayPicture.setImage(image);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);

        messageContainer.getStyleClass().add("reply-card");
        messageContainer.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(messageContainer, Priority.ALWAYS);
    }

    /**
     * Creates a dialog box for a user message.
     *
     * @param text Message text.
     * @param image User display image.
     * @return Dialog box aligned for the user.
     */
    public static DialogBox createUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a dialog box for a Tianyi response.
     *
     * @param text Response text.
     * @param image Tianyi display image.
     * @return Dialog box aligned for Tianyi.
     */
    public static DialogBox createTianyiDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();

        return dialogBox;
    }

    /**
     * Creates a normal reply with its title, body, and footer displayed separately.
     *
     * @param response Successful command result.
     * @param image Tianyi display image.
     * @return Reply card with separately styled content sections when present.
     */
    public static DialogBox createTianyiDialog(Response response, Image image) {
        DialogBox dialogBox = createTianyiDialog(response.getMessage(), image);

        if (!response.getHeader().isEmpty()) {
            dialogBox.heading.setText(response.getHeader());
            dialogBox.heading.setVisible(true);
            dialogBox.heading.setManaged(true);
        }

        dialogBox.dialog.setVisible(!response.getMessage().isEmpty());
        dialogBox.dialog.setManaged(!response.getMessage().isEmpty());

        dialogBox.footer.setText(response.getFooter());
        dialogBox.footer.setVisible(!response.getFooter().isEmpty());
        dialogBox.footer.setManaged(!response.getFooter().isEmpty());

        return dialogBox;
    }

    /**
     * Creates an error card with a visible heading as well as error colors.
     *
     * @param text Error message and any correction guidance.
     * @param image Tianyi display image.
     * @return Highlighted error dialog.
     */
    public static DialogBox createErrorDialog(String text, Image image) {
        DialogBox dialogBox = createTianyiDialog(text, image);
        dialogBox.messageContainer.getStyleClass().add("error-card");

        dialogBox.heading.setText("⚠ Command error");
        dialogBox.heading.setVisible(true);
        dialogBox.heading.setManaged(true);

        return dialogBox;
    }

    /**
     * Creates a Tianyi dialog box for displaying the welcome greeting.
     *
     * @param text Welcome message text.
     * @param image Tianyi display image.
     * @return Dialog box with welcome styling.
     */
    public static DialogBox createWelcomeDialog(String text, Image image) {
        DialogBox dialogBox = createTianyiDialog(text, image);
        dialogBox.messageContainer.getStyleClass().add("welcome-card");

        return dialogBox;
    }
}
