package tony;

import java.util.Objects;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Displays a compact user command or a visually classified Tony response.
 */
public class DialogBox extends HBox {
    /** Diameter of each compact profile picture. */
    private static final double AVATAR_SIZE = 32;

    /** Maximum share of the conversation width used by a user command. */
    private static final double USER_MESSAGE_WIDTH_RATIO = 0.72;

    /** Maximum share of the conversation width used by an application response. */
    private static final double TONY_MESSAGE_WIDTH_RATIO = 0.84;

    /** Profile picture representing Tony. */
    private static final Image TONY_AVATAR = loadImage("/images/tony-avatar.png");

    /** Profile picture representing the user. */
    private static final Image USER_AVATAR = loadImage("/images/user-avatar.png");

    private DialogBox(String message, boolean isUser, Tony.ResponseType responseType) {
        assert message != null : "A dialog message must not be null";
        assert responseType != null : "A dialog response type must not be null";

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setMinHeight(Region.USE_PREF_SIZE);
        messageLabel.setAccessibleText((isUser ? "Your command: " : "Tony's response: ") + message);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        setSpacing(8);
        getStyleClass().add("dialog-box");
        if (isUser) {
            messageLabel.getStyleClass().add("user-message");
            messageLabel.maxWidthProperty().bind(widthProperty().multiply(USER_MESSAGE_WIDTH_RATIO));
            setAlignment(Pos.TOP_RIGHT);
            getChildren().addAll(spacer, messageLabel, createAvatar(USER_AVATAR, "Your profile picture", true));
        } else {
            VBox response = createTonyResponse(messageLabel, responseType);
            response.maxWidthProperty().bind(widthProperty().multiply(TONY_MESSAGE_WIDTH_RATIO));
            setAlignment(Pos.TOP_LEFT);
            getChildren().addAll(createAvatar(TONY_AVATAR, "Tony's profile picture", false), response, spacer);
        }
    }

    /** Loads a bundled profile picture and fails early when the application resource is missing. */
    private static Image loadImage(String resourcePath) {
        String imageUrl = Objects.requireNonNull(
                DialogBox.class.getResource(resourcePath), "Missing profile picture: " + resourcePath)
                .toExternalForm();
        return new Image(imageUrl, AVATAR_SIZE * 2, AVATAR_SIZE * 2, true, true);
    }

    /** Creates a small circular profile picture with an accessible description. */
    private static StackPane createAvatar(Image image, String accessibleText, boolean isUser) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(AVATAR_SIZE);
        imageView.setFitHeight(AVATAR_SIZE);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));

        StackPane avatar = new StackPane(imageView);
        avatar.setMinSize(AVATAR_SIZE, AVATAR_SIZE);
        avatar.setPrefSize(AVATAR_SIZE, AVATAR_SIZE);
        avatar.setMaxSize(AVATAR_SIZE, AVATAR_SIZE);
        avatar.setAccessibleText(accessibleText);
        avatar.getStyleClass().addAll("avatar", isUser ? "user-avatar" : "tony-avatar");
        return avatar;
    }

    /** Creates a visually classified Tony response without imitating the user's command bubble. */
    private static VBox createTonyResponse(Label messageLabel, Tony.ResponseType responseType) {
        String headingText;
        String styleClass;
        switch (responseType) {
            case ERROR:
                headingText = "Couldn't run that command";
                styleClass = "error-response";
                break;
            case WARNING:
                headingText = "Needs attention";
                styleClass = "warning-response";
                break;
            case NORMAL:
                headingText = "Tony";
                styleClass = "normal-response";
                break;
            default:
                throw new AssertionError("Every response type must have a dialog style");
        }

        Label heading = new Label(headingText);
        heading.getStyleClass().add("response-heading");
        messageLabel.getStyleClass().add("response-message");

        VBox response = new VBox(5, heading, messageLabel);
        response.getStyleClass().addAll("tony-response", styleClass);
        return response;
    }

    /**
     * Creates a message displayed on the user's side of the conversation.
     *
     * @param message message entered by the user.
     * @return the user dialog box.
     */
    public static DialogBox getUserDialog(String message) {
        return new DialogBox(message, true, Tony.ResponseType.NORMAL);
    }

    /**
     * Creates a message displayed on Tony's side of the conversation.
     *
     * @param message response generated by Tony.
     * @param responseType visual meaning of the response.
     * @return Tony's dialog box.
     */
    public static DialogBox getTonyDialog(String message, Tony.ResponseType responseType) {
        return new DialogBox(message, false, responseType);
    }
}
