package kotha.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/** A speech bubble displayed in the chatbot conversation. */
public final class DialogBox extends HBox {
    private static final double LABEL_MAX_WIDTH = 320;
    private static final double ICON_SIZE = 36;
    private static final double CONTENT_SPACING = 6;

    private DialogBox(String text, boolean fromBot, Image image) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(LABEL_MAX_WIDTH);
        label.getStyleClass().add(fromBot ? "bot-bubble" : "user-bubble");
        setAlignment(fromBot ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        ImageView icon = new ImageView(image);
        icon.setFitWidth(ICON_SIZE);
        icon.setFitHeight(ICON_SIZE);
        icon.setPreserveRatio(true);
        setMaxWidth(Double.MAX_VALUE);
        setSpacing(CONTENT_SPACING);
        if (fromBot) {
            getChildren().addAll(icon, label);
        } else {
            getChildren().addAll(label, icon);
        }
    }
    /** Creates a user speech bubble with an icon. */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, false, image);
    }

    /** Creates a bot speech bubble with an icon. */
    public static DialogBox getBotDialog(String text, Image image) {
        return getBotDialog(text, image, "royal");
    }

    /** Creates a bot bubble with a personality-specific color category. */
    public static DialogBox getBotDialog(String text, Image image, String style) {
        DialogBox box = new DialogBox(text, true, image);
        Label label = (Label) box.getChildren().get(1);
        label.getStyleClass().add(style + "-bubble");
        Label personaIcon = new Label(style.equals("royal") ? "♛" : "♠");
        personaIcon.getStyleClass().addAll("persona-icon", style + "-icon");
        box.getChildren().add(0, personaIcon);
        return box;
    }
}
