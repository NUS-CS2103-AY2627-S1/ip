package duke;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * A custom control using HBox and FXML representing a dialogue row.
 * The left column displays the speaker's avatar and name,
 * and the right column displays the dialogue text.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;
    @FXML
    private Label speakerName;

    private DialogBox(String text, Image img, String speaker) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        speakerName.setText(speaker);
    }

    /**
     * Creates a user dialog box with User avatar and name on the left and text on the right.
     *
     * @param text The message text from the user.
     * @param img  The user display picture.
     * @return A DialogBox configured for user input.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img, "User");
    }

    /**
     * Creates a user dialog box with User avatar and name on the left and text on the right.
     *
     * @param l  The label containing user text.
     * @param iv The user display picture view.
     * @return A DialogBox configured for user input.
     */
    public static DialogBox getUserDialog(Label l, ImageView iv) {
        return getUserDialog(l.getText(), iv.getImage());
    }

    /**
     * Creates a TBC dialog box with TBC avatar and name on the left and text on the right.
     *
     * @param text The response text from TBC.
     * @param img  The TBC display picture.
     * @return A DialogBox configured for TBC response.
     */
    public static DialogBox getTbcDialog(String text, Image img) {
        return new DialogBox(text, img, "TBC");
    }

    /**
     * Creates a TBC dialog box with TBC avatar and name on the left and text on the right.
     *
     * @param l  The label containing TBC text.
     * @param iv The TBC display picture view.
     * @return A DialogBox configured for TBC response.
     */
    public static DialogBox getTbcDialog(Label l, ImageView iv) {
        return getTbcDialog(l.getText(), iv.getImage());
    }

    /**
     * Creates a Duke dialog box for backward compatibility.
     *
     * @param text The response text from Duke.
     * @param img  The Duke display picture.
     * @return A DialogBox configured for Duke response.
     */
    public static DialogBox getDukeDialog(String text, Image img) {
        return getTbcDialog(text, img);
    }

    /**
     * Creates a Duke dialog box for backward compatibility.
     *
     * @param l  The label containing Duke text.
     * @param iv The Duke display picture view.
     * @return A DialogBox configured for Duke response.
     */
    public static DialogBox getDukeDialog(Label l, ImageView iv) {
        return getTbcDialog(l, iv);
    }
}
