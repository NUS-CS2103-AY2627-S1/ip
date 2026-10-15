package dude.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;

/**
 * Displays one message in the GUI conversation.
 */
public class DialogBox extends HBox {
    /**
     * Visual categories used to distinguish conversation participants.
     */
    public enum Kind {
        /** Command typed by the user. */
        USER,
        /** Normal response from DUDE. */
        APP,
        /** Response describing invalid input. */
        ERROR
    }

    /**
     * Creates a styled conversation message.
     *
     * @param text Message to display.
     * @param kind Message category.
     */
    public DialogBox(String text, Kind kind) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(kind == Kind.USER ? 600 : Double.MAX_VALUE);
        StackPane bubble = new StackPane(message);
        bubble.getStyleClass().addAll("message-bubble", kind.name().toLowerCase());
        bubble.setMaxWidth(kind == Kind.USER ? 600 : 700);

        Polygon pointer = new Polygon(kind == Kind.USER
                ? new double[] {0, 0, 8, 6, 0, 12}
                : new double[] {8, 0, 0, 6, 8, 12});
        pointer.getStyleClass().addAll("chat-pointer", kind.name().toLowerCase());

        setAlignment(kind == Kind.USER ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        setMaxWidth(Double.MAX_VALUE);
        getStyleClass().add("dialog-box");
        if (kind == Kind.USER) {
            getChildren().addAll(bubble, pointer);
        } else {
            Label profile = new Label("😎");
            profile.getStyleClass().add("profile-icon");
            getChildren().addAll(profile, pointer, bubble);
        }
    }
}
