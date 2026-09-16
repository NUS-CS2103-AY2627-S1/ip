package kia.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * A chat message displaying the speaker and message text.
 */
public class DialogBox extends HBox {
    @FXML
    private Label speaker;

    @FXML
    private Label message;

    @FXML
    private ImageView avatar;



    /**
     * Creates a styled chat message.
     *
     * @param speakerName label identifying the speaker
     * @param text message to display
     */
    private DialogBox(String speakerName, String text) {
        FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog layout.", e);
        }
        speaker.setText(speakerName);
        message.setText("Kia".equals(speakerName) && !text.endsWith("Arf!")
                ? text + " Arf!" : text);
        if ("Kia".equals(speakerName)) {
            setStyle("-fx-padding: 4 8 4 4;");
            avatar.setImage(new Image(DialogBox.class.getResourceAsStream("/images/kia-profile.jpg")));
            message.setStyle("-fx-font-size: 13px; -fx-text-fill: #25324a;"
                    + " -fx-background-color: rgba(255,255,255,0.92); -fx-padding: 8 10;"
                    + " -fx-background-radius: 4 12 12 12;");
        } else {
            setStyle("-fx-padding: 4 4 4 48;");
            avatar.setImage(new Image(DialogBox.class.getResourceAsStream("/images/user-profile.jpg")));
            avatar.setManaged(true);
            avatar.setVisible(true);
            message.setStyle("-fx-font-size: 13px; -fx-text-fill: white;"
                    + " -fx-background-color: #3978b8; -fx-padding: 8 10;"
                    + " -fx-background-radius: 12 4 12 12;");
        }
        if (text.startsWith("OOPS!!!")) {
            message.setStyle("-fx-font-size: 13px; -fx-text-fill: #a61b29; -fx-font-weight: bold;"
                    + " -fx-background-color: #ffe8eb; -fx-padding: 8; -fx-background-radius: 8;");
            speaker.setStyle("-fx-font-weight: bold; -fx-text-fill: #a61b29;");
        }
    }

    /**
     * Creates a right-aligned message from the user.
     *
     * @param text user-entered text
     * @return a user dialog box
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox("You", text);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        return dialogBox;
    }

    /**
     * Creates a left-aligned message from Kia.
     *
     * @param text Kia's response
     * @return a Kia dialog box
     */
    public static DialogBox getKiaDialog(String text) {
        return new DialogBox("Kia", text);
    }
}
