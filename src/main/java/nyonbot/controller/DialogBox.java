package nyonbot.controller;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaException;

/**
 * Represents one message shown in the conversation.
 */
public class DialogBox extends HBox {

    private static final AudioClip happySound = loadSound("/sounds/Kawkaw_voiceclip_happy_1.wav");
    private static final AudioClip sadSound = loadSound("/sounds/Kawkaw_voiceclip_sad_1.wav");

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.7));
        dialog.setText(text);
        displayPicture.setImage(image);
    }

    private static AudioClip loadSound(String resourcePath) {
        URL resource = DialogBox.class.getResource(resourcePath);

        if (resource == null) {
            System.err.println("Sound file missing: " + resourcePath);
            return null;
        }

        try {
            return new AudioClip(resource.toExternalForm());
        } catch (MediaException e) {
            System.err.printf("Sound disabled (%s): %s%n",
                    e.getType(), e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid sound URL: " + resourcePath);
            return null;
        }
    }

    private static void playSound(AudioClip sound) {
        if (sound != null) {
            sound.play(0.25);
        }
    }

    /**
     * Changes the message layout so that bot messages are aligned to the left.
     * The image itself is not transformed; its orientation is determined by the
     * image file.
     */
    private void flip() {
        ObservableList<Node> components = FXCollections.observableArrayList(getChildren());
        Collections.reverse(components);
        getChildren().setAll(components);
        setAlignment(Pos.BOTTOM_LEFT);
        dialog.getStyleClass().add("bot-label");
    }

    /**
     * Applies the visual treatment for an error message.
     */
    private void showError() {
        dialog.getStyleClass().add("error-label");
    }

    /**
     * Creates a right-aligned dialog for a user message.
     *
     * @param text  message entered by the user
     * @param image image displayed alongside the message
     * @return a dialog for the user message
     * @throws IOException if the dialog FXML cannot be loaded
     */
    public static DialogBox getUserDialog(String text, Image image) throws IOException {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add("user-label");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for a normal bot response.
     *
     * @param text  response produced by the bot
     * @param image image displayed alongside the response
     * @return a dialog for the bot response
     * @throws IOException if the dialog FXML cannot be loaded
     */
    public static DialogBox getBotDialog(String text, Image image) throws IOException {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        playSound(happySound);
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for a bot error response.
     *
     * @param text  error response produced by the bot
     * @param image image displayed alongside the response
     * @return a dialog for the bot error response
     * @throws IOException if the dialog FXML cannot be loaded
     */
    public static DialogBox getErrorDialog(String text, Image image) throws IOException {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.showError();
        playSound(sadSound);
        return dialogBox;
    }
}
