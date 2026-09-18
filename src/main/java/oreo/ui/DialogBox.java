package oreo.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
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

/** Represents one speaker message in the conversation. */
public class DialogBox extends HBox {
    @FXML private Label dialog;
    @FXML private Label speakerName;
    @FXML private VBox messageContent;
    @FXML private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box.", e);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
        messageContent.getStyleClass().add("message-content");
        HBox.setHgrow(messageContent, Priority.ALWAYS);
        displayPicture.fitWidthProperty().bind(Bindings.createDoubleBinding(
                () -> Math.max(64.0, Math.min(120.0, getWidth() * 0.12)), widthProperty()));
        displayPicture.fitHeightProperty().bind(displayPicture.fitWidthProperty());
        speakerName.setManaged(false);
        speakerName.setVisible(false);
    }

    /** Creates a user message. */
    public static DialogBox user(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        box.getStyleClass().add("user-message");
        box.dialog.getStyleClass().add("user-label");
        box.speakerName.setText("You");
        box.speakerName.setManaged(true);
        box.speakerName.setVisible(true);
        box.speakerName.getStyleClass().add("user-name");
        return box;
    }

    /** Creates a reply message with the speaker on the left. */
    public static DialogBox reply(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        ObservableList<Node> children = FXCollections.observableArrayList(box.getChildren());
        Collections.reverse(children);
        box.getChildren().setAll(children);
        box.setAlignment(Pos.TOP_LEFT);
        box.getStyleClass().add("bot-message");
        box.dialog.getStyleClass().add("reply-label");
        box.speakerName.setText("Oreo");
        box.speakerName.setManaged(true);
        box.speakerName.setVisible(true);
        box.speakerName.getStyleClass().add("bot-name");
        box.displayPicture.setScaleX(-1);
        return box;
    }

    /** Marks this chatbot response as an error while retaining its readable reply layout. */
    public void markAsError() {
        getStyleClass().add("error-message");
        dialog.getStyleClass().add("error-label");
    }
}
