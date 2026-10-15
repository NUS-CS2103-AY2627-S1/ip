package lumi.ui;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lumi.exception.LumiException;

/**
 * Displays a compact chat message using sender-specific presentation.
 */
public class DialogBox extends HBox {
    private static final double USER_MESSAGE_WIDTH_RATIO = 0.72;
    private static final double ASSISTANT_ROW_NON_MESSAGE_WIDTH = 64;

    @FXML
    private Label dialog;
    @FXML
    private StackPane botMark;

    /**
     * Creates a dialog box for a message and its sender.
     *
     * @param message Message to display.
     */
    private DialogBox(String message) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException error) {
            throw new IllegalStateException("Unable to load the dialog box layout.", error);
        }

        assert dialog != null : "DialogBox.fxml must inject dialog";
        assert botMark != null : "DialogBox.fxml must inject botMark";
        dialog.setText(message.strip());
    }

    /**
     * Creates a right-aligned dialog for a message from the user.
     *
     * @param message User's message.
     * @return Right-aligned user dialog.
     */
    public static DialogBox getUserDialog(String message) {
        DialogBox dialogBox = new DialogBox(message);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.botMark.setManaged(false);
        dialogBox.botMark.setVisible(false);
        dialogBox.dialog.getStyleClass().add("user-message");
        dialogBox.dialog.maxWidthProperty().bind(Bindings.max(
                140, dialogBox.widthProperty().multiply(USER_MESSAGE_WIDTH_RATIO)));
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for a response from Lumi.
     *
     * @param message Lumi's response.
     * @return Left-aligned Lumi dialog.
     */
    public static DialogBox getLumiDialog(String message) {
        DialogBox dialogBox = new DialogBox(message);
        dialogBox.botMark.setAccessibleText("Lumi");
        dialogBox.dialog.maxWidthProperty().bind(Bindings.max(
                180, dialogBox.widthProperty().subtract(ASSISTANT_ROW_NON_MESSAGE_WIDTH)));
        if (isErrorMessage(message)) {
            dialogBox.dialog.getStyleClass().add("error-message");
            dialogBox.dialog.setAccessibleText("Error: " + message.strip());
        } else {
            dialogBox.dialog.getStyleClass().add("assistant-message");
        }
        return dialogBox;
    }

    /**
     * Identifies responses that should receive the prominent error treatment.
     *
     * @param message Lumi response to inspect.
     * @return True when at least one response line starts with Lumi's error marker.
     */
    static boolean isErrorMessage(String message) {
        return message.lines()
                .map(String::stripLeading)
                .anyMatch(line -> line.startsWith(LumiException.ERROR_PREFIX));
    }
}
