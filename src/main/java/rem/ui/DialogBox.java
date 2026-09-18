package rem.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Displays a compact user command or a wider application response with an optional error heading.
 */
public class DialogBox extends HBox {
    private final double messageWidthRatio;
    @FXML
    private Label dialog;
    @FXML
    private Label errorHeading;
    @FXML
    private VBox message;

    /**
     * Creates a normal message aligned according to its speaker.
     *
     * @param text Message text.
     * @param isUser Whether this message belongs to the user.
     */
    public DialogBox(String text, boolean isUser) {
        this(text, isUser, false);
    }

    /**
     * Creates a message with a visible, accessible heading when an application error occurs.
     *
     * @param text Message text.
     * @param isUser Whether this message belongs to the user.
     * @param isError Whether the application response describes a failure.
     */
    public DialogBox(String text, boolean isUser, boolean isError) {
        messageWidthRatio = isUser ? 0.85 : 0.94;
        loadView();
        dialog.setText(text);
        errorHeading.setVisible(isError);
        errorHeading.setManaged(isError);
        message.maxWidthProperty().bind(widthProperty().multiply(messageWidthRatio));
        if (isUser) {
            setAlignment(Pos.TOP_RIGHT);
            message.getStyleClass().add("user-message");
            message.setAccessibleText("You: " + text);
        } else {
            if (text.contains("\n")) {
                message.prefWidthProperty().bind(widthProperty().multiply(messageWidthRatio));
            }
            message.setAccessibleText((isError ? "Error: " : "RemBot: ") + text);
        }
        if (isError) {
            message.getStyleClass().add("error-message");
        }
    }

    @Override
    protected double computePrefHeight(double width) {
        // Use the proposed width during resizing, before the width binding has been updated.
        return width < 0 ? super.computePrefHeight(width) : message.prefHeight(width * messageWidthRatio);
    }

    @Override
    protected double computeMinHeight(double width) {
        return computePrefHeight(width);
    }

    private void loadView() {
        FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load chat message", e);
        }
    }
}
