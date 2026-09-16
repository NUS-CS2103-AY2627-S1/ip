package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents one message in the chat between the user and NOVA.
 */
public class DialogBox extends HBox {

    /**
     * Types of dialog messages.
     */
    public enum DialogType {
        USER,
        NOVA,
        ERROR,
        WARNING
    }

    private final Label message;

    /**
     * Creates a dialog box.
     *
     * @param text message to display
     * @param type type of dialog message
     */
    public DialogBox(String text, DialogType type) {
        message = new Label(text);

        message.setWrapText(true);
        message.setMaxWidth(420);
        message.setPadding(new Insets(10, 14, 10, 14));

        switch (type) {
            case USER:
                setAlignment(Pos.CENTER_RIGHT);
                message.setMaxWidth(300);
                message.setStyle(
                        "-fx-background-color: #dbeafe;"
                                + "-fx-background-radius: 12;"
                );
                break;

            case ERROR:
                setAlignment(Pos.CENTER_LEFT);
                message.setMaxWidth(480);
                message.setStyle(
                        "-fx-background-color: #fee2e2;"
                                + "-fx-text-fill: #991b1b;"
                                + "-fx-background-radius: 12;"
                                + "-fx-font-weight: bold;"
                );
                break;

            case WARNING:
                setAlignment(Pos.CENTER_LEFT);
                message.setMaxWidth(480);
                message.setStyle(
                        "-fx-background-color: #fef3c7;"
                                + "-fx-text-fill: #92400e;"
                                + "-fx-background-radius: 12;"
                );
                break;

            case NOVA:
            default:
                setAlignment(Pos.CENTER_LEFT);
                message.setMaxWidth(480);
                message.setStyle(
                        "-fx-background-color: #eeeeee;"
                                + "-fx-background-radius: 12;"
                );
                break;

        }

        getChildren().add(message);
    }
}
