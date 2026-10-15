package duchess.ui.gui.components;

import java.io.IOException;
import java.net.URL;

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
 * Displays the profile picture and text in
 * a GUI format.
 */
public class DialogBox extends HBox {
    private static final Image USER_IMAGE = new Image(DialogBox.class.getResourceAsStream("/images/Soyjak.jpg"));
    private static final Image DUCHESS_IMAGE = new Image(DialogBox.class.getResourceAsStream("/images/Gigachad.jpg"));

    @FXML
    private Label textDisplay;
    @FXML
    private ImageView profilePictureDisplay;

    private DialogBox(String text, Image picture) throws IllegalStateException {
        assert (text != null) : "Dialog text must not be null";
        assert (picture != null) : "Dialog image must not be null";

        loadLayout();

        assert (this.textDisplay != null)
                : "textDisplay must be injected from DialogBox.fxml";
        assert (this.profilePictureDisplay != null)
                : "profilePictureDisplay must be injected from DialogBox.fxml";

        this.textDisplay.setText(text);
        this.profilePictureDisplay.setImage(picture);


    }

    private void loadLayout() {
        URL fxmlResource = DialogBox.class.getResource("/view/DialogBox.fxml");
        assert (fxmlResource != null) : "DialogBox.fxml must be present in /resources/view/";

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlResource);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Dialog box could not be loaded", e);
        }
    }

    private void configureAsDuchessReply() {
        reverseChildren();
        applyReplyStyle();
    }

    private void reverseChildren() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());

        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
    }

    private void applyReplyStyle() {
        this.textDisplay.getStyleClass().add("reply-label");
    }

    public static DialogBox getUserDialogBox(String input) {
        return new DialogBox(input, USER_IMAGE);
    }

    public static DialogBox getDuchessDialogBox(String response) {
        DialogBox dialogBox = new DialogBox(response, DUCHESS_IMAGE);
        dialogBox.configureAsDuchessReply();
        return dialogBox;
    }
}
