package miku.ui;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import miku.parser.Parser;

/** Displays a compact command row or a response from Miku. */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 48;
    private static final double AVATAR_CORNER_SIZE = 12;
    private static final double MESSAGE_GAP = 8;

    /** Creates an empty row that a factory method fills with message content. */
    private DialogBox() {
        setSpacing(MESSAGE_GAP);
    }

    /** Returns a right-aligned row that emphasizes recognized command words and their markers. */
    public static DialogBox getUserDialog(String input, Image avatarImage) {
        DialogBox dialogBox = new DialogBox();
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.getStyleClass().add("command-row");

        DialogTextSplitter.CommandParts parts = DialogTextSplitter.splitCommand(input);
        boolean shouldHighlightSyntax = Parser.isRecognizedCommandWord(parts.command());
        TextFlow commandText = new TextFlow(new Text(parts.leadingWhitespace()));
        Text command = new Text(parts.command());
        if (shouldHighlightSyntax) {
            command.getStyleClass().add("command-verb");
        }
        commandText.getChildren().add(command);
        for (DialogTextSplitter.ArgumentPart part : DialogTextSplitter.splitArguments(
                parts.command(), parts.arguments(), shouldHighlightSyntax)) {
            Text argument = new Text(part.text());
            if (part.isMarker()) {
                argument.getStyleClass().add("command-marker");
            }
            commandText.getChildren().add(argument);
        }
        commandText.setTextAlignment(TextAlignment.RIGHT);
        commandText.setMinWidth(0);
        commandText.setMaxWidth(Double.MAX_VALUE);
        commandText.getStyleClass().add("command-content");
        dialogBox.getChildren().addAll(commandText, createAvatar(avatarImage));
        return dialogBox;
    }

    /** Returns a left-aligned reply that emphasizes only an error's opening word. */
    public static DialogBox getMikuDialog(String text, boolean isError, Image avatarImage) {
        DialogBox dialogBox = new DialogBox();
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.getStyleClass().add("reply-row");

        DialogTextSplitter.ReplyParts parts = DialogTextSplitter.splitReply(text, isError);
        TextFlow message = new TextFlow();
        if (!parts.highlightedPrefix().isEmpty()) {
            message.getChildren().add(styledText(parts.highlightedPrefix(), "oops-prefix"));
        }
        message.getChildren().add(new Text(parts.remainingText()));
        message.setMinWidth(0);
        message.setMaxWidth(Double.MAX_VALUE);
        message.getStyleClass().add("reply-content");
        dialogBox.getChildren().addAll(createAvatar(avatarImage), message);
        return dialogBox;
    }

    /** Creates a small rounded avatar, omitting its layout space when no image is available. */
    private static ImageView createAvatar(Image image) {
        ImageView avatar = new ImageView(image);
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setPreserveRatio(true);
        Rectangle clip = new Rectangle(AVATAR_SIZE, AVATAR_SIZE);
        clip.setArcWidth(AVATAR_CORNER_SIZE);
        clip.setArcHeight(AVATAR_CORNER_SIZE);
        avatar.setClip(clip);
        if (image == null) {
            avatar.setManaged(false);
            avatar.setVisible(false);
        }
        return avatar;
    }

    /** Creates text with one CSS role in the command row. */
    private static Text styledText(String content, String styleClass) {
        Text text = new Text(content);
        text.getStyleClass().add(styleClass);
        return text;
    }

}
