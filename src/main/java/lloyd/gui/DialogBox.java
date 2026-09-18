package lloyd.gui;

import java.net.URL;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import lloyd.LloydMood;

/** Displays one user or Lloyd message in the conversation. */
public class DialogBox extends HBox {
    private static final double MESSAGE_WIDTH_RATIO = 0.75;
    private static final double PORTRAIT_SIZE = 52.0;
    private static final String IMAGE_DIRECTORY = "/images/";

    private static final String USER_MESSAGE_STYLE =
            "-fx-background-color: #d8ecff; -fx-background-radius: 8;";
    private static final String LLOYD_MESSAGE_STYLE =
            "-fx-background-color: #eeeeee; -fx-background-radius: 8;";
    private static final String SPEAKER_STYLE =
            "-fx-background-color: #303846; -fx-background-radius: 20;"
                    + " -fx-text-fill: white; -fx-font-weight: bold;";
    private static final Map<LloydMood, Image> LLOYD_PORTRAITS = Map.of(
            LloydMood.CONFIDENT, loadPortraitImage("lloyd-confident.png"),
            LloydMood.DELIGHTED, loadPortraitImage("lloyd-delighted.png"),
            LloydMood.ANNOYED, loadPortraitImage("lloyd-annoyed.png"),
            LloydMood.ALARMED, loadPortraitImage("lloyd-alarmed.png"));

    /**
     * Creates a dialog box for a message and its speaker.
     *
     * @param text Message to display.
     * @param speaker Visual shown beside the message.
     * @param isUserMessage Whether the message belongs to the user.
     */
    private DialogBox(String text, Node speaker, boolean isUserMessage) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMinWidth(0.0);
        message.maxWidthProperty().bind(
                widthProperty().multiply(MESSAGE_WIDTH_RATIO));
        message.setPadding(new Insets(10));
        message.setStyle(isUserMessage ? USER_MESSAGE_STYLE : LLOYD_MESSAGE_STYLE);

        setAlignment(isUserMessage ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        setSpacing(8.0);
        setPadding(new Insets(4.0));

        if (isUserMessage) {
            getChildren().addAll(message, speaker);
        } else {
            getChildren().addAll(speaker, message);
        }
    }

    /** Returns the portrait image stored at the supplied resource path. */
    private static Image loadPortraitImage(String fileName) {
        URL resource = DialogBox.class.getResource(IMAGE_DIRECTORY + fileName);
        if (resource == null) {
            throw new IllegalStateException("Missing Lloyd portrait: " + fileName);
        }
        return new Image(resource.toExternalForm());
    }

    /** Creates the badge displayed beside a user message. */
    private static Label createUserBadge() {
        Label badge = new Label("YOU");
        badge.setAlignment(Pos.CENTER);
        badge.setMinSize(PORTRAIT_SIZE, 40.0);
        badge.setStyle(SPEAKER_STYLE);
        return badge;
    }

    /** Creates Lloyd's portrait for the supplied mood. */
    private static ImageView createLloydPortrait(LloydMood mood) {
        ImageView portrait = new ImageView(LLOYD_PORTRAITS.get(mood));
        portrait.setFitWidth(PORTRAIT_SIZE);
        portrait.setFitHeight(PORTRAIT_SIZE);
        portrait.setPreserveRatio(true);
        portrait.setSmooth(true);
        portrait.setClip(new Circle(
                PORTRAIT_SIZE / 2,
                PORTRAIT_SIZE / 2,
                PORTRAIT_SIZE / 2));
        portrait.setAccessibleText(switch (mood) {
            case CONFIDENT -> "Lloyd looks confident";
            case DELIGHTED -> "Lloyd looks delighted";
            case ANNOYED -> "Lloyd looks annoyed";
            case ALARMED -> "Lloyd looks alarmed";
        });
        return portrait;
    }

    /**
     * Creates a dialog box for text entered by the user.
     *
     * @param text User's message.
     * @return Dialog box aligned to the right.
     */
    public static DialogBox createUserDialog(String text) {
        return new DialogBox(text, createUserBadge(), true);
    }

    /**
     * Creates a dialog box for a response from Lloyd.
     *
     * @param text Lloyd's response.
     * @param mood Lloyd's mood for the response.
     * @return Dialog box aligned to the left.
     */
    public static DialogBox createLloydDialog(String text, LloydMood mood) {
        return new DialogBox(text, createLloydPortrait(mood), false);
    }
}
