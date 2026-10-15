package james.gui;

import java.io.IOException;

import james.command.CommandResponse;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


/**
 * Displays one message in the conversation.
 */
public class DialogBox extends HBox {
    private static final String DIALOG_BOX_RESOURCE = "/view/DialogBox.fxml";
    private static final String DIALOG_LOAD_ERROR_MESSAGE = "Unable to load dialog box";
    private static final double RANDOM_STICKER_SIZE = 160;
    private static final double RESPONSE_STICKER_SIZE = 120;
    private static final double GROUPED_MESSAGE_GAP = 4;
    private static final double USER_WIDTH_FRACTION = 0.82;
    private static final double DIALOG_HORIZONTAL_MARGIN = 24;
    private static final String REPLY_STYLE_CLASS = "reply-label";

    @FXML
    private TextFlow dialog;

    /**
     * Loads the message layout and configures its text and response styling.
     *
     * @param text Message text.
     * @param isUser Whether this message belongs to the user.
     * @param type Response category used for styling James's replies.
     */
    private DialogBox(String text, boolean isUser, CommandResponse.Type type) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource(DIALOG_BOX_RESOURCE));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException(DIALOG_LOAD_ERROR_MESSAGE, e);
        }
        dialog.getChildren().setAll(new Text(text));
        setFillHeight(false);
        setMinWidth(0);
        setPrefWidth(0);
        setMaxWidth(Double.MAX_VALUE);
        dialog.setMinWidth(0);
        dialog.setMaxWidth(Double.MAX_VALUE);
        dialog.setMinHeight(0);

        HBox.setHgrow(dialog, Priority.ALWAYS);
        if (isUser) {
            HBox.setHgrow(dialog, Priority.NEVER);
            dialog.maxWidthProperty().bind(Bindings.max(0, widthProperty().subtract(DIALOG_HORIZONTAL_MARGIN)
                    .multiply(USER_WIDTH_FRACTION)));
            dialog.getStyleClass().add("user-command");
        } else {
            setAlignment(Pos.TOP_LEFT);
            dialog.getStyleClass().add(REPLY_STYLE_CLASS);
            dialog.getStyleClass().add(type.name().toLowerCase());
            if (type == CommandResponse.Type.ERROR) {
                Text heading = new Text("Error\n");
                heading.getStyleClass().add("error-heading");
                dialog.getChildren().addFirst(heading);
            }
        }
    }

    /**
     * Displays a compact sticker bubble, grouped below any reply text.
     */
    private void showSticker(CommandResponse response) {
        boolean isStickerOnly = response.getDisplayMode() == CommandResponse.DisplayMode.STICKER_ONLY;
        double size = isStickerOnly ? RANDOM_STICKER_SIZE : RESPONSE_STICKER_SIZE;
        ImageView sticker = new ImageView(new Image(DialogBox.class.getResourceAsStream(response.getStickerPath())));
        sticker.setFitWidth(size);
        sticker.setFitHeight(size);
        sticker.setPreserveRatio(true);
        sticker.setAccessibleText(response.getSticker().name() + " sticker");

        // Cap the bubble at its preferred size so it hugs the artwork instead of filling the row.
        VBox stickerBubble = new VBox(sticker);
        stickerBubble.getStyleClass().addAll("sticker-bubble", REPLY_STYLE_CLASS);
        stickerBubble.setMaxWidth(Region.USE_PREF_SIZE);
        stickerBubble.setMinHeight(Region.USE_PREF_SIZE);

        // Keep text flexible while the two bubbles form one closely spaced reply group.
        int contentIndex = getChildren().indexOf(dialog);
        getChildren().remove(dialog);
        VBox content = new VBox(GROUPED_MESSAGE_GAP);
        content.setAlignment(Pos.TOP_LEFT);
        content.setMinWidth(0);
        content.setMaxWidth(Double.MAX_VALUE);
        if (!isStickerOnly) {
            content.getChildren().add(dialog);
        }
        content.getChildren().add(stickerBubble);
        HBox.setMargin(content, new Insets(0, 7, 0, 7));
        HBox.setHgrow(content, Priority.ALWAYS);
        getChildren().add(contentIndex, content);
    }

    /**
     * Creates a user message box.
     *
     * @param text Message entered by the user.
     * @return Dialog displaying the user's message.
     */
    public static DialogBox createUser(String text) {
        return new DialogBox(text, true, CommandResponse.Type.NORMAL);
    }

    /**
     * Creates a James response box.
     *
     * @param response Command result containing reply text and optional sticker information.
     * @return Dialog displaying the response in its requested display mode.
     */
    public static DialogBox createJames(CommandResponse response) {
        DialogBox db = new DialogBox(response.getMessage(), false, response.getType());
        if (response.getStickerPath() != null) {
            db.showSticker(response);
        }
        return db;
    }
}
