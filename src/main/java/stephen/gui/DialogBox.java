package stephen.gui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/** Displays one wrapped chat message with speaker-specific alignment and styling. */
public class DialogBox extends HBox {
    private static final double MAX_BUBBLE_WIDTH = 430;
    private static final double BUBBLE_WIDTH_RATIO = 0.82;

    private DialogBox(String text, DialogType type) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMinHeight(Region.USE_PREF_SIZE);
        message.maxWidthProperty().bind(Bindings.min(
                MAX_BUBBLE_WIDTH, widthProperty().multiply(BUBBLE_WIDTH_RATIO)));
        message.getStyleClass().add(type.styleClass);

        setFillHeight(true);
        setAlignment(type == DialogType.USER ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        getStyleClass().add("dialog-row");
        getChildren().add(message);
        HBox.setHgrow(message, Priority.SOMETIMES);
    }

    /** Returns a right-aligned dialog for a user command. */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, DialogType.USER);
    }

    /** Returns a left-aligned dialog for Orbit's response. */
    public static DialogBox getOrbitDialog(String text) {
        return getOrbitDialog(text, false);
    }

    /** Returns a left-aligned response, using a distinct style when it reports an error. */
    public static DialogBox getOrbitDialog(String text, boolean isError) {
        return new DialogBox(text, isError ? DialogType.ERROR : DialogType.ORBIT);
    }

    /** Identifies the alignment and visual style of a message. */
    private enum DialogType {
        USER("user-bubble"),
        ORBIT("orbit-bubble"),
        ERROR("error-bubble");

        private final String styleClass;

        DialogType(String styleClass) {
            this.styleClass = styleClass;
        }
    }
}
