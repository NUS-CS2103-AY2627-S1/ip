package nova;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import javafx.beans.property.ReadOnlyDoubleProperty;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Represents one turn of the conversation: the text in a bubble with a small
 * tail pointing at its speaker, the time it was said, and on Nova's side a round
 * avatar. Errors carry a marker and their own style, confirmations a tick, so a
 * glance down the chat shows what happened.
 */
public class DialogBox extends VBox {
    private static final double AVATAR_SIZE = 34;
    private static final double BUBBLE_WIDTH_RATIO = 0.74;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private static final String STYLE_DIALOG_BOX = "dialog-box";
    private static final String STYLE_BUBBLE = "bubble";
    private static final String STYLE_NOVA = "nova";
    private static final String STYLE_USER = "user";
    private static final String STYLE_ERROR = "error";
    private static final String STYLE_SUCCESS = "success";
    private static final String STYLE_BADGE = "badge";
    private static final String BADGE_ERROR = "!";
    private static final String BADGE_SUCCESS = "\u2713";

    @FXML
    private HBox row;
    @FXML
    private Region tail;
    @FXML
    private Label dialog;
    @FXML
    private Label timestamp;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getStyleClass().add(STYLE_DIALOG_BOX);
        dialog.getStyleClass().add(STYLE_BUBBLE);
        dialog.setText(text);
        timestamp.setText(LocalTime.now().format(TIME_FORMAT));
        displayPicture.setImage(img);
        displayPicture.setFitWidth(AVATAR_SIZE);
        displayPicture.setFitHeight(AVATAR_SIZE);
        displayPicture.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
    }

    /**
     * Creates a dialog box for the user's message: the text sits in a bubble on
     * the right, with the user's avatar beside it, mirroring Nova's turns.
     *
     * @param text the message text
     * @param img the user's avatar
     * @return the dialog box
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.getStyleClass().add(STYLE_USER);
        return dialogBox;
    }

    /**
     * Creates a dialog box for the chatbot's reply, styled by the kind of reply
     * it is.
     *
     * @param result the outcome of the user's command
     * @param img the chatbot's avatar
     * @return the dialog box
     */
    public static DialogBox getNovaDialog(CommandResult result, Image img) {
        DialogBox dialogBox = new DialogBox(result.text(), img);
        dialogBox.getStyleClass().add(STYLE_NOVA);
        dialogBox.flip();
        switch (result.kind()) {
            case ERROR:
                dialogBox.getStyleClass().add(STYLE_ERROR);
                dialogBox.dialog.setGraphic(createBadge(BADGE_ERROR));
                break;
            case SUCCESS:
                dialogBox.getStyleClass().add(STYLE_SUCCESS);
                dialogBox.dialog.setGraphic(createBadge(BADGE_SUCCESS));
                break;
            default:
                break;
        }
        return dialogBox;
    }

    /**
     * Keeps the text bubble to a readable share of the chat width by tracking
     * the width of its container.
     *
     * @param containerWidth the width of the container holding the dialog boxes
     */
    public void limitBubbleWidth(ReadOnlyDoubleProperty containerWidth) {
        dialog.maxWidthProperty().bind(containerWidth.multiply(BUBBLE_WIDTH_RATIO));
    }

    /** Creates the small round marker that opens a reply. */
    private static Label createBadge(String symbol) {
        Label badge = new Label(symbol);
        badge.getStyleClass().add(STYLE_BADGE);
        return badge;
    }

    /**
     * Flips the dialog box so that the avatar sits on the left, the bubble next
     * to it and the tail points from the bubble towards the avatar.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(row.getChildren());
        Collections.reverse(tmp);
        row.getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        row.setAlignment(Pos.BOTTOM_LEFT);
        tail.setScaleX(1);
    }
}
