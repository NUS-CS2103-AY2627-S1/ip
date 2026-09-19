package gary.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;

import gary.ui.ChatResponse;
import gary.ui.ResponseType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * Displays one message and identifies its speaker.
 */
public class DialogBox extends HBox {
    private static final double GARY_MESSAGE_WIDTH_RATIO = 0.82;
    private static final double HELP_MESSAGE_WIDTH_RATIO = 0.94;
    private static final double USER_MESSAGE_WIDTH_RATIO = 0.72;

    @FXML
    private StackPane avatarFrame;
    @FXML
    private Label dialog;

    private DialogBox(String text) {
        URL view = Objects.requireNonNull(
                DialogBox.class.getResource("/view/DialogBox.fxml"),
                "DialogBox.fxml is missing");
        FXMLLoader loader = new FXMLLoader(view);
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load dialog box", e);
        }

        dialog.setText(text);
    }

    /**
     * Returns a right-aligned user message.
     *
     * @param text Message text.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.avatarFrame.setManaged(false);
        dialogBox.avatarFrame.setVisible(false);
        dialogBox.bindMessageWidth(USER_MESSAGE_WIDTH_RATIO);
        return dialogBox;
    }

    /**
     * Returns a left-aligned Gary response with styling based on its type.
     *
     * @param response Typed response to display.
     * @return Styled Gary dialog box.
     */
    public static DialogBox getGaryDialog(ChatResponse response) {
        String message = response.type() == ResponseType.ERROR
                ? "⚠  " + response.message()
                : response.message();
        DialogBox dialogBox = new DialogBox(message);
        dialogBox.getStyleClass().add("gary-dialog");
        if (response.type() == ResponseType.ERROR) {
            dialogBox.getStyleClass().add("error-dialog");
        } else if (response.type() == ResponseType.FAREWELL) {
            dialogBox.getStyleClass().add("farewell-dialog");
        } else if (response.type() == ResponseType.HELP) {
            dialogBox.getStyleClass().add("help-dialog");
        }
        double widthRatio = response.type() == ResponseType.HELP
                ? HELP_MESSAGE_WIDTH_RATIO
                : GARY_MESSAGE_WIDTH_RATIO;
        dialogBox.bindMessageWidth(widthRatio);
        dialogBox.flip();
        return dialogBox;
    }

    private void bindMessageWidth(double widthRatio) {
        dialog.maxWidthProperty().bind(widthProperty().multiply(widthRatio));
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
