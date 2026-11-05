package larp;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents one message in the conversation between the user and Larp.
 */
public class DialogBox extends HBox {
    @FXML
    private Label speakerBadge;

    @FXML
    private Label dialog;

    /**
     * Loads the reusable dialog layout and fills it with message content.
     *
     * @param text Message to display.
     */
    private DialogBox(String text) {
        URL dialogBoxUrl = Objects.requireNonNull(
                DialogBox.class.getResource("/view/DialogBox.fxml"),
                "DialogBox.fxml is missing");
        FXMLLoader fxmlLoader = new FXMLLoader(dialogBoxUrl);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box layout", e);
        }

        dialog.setText(text);
    }

    /**
     * Creates a right-aligned message written by the user.
     *
     * @param text User's command.
     * @return Dialog box styled as a user message.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.speakerBadge.setText("YOU");
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.getChildren().setAll(List.of(dialogBox.dialog, dialogBox.speakerBadge));
        return dialogBox;
    }

    /**
     * Creates a left-aligned message written by Larp.
     *
     * @param text Larp's response.
     * @return Dialog box styled as a Larp message.
     */
    public static DialogBox getLarpDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.speakerBadge.setText("L");
        dialogBox.getStyleClass().add("larp-dialog");
        return dialogBox;
    }

    /**
     * Creates a visually distinct response that explains an invalid command or data problem.
     *
     * @param text Error response to display.
     * @return Dialog box styled as an error and marked with an exclamation point.
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.speakerBadge.setText("!");
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }
}
