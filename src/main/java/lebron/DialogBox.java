package lebron;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents a message displayed in the chatbot GUI.
 */
public class DialogBox extends HBox {

    private static final double MESSAGE_WIDTH = 260.0;

    private static final String USER_STYLE =
            "-fx-background-color: #552583;"
                    + "-fx-text-fill: white;"
                    + "-fx-background-radius: 14;"
                    + "-fx-padding: 10;"
                    + "-fx-font-size: 13px;";

    private static final String LEBRON_STYLE =
            "-fx-background-color: #FDB927;"
                    + "-fx-text-fill: #2b2b2b;"
                    + "-fx-background-radius: 14;"
                    + "-fx-padding: 10;"
                    + "-fx-font-size: 13px;";

    private static final String AVATAR_STYLE =
            "-fx-background-color: #552583;"
                    + "-fx-background-radius: 20;"
                    + "-fx-padding: 7;"
                    + "-fx-font-size: 16px;";

    private final Label text;

    /**
     * Creates a dialog box containing the given text.
     *
     * @param text message to display
     */
    private DialogBox(String text) {
        this.text = new Label(text);

        this.text.setWrapText(true);
        this.text.setMaxWidth(MESSAGE_WIDTH);

        this.setSpacing(8);
        this.setPadding(new Insets(4, 10, 4, 10));
    }

    /**
     * Creates a dialog box for a user message.
     *
     * @param text user's message
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);

        Label avatar = new Label("YOU");
        avatar.setStyle(
                "-fx-text-fill: #552583;"
                        + "-fx-font-size: 10px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 7;"
        );

        dialogBox.text.setStyle(USER_STYLE);
        dialogBox.setAlignment(Pos.TOP_RIGHT);

        dialogBox.getChildren().addAll(
                dialogBox.text,
                avatar
        );

        return dialogBox;
    }

    /**
     * Creates a dialog box for a Lebron response.
     *
     * @param text Lebron's response
     * @return Lebron dialog box
     */
    public static DialogBox getLebronDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);

        Label avatar = new Label("🏀");
        avatar.setStyle(AVATAR_STYLE);

        dialogBox.text.setStyle(LEBRON_STYLE);
        dialogBox.setAlignment(Pos.TOP_LEFT);

        dialogBox.getChildren().addAll(
                avatar,
                dialogBox.text
        );

        return dialogBox;
    }
}

