package larper.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Displays one chat message with an avatar and message bubble.
 * User messages appear on the right, while Larper messages appear on the left.
 */
public class DialogBox extends HBox {
    private static final int AVATAR_SIZE = 24;
    private static final int MESSAGE_WIDTH = 520;
    private static final Pattern TAG_PATTERN = Pattern.compile("\\[#([A-Za-z0-9]+)\\]");
    private static final String[] TAG_COLORS = {
        "#fbcfe8", "#bfdbfe", "#bbf7d0", "#fde68a", "#ddd6fe", "#fed7aa"
    };
    private static final String BASE_MESSAGE_STYLE = "-fx-background-radius: 16;"
            + " -fx-border-radius: 16;"
            + " -fx-border-width: 1;"
            + " -fx-padding: 11 13 11 13;"
            + " -fx-font-family: 'Avenir Next';"
            + " -fx-font-size: 13;";
    private static final String LARPER_MESSAGE_STYLE = "-fx-background-color: #fffaf3;"
            + " -fx-border-color: #e7d9c8;"
            + BASE_MESSAGE_STYLE;
    private static final String USER_MESSAGE_STYLE = "-fx-background-color: #ead4c2;"
            + " -fx-border-color: #c89a79;"
            + BASE_MESSAGE_STYLE;
    private static final String ERROR_MESSAGE_STYLE = "-fx-background-color: #fff1f1;"
            + " -fx-border-color: #e66b6b;"
            + BASE_MESSAGE_STYLE
            + " -fx-border-width: 1 1 1 4;";
    private static final String ERROR_HEADER_STYLE = "-fx-text-fill: #9b2c2c;"
            + " -fx-font-size: 11;"
            + " -fx-font-weight: 800;";
    private static final String ERROR_ICON_STYLE = "-fx-background-color: #d94a4a;"
            + " -fx-background-radius: 999;"
            + " -fx-text-fill: white;"
            + " -fx-font-size: 10;"
            + " -fx-font-weight: 800;"
            + " -fx-alignment: center;";
    private static final String MESSAGE_TEXT_FONT = "Avenir Next";
    private static final String BANNER_TEXT_FONT = "Menlo";
    private static final String TAG_CHIP_FONT = "Avenir Next";

    private final VBox text;
    private final ImageView displayPicture;

    /**
     * Creates a chat row with a message and display picture.
     *
     * @param message Text to display in the chat bubble.
     * @param image Display picture to show beside the message.
     */
    public DialogBox(String message, Image image) {
        assert message != null : "Dialog box message should not be null.";
        assert image != null : "Dialog box image should not be null.";
        text = createMessageBubble(message);
        text.setMinHeight(Region.USE_PREF_SIZE);

        displayPicture = createDisplayPicture(image);

        setSpacing(8);
        setPadding(new Insets(2, 0, 2, 0));
        setMaxWidth(Double.MAX_VALUE);
        getChildren().addAll(text, displayPicture);
    }

    /**
     * Returns a message bubble with valid tag patterns rendered as chips.
     *
     * @param message Message text to split into normal text and tag chips.
     * @return Flow pane containing the styled message content.
     */
    private VBox createMessageBubble(String message) {
        VBox messageBubble = new VBox();
        messageBubble.setMaxWidth(MESSAGE_WIDTH);
        String[] messageLines = message.split("\\R", -1);
        for (String messageLine : messageLines) {
            messageBubble.getChildren().add(createMessageLine(messageLine));
        }
        return messageBubble;
    }

    private TextFlow createMessageLine(String messageLine) {
        TextFlow messageLineFlow = new TextFlow();
        messageLineFlow.setPrefWidth(MESSAGE_WIDTH - 26);
        messageLineFlow.setMinHeight(Region.USE_PREF_SIZE);

        Matcher matcher = TAG_PATTERN.matcher(messageLine);
        int textStart = 0;
        while (matcher.find()) {
            addTextSegment(messageLineFlow, messageLine.substring(textStart, matcher.start()));
            addTagChip(messageLineFlow, matcher.group(1));
            textStart = matcher.end();
        }
        addTextSegment(messageLineFlow, messageLine.substring(textStart));
        if (isAsciiArtLine(messageLine)) {
            setLineFont(messageLineFlow, Font.font(BANNER_TEXT_FONT, 13));
        }
        return messageLineFlow;
    }

    private void addTextSegment(TextFlow messageLine, String textSegment) {
        if (textSegment.isEmpty()) {
            if (messageLine.getChildren().isEmpty()) {
                Text blankText = new Text(" ");
                blankText.setFont(Font.font(MESSAGE_TEXT_FONT, 13));
                messageLine.getChildren().add(blankText);
            }
            return;
        }
        Text text = new Text(textSegment);
        text.setFont(Font.font(MESSAGE_TEXT_FONT, 13));
        messageLine.getChildren().add(text);
    }

    private void addTagChip(TextFlow messageLine, String tag) {
        Label tagChip = new Label("#" + tag.toLowerCase());
        tagChip.setStyle(getTagChipStyle(tag));
        tagChip.setMinHeight(Region.USE_PREF_SIZE);
        messageLine.getChildren().add(tagChip);
    }

    static String getTagChipStyle(String tag) {
        int colorIndex = Math.floorMod(tag.toLowerCase().hashCode(), TAG_COLORS.length);
        return "-fx-background-color: " + TAG_COLORS[colorIndex] + ";"
                + " -fx-background-radius: 7;"
                + " -fx-padding: 2 6 2 6;"
                + " -fx-font-family: '" + TAG_CHIP_FONT + "';"
                + " -fx-font-size: 12;";
    }

    /**
     * Returns a dialog box aligned for a user message.
     *
     * @param message Text entered by the user.
     * @param image User display picture.
     * @return Dialog box for the user message.
     */
    public static DialogBox getUserDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.setAlignment(Pos.CENTER_RIGHT);
        dialogBox.text.setStyle(USER_MESSAGE_STYLE);
        dialogBox.text.setMaxWidth(330);
        dialogBox.setMessageTextFill(Color.web("#3f2b24"));
        assert dialogBox.getAlignment() == Pos.CENTER_RIGHT : "User dialog should be aligned to the right.";
        return dialogBox;
    }

    /**
     * Returns a dialog box aligned for a Larper message.
     *
     * @param message Text returned by Larper.
     * @param image Larper display picture.
     * @return Dialog box for the Larper message.
     */
    public static DialogBox getLarperDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.text.setStyle(LARPER_MESSAGE_STYLE);
        dialogBox.setMessageTextFill(Color.web("#4a3a33"));
        dialogBox.flip();
        assert dialogBox.getAlignment() == Pos.CENTER_LEFT : "Larper dialog should be aligned to the left.";
        return dialogBox;
    }

    /**
     * Returns a dialog box aligned and styled for an error message.
     *
     * @param message Error text returned by Larper.
     * @param image Larper display picture.
     * @return Dialog box for the error message.
     */
    public static DialogBox getErrorDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.text.setStyle(ERROR_MESSAGE_STYLE);
        dialogBox.setMessageTextFill(Color.web("#7a2c2c"));
        dialogBox.addErrorHeader();
        dialogBox.flip();
        assert dialogBox.getAlignment() == Pos.CENTER_LEFT : "Error dialog should be aligned to the left.";
        return dialogBox;
    }

    /**
     * Reorders the display picture and text so the avatar appears on the left.
     */
    private void flip() {
        assert getChildren().size() == 2 : "Dialog box should contain exactly a text node and an image node.";
        setAlignment(Pos.CENTER_LEFT);
        ObservableList<Node> nodes = FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(nodes);
        getChildren().setAll(nodes);
    }

    private void addErrorHeader() {
        Label icon = new Label("!");
        icon.setMinSize(18, 18);
        icon.setPrefSize(18, 18);
        icon.setStyle(ERROR_ICON_STYLE);

        Label title = new Label("Reality check");
        title.setStyle(ERROR_HEADER_STYLE);

        HBox header = new HBox(6, icon, title);
        header.setAlignment(Pos.CENTER_LEFT);
        text.getChildren().add(0, header);
    }

    private void setMessageTextFill(Color color) {
        assert color != null : "Message text color should not be null.";
        for (Node line : text.getChildren()) {
            if (line instanceof TextFlow messageLine) {
                setLineTextFill(messageLine, color);
            }
        }
    }

    private void setLineTextFill(TextFlow messageLine, Color color) {
        for (Node child : messageLine.getChildren()) {
            if (child instanceof Text textNode) {
                textNode.setFill(color);
            }
        }
    }

    private void setLineFont(TextFlow messageLine, Font font) {
        assert font != null : "Message text font should not be null.";
        for (Node child : messageLine.getChildren()) {
            if (child instanceof Text textNode) {
                textNode.setFont(font);
            }
        }
    }

    private boolean isAsciiArtLine(String messageLine) {
        return !messageLine.isBlank()
                && !messageLine.chars().anyMatch(Character::isLetterOrDigit)
                && messageLine.matches(".*[_|/\\\\].*");
    }

    private ImageView createDisplayPicture(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(AVATAR_SIZE);
        imageView.setFitHeight(AVATAR_SIZE);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
        imageView.setClip(new Circle(AVATAR_SIZE / 2.0, AVATAR_SIZE / 2.0, AVATAR_SIZE / 2.0));
        return imageView;
    }
}
