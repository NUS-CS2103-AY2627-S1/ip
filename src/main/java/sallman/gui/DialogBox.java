package sallman.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
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
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * One message in the conversation.
 * <p>
 * The two sides are drawn differently on purpose: the conversation is between a
 * person and an app, not between two people, so the user's messages are
 * compact bubbles with no picture, while the chatbot's replies carry a small
 * avatar and a style that says what kind of reply they are.
 * <p>
 * The idea of a dialog box loaded from FXML, and flipped for the chatbot's side,
 * comes from the SE-EDU JavaFX tutorial (https://se-education.org/guides/tutorials/javaFx.html).
 */
public class DialogBox extends HBox {

    /**
     * How much of the window's width a bubble may take. A long reply stretched
     * across a wide window is hard to read, while a fixed width in pixels would
     * waste most of a narrow one.
     */
    private static final double MAX_BUBBLE_SHARE = 0.8;

    /**
     * How long a new message takes to fade and slide into place. Long enough to
     * follow the eye to the newest message, short enough not to delay reading it.
     */
    private static final Duration ENTRANCE = Duration.millis(180);

    /** How far below its place a new message starts, in pixels. */
    private static final double ENTRANCE_OFFSET = 12;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            // This box is both the root of the loaded layout and its controller,
            // which is what lets the same layout be built once per message.
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().multiply(MAX_BUBBLE_SHARE));
        displayPicture.setImage(img);
        // The avatar is shown as a circle, so its square corners do not take up
        // room or draw the eye away from the text beside it.
        double radius = displayPicture.getFitWidth() / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Fades and slides this message into place, so a new one is easy to follow
     * in a conversation that is already several messages long.
     */
    private void playEntrance() {
        FadeTransition fade = new FadeTransition(ENTRANCE, this);
        fade.setFromValue(0);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(ENTRANCE, this);
        slide.setFromY(ENTRANCE_OFFSET);
        slide.setToY(0);
        new ParallelTransition(fade, slide).play();
    }

    /** Puts the avatar on the left and the text on the right. */
    private void flip() {
        ObservableList<Node> nodes = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(nodes);
        getChildren().setAll(nodes);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Gives the bubble an edge colored by the kind of command being answered,
     * so the effect of a command can be seen before the reply is read.
     *
     * @param commandType simple class name of the command, or an empty string
     */
    private void colorByCommand(String commandType) {
        switch (commandType) {
            case "AddCommand":
                dialog.getStyleClass().add("add-label");
                break;
            case "MarkCommand":
                dialog.getStyleClass().add("marked-label");
                break;
            case "DeleteCommand":
                dialog.getStyleClass().add("delete-label");
                break;
            default:
                // Anything else keeps the plain bubble.
        }
    }

    /**
     * Returns a box showing something the user typed.
     * <p>
     * No picture is shown: the user knows which messages are their own, and the
     * space is better spent on the conversation.
     *
     * @param text what the user said
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox box = new DialogBox(text, null);
        box.getChildren().remove(box.displayPicture);
        box.dialog.getStyleClass().add("user-label");
        box.playEntrance();
        return box;
    }

    /**
     * Returns a box showing one of the chatbot's replies, on the opposite side
     * from the user's messages.
     *
     * @param text        what the chatbot said
     * @param img         the chatbot's avatar
     * @param commandType simple class name of the command that produced the
     *                    reply, used to color the bubble
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getSallmanDialog(String text, Image img, String commandType) {
        DialogBox box = new DialogBox(text, img);
        box.flip();
        box.colorByCommand(commandType);
        box.playEntrance();
        return box;
    }

    /**
     * Returns a box showing a reply that reports a problem, styled to stand out
     * from ordinary replies so a mistyped command is noticed straight away.
     *
     * @param text what the chatbot said about the problem
     * @param img  the chatbot's avatar
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox box = new DialogBox(text, img);
        box.flip();
        box.dialog.getStyleClass().add("error-label");
        box.playEntrance();
        return box;
    }
}
