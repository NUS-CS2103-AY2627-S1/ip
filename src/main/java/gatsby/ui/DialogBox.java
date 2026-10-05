package gatsby.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/** Represents one speaker-labelled message in Gatsby's conversation. */
public class DialogBox extends HBox {
    /** Shows whether the message came from the user or Gatsby. */
    @FXML
    private Label speaker;

    /** Displays the message text. */
    @FXML
    private Label dialog;

    /** Holds the speaker label and message text as one visual card. */
    @FXML
    private VBox messageCard;

    /** Displays Gatsby's small circular avatar beside his messages. */
    @FXML
    private ImageView gatsbyImage;

    /** Displays the user's small circular avatar beside user messages. */
    @FXML
    private ImageView userImage;

    /** Loads the reusable dialog-box view and fills its text. */
    private DialogBox(String text, String speakerName, boolean isGatsby, boolean isError) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load Gatsby's dialog layout", e);
        }

        speaker.setText(speakerName);
        dialog.setText(text);
        makeCircular(gatsbyImage);
        makeCircular(userImage);
        if (isGatsby) {
            gatsbyImage.setVisible(true);
            gatsbyImage.setManaged(true);
            flip();
            if (isError) {
                getStyleClass().add("error-dialog");
            }
        } else {
            userImage.setVisible(true);
            userImage.setManaged(true);
            getStyleClass().add("user-dialog");
        }
    }

    /** Clips an avatar to a small circle so it stays visually subtle. */
    private void makeCircular(ImageView imageView) {
        imageView.setClip(new Circle(16.0, 16.0, 16.0));
    }

    /** Aligns a Gatsby message on the left side of the conversation. */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("gatsby-dialog");
        HBox.setHgrow(messageCard, Priority.ALWAYS);
    }

    /**
     * Creates a right-aligned message containing user input.
     *
     * @param text the user's command
     * @return a dialog box for the user message
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, "You", false, false);
    }

    /**
     * Creates a left-aligned message containing Gatsby's response.
     *
     * @param text Gatsby's response
     * @return a dialog box for Gatsby's message
     */
    public static DialogBox getGatsbyDialog(String text) {
        return new DialogBox(text, "Gatsby", true, false);
    }

    /**
     * Creates a left-aligned Gatsby response, optionally styled as an error.
     *
     * @param text Gatsby's response
     * @param isError whether the response should use error styling
     * @return a dialog box for Gatsby's response
     */
    public static DialogBox getGatsbyDialog(String text, boolean isError) {
        return new DialogBox(text, "Gatsby", true, isError);
    }
}
