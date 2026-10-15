package doe;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

/**
 * Displays one speaker image beside a wrapped chat message.
 */
public class DialogBox extends HBox {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;
    @FXML
    private Label speaker;
    @FXML
    private Label timestamp;

    private DialogBox(String text, Image image, String speakerName) {
        FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a dialog box", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        speaker.setText(speakerName);
        timestamp.setText(LocalTime.now().format(TIME_FORMAT));
        // Keep avatar space proportional to the row so narrow windows leave room for text.
        displayPicture.fitWidthProperty().bind(Bindings.min(88, Bindings.max(40, widthProperty().subtract(360))));
        displayPicture.fitHeightProperty().bind(displayPicture.fitWidthProperty());
    }

    /**
     * Creates a right-aligned message from the user.
     *
     * @param text Message text.
     * @param image User display image.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image, "YOU");
        box.getStyleClass().add("user-dialog");
        return box;
    }

    /**
     * Creates a left-aligned reply from Doe.
     *
     * @param text Reply text.
     * @param image Doe display image.
     * @return Doe dialog box.
     */
    public static DialogBox getDoeDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image, "୨୧  DOE  ୨୧");
        ObservableList<Node> reversed = FXCollections.observableArrayList(box.getChildren());
        Collections.reverse(reversed);
        box.getChildren().setAll(reversed);
        box.setAlignment(Pos.TOP_LEFT);
        box.dialog.getStyleClass().add("reply-label");
        box.getStyleClass().add("doe-dialog");
        return box;
    }

    /**
     * Creates a visually distinct Doe notice for a recoverable startup problem.
     *
     * @param text Warning and recovery guidance.
     * @param image Doe display image.
     * @return Doe warning dialog box.
     */
    public static DialogBox getWarningDialog(String text, Image image) {
        DialogBox box = getDoeDialog(text, image);
        box.speaker.setText("⚠  DOE NOTICE");
        box.getStyleClass().add("warning-dialog");
        return box;
    }
}
