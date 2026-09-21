package peter.gui;

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

/**
 * One entry in the transcript: a speaker's picture beside what they said.
 *
 * <p>Unlike the main window, this component is created many times while the
 * application runs, so it uses the {@code fx:root} pattern: the class is both
 * the root of its own FXML and its own controller. That is why the loader is
 * given {@code this} as root and controller before {@code load} is called.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Builds a dialog box showing the given text next to the given picture.
     *
     * <p>Private because the two arrangements are reached through the named
     * factory methods below, which read more clearly at the call site than a
     * constructor plus a boolean would.
     *
     * @param text words to display.
     * @param image speaker's picture.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            // Both calls must come before load(), or the loader builds a
            // separate node tree and this instance stays empty.
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        // Keeping messages narrower than the transcript leaves a clear visual
        // margin between speakers and makes long replies easier to scan.
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.75));
        displayPicture.setImage(image);
    }

    /**
     * Returns a dialog box for something the user said, with the picture on
     * the right.
     *
     * @param text words the user typed.
     * @param image user's picture.
     * @return dialog box ready to add to the transcript.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().addAll("dialog-box", "user-dialog");
        return dialogBox;
    }

    /**
     * Returns a dialog box for something the chatbot said, mirrored so that
     * the two speakers are told apart at a glance.
     *
     * @param text words the chatbot replied with.
     * @param image chatbot's picture.
     * @return dialog box ready to add to the transcript.
     */
    public static DialogBox getPeterDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().addAll("dialog-box", "peter-dialog");
        dialogBox.flip();
        return dialogBox;
    }

    /** Moves the picture to the left of the text and aligns the box left. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
