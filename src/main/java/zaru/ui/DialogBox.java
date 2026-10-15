package zaru.ui;

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
import zaru.parser.Response;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    private static final double NON_TEXT_WIDTH = 62;

    @FXML
    private Label dialogLabel;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box with the given text and image.
     * @param text Text to display.
     * @param image Image to display.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box.", e);
        }

        dialogLabel.setText(text);
        displayPicture.setImage(image);
        dialogLabel.maxWidthProperty().bind(widthProperty().subtract(NON_TEXT_WIDTH));
        assert dialogLabel != null : "Dialog label should have been injected by FXML.";
        assert displayPicture != null : "Display picture should have been injected by FXML.";
    }

    /**
     * Formats the dialog box with the chatbot image on the left.
     */
    private void formatAsZaruDialog() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        dialogLabel.getStyleClass().add("reply-label");
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for user input.
     *
     * @param text User input to display.
     * @param image User display image.
     * @return Dialog box for the user.
     */
    public static DialogBox createUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a dialog box for a chatbot response.
     *
     * @param response Chatbot response to display.
     * @param image Chatbot display image.
     * @return Dialog box for the chatbot.
     */
    public static DialogBox createZaruDialog(Response response, Image image) {
        assert response != null : "Dialog box requires a chatbot response.";
        DialogBox dialogBox = new DialogBox(response.getText(), image);
        dialogBox.formatAsZaruDialog();
        dialogBox.changeDialogStyle(response.getType());
        return dialogBox;
    }

    /** Applies visual emphasis appropriate to the response category. */
    private void changeDialogStyle(Response.ResponseType responseType) {
        if (responseType == Response.ResponseType.ERROR) {
            dialogLabel.getStyleClass().add("error-label");
        }
    }
}
