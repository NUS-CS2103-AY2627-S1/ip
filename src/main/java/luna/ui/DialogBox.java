package luna.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Represents one message in the JavaFX conversation view.
 *
 * <p>User commands appear as compact chat bubbles, while Luna responses use branded cards.</p>
 */
public class DialogBox extends HBox {
    private static final double USER_MESSAGE_MAX_WIDTH = 340.0;
    private static final double ICON_SIZE = 28.0;
    private static final String MOON_ICON = "\u263E";
    private static final String USER_AVATAR_PATH = "/images/user-avatar.png";
    private static final Image USER_AVATAR = new Image(
            DialogBox.class.getResourceAsStream(USER_AVATAR_PATH));

    private DialogBox() {
        setMaxWidth(Double.MAX_VALUE);
        setSnapToPixel(true);
        getStyleClass().add("dialog-row");
    }

    /**
     * Returns a rounded, right-aligned bubble representing user input.
     *
     * @param message User message text.
     * @return Dialog box aligned for the user.
     */
    public static DialogBox getUserDialog(String message) {
        DialogBox dialogBox = new DialogBox();
        Label messageLabel = createWrappingLabel(message, "user-message");
        StackPane avatar = createUserAvatar();

        messageLabel.setId("user-message");
        messageLabel.setMaxWidth(USER_MESSAGE_MAX_WIDTH);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.setSpacing(8);
        dialogBox.getChildren().addAll(messageLabel, avatar);
        return dialogBox;
    }

    /**
     * Creates the small circular profile picture shown beside user input.
     *
     * @return Circular avatar containing the bundled user image.
     */
    private static StackPane createUserAvatar() {
        ImageView avatarImage = new ImageView(USER_AVATAR);
        avatarImage.setFitWidth(ICON_SIZE);
        avatarImage.setFitHeight(ICON_SIZE);
        avatarImage.setPreserveRatio(true);
        avatarImage.setSmooth(true);
        avatarImage.setClip(new Circle(ICON_SIZE / 2, ICON_SIZE / 2, ICON_SIZE / 2));

        StackPane avatarFrame = new StackPane(avatarImage);
        avatarFrame.setMinSize(ICON_SIZE, ICON_SIZE);
        avatarFrame.setPrefSize(ICON_SIZE, ICON_SIZE);
        avatarFrame.setMaxSize(ICON_SIZE, ICON_SIZE);
        avatarFrame.getStyleClass().add("user-avatar-frame");
        return avatarFrame;
    }

    /**
     * Returns a blue-accented card representing Luna's output.
     *
     * @param message Luna response text.
     * @return Dialog box aligned for Luna.
     */
    public static DialogBox getLunaDialog(String message) {
        return createLunaDialog(message, "Luna", "normal-card", "moon-icon", MOON_ICON, false);
    }

    /**
     * Returns a softly highlighted card representing an error from Luna.
     *
     * @param message Error message text.
     * @return Dialog box styled to draw attention to the error.
     */
    public static DialogBox getErrorDialog(String message) {
        return createLunaDialog(message, "Check your command", "error-card", "error-icon", "!", false);
    }

    /**
     * Returns a compact card for Luna's welcome message.
     *
     * @param message Welcome text and command descriptions.
     * @return Formatted help card aligned for Luna.
     */
    public static DialogBox getWelcomeDialog(String message) {
        return createLunaDialog(message, "Quick start", "welcome-card", "moon-icon", MOON_ICON, false);
    }

    /**
     * Returns a structured card containing Luna's command reference.
     *
     * @param message Command reference text.
     * @return Formatted help card aligned for Luna.
     */
    public static DialogBox getHelpDialog(String message) {
        return createLunaDialog(message, "Command guide", "welcome-card", "moon-icon", MOON_ICON, true);
    }

    /**
     * Builds a Luna response with its icon, header, card, and formatted content.
     *
     * @param message Response text.
     * @param header Card header.
     * @param cardStyleClass Specialized card style class.
     * @param iconStyleClass Specialized icon style class.
     * @param iconText Character displayed in the icon.
     * @param isHelpPanel Whether command descriptions should use the help layout.
     * @return Dialog box containing the response card.
     */
    private static DialogBox createLunaDialog(String message, String header, String cardStyleClass,
            String iconStyleClass, String iconText, boolean isHelpPanel) {
        DialogBox dialogBox = new DialogBox();
        Label iconLabel = new Label(iconText);
        VBox responseCard = new VBox(6);
        Label headerLabel = new Label(header);
        Node content = isHelpPanel ? createHelpContent(message) : createMessageContent(message);

        iconLabel.setMinSize(ICON_SIZE, ICON_SIZE);
        iconLabel.setPrefSize(ICON_SIZE, ICON_SIZE);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.getStyleClass().addAll("response-icon", iconStyleClass);

        responseCard.setId("luna-response");
        responseCard.setMaxWidth(Double.MAX_VALUE);
        responseCard.setSnapToPixel(true);
        responseCard.getStyleClass().addAll("luna-card", cardStyleClass);
        HBox.setHgrow(responseCard, Priority.ALWAYS);

        headerLabel.setId("luna-header");
        headerLabel.getStyleClass().add("luna-card-header");
        if (cardStyleClass.equals("error-card")) {
            headerLabel.getStyleClass().add("error-card-header");
        }

        responseCard.getChildren().addAll(headerLabel, content);
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.setSpacing(8);
        dialogBox.getChildren().addAll(iconLabel, responseCard);
        return dialogBox;
    }

    /**
     * Formats an ordinary response without changing Luna's task notation.
     *
     * @param message Response text.
     * @return Wrapping label containing the original response.
     */
    private static Node createMessageContent(String message) {
        Label messageLabel = createWrappingLabel(message, "luna-message");
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        return messageLabel;
    }

    /**
     * Formats the command reference as a heading and individual command rows.
     *
     * @param message Help text containing command descriptions.
     * @return Structured help content.
     */
    private static Node createHelpContent(String message) {
        VBox helpContent = new VBox(7);
        String[] lines = message.split("\\R");

        for (String line : lines) {
            if (line.equals("Available commands:")) {
                helpContent.getChildren().add(createWrappingLabel(line, "help-title"));
            } else if (line.startsWith("> ")) {
                helpContent.getChildren().add(createCommandRow(line));
            }
        }
        return helpContent;
    }

    /**
     * Creates a wrapping help row with distinct command and description text.
     *
     * @param commandLine Command line in {@code > command: description} form.
     * @return Formatted command row.
     */
    private static Node createCommandRow(String commandLine) {
        int separatorIndex = commandLine.indexOf(':');
        String command = separatorIndex < 0 ? commandLine.substring(2) : commandLine.substring(2, separatorIndex);
        String description = separatorIndex < 0 ? "" : commandLine.substring(separatorIndex + 1).trim();

        Text commandText = new Text(command + "  ");
        commandText.getStyleClass().add("command-name");
        Text descriptionText = new Text(description);
        descriptionText.getStyleClass().add("command-description");

        TextFlow commandRow = new TextFlow(commandText, descriptionText);
        commandRow.setLineSpacing(2);
        commandRow.getStyleClass().add("command-row");
        return commandRow;
    }

    /**
     * Creates a wrapping label with the requested style class.
     *
     * @param message Text to display.
     * @param styleClass CSS style class applied to the label.
     * @return Configured label.
     */
    private static Label createWrappingLabel(String message, String styleClass) {
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add(styleClass);
        return messageLabel;
    }
}
