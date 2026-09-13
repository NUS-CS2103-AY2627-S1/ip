package xuan.ui;

import java.io.IOException;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

/**
 * Represents a dialog box containing a message and an image.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box with the specified text and image.
     *
     * @param text the message to display
     * @param image the image to display
     */
    private DialogBox(String text, ImageView image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(image.getImage());

        setOnMouseEntered(event -> moveDialogBox(-3));
        setOnMouseExited(event -> moveDialogBox(0));
    }

    /**
     * Creates a dialog box for a user message.
     *
     * @param text the user's message
     * @param image the user's image
     * @return the user dialog box
     */
    public static DialogBox getUserDialog(String text, ImageView image) {
        DialogBox dialogBox = new DialogBox(text, image);

        dialogBox.dialog.setStyle(
                "-fx-background-color: #4A90E2;"
                        + "-fx-text-fill: white;"
                        + "-fx-background-radius: 10;"
                        + "-fx-padding: 10;"
        );

        return dialogBox;
    }

    /**
     * Creates a dialog box for a Xuan message.
     *
     * @param text Xuan's response
     * @param image Xuan's image
     * @return the Xuan dialog box
     */
    public static DialogBox getXuanDialog(String text, ImageView image) {
        DialogBox dialogBox = new DialogBox(text, image);

        dialogBox.getChildren().clear();
        dialogBox.getChildren().addAll(
                dialogBox.displayPicture,
                dialogBox.dialog
        );

        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.dialog.setStyle(
                "-fx-background-color: #e8e8e8;"
                        + "-fx-background-radius: 10;"
                        + "-fx-padding: 10;"
        );

        return dialogBox;
    }

    /**
     * Creates a dialog box for displaying an error message.
     *
     * @param text the error message
     * @param image Xuan's image
     * @return the error dialog box
     */
    public static DialogBox getXuanErrorDialog(String text, ImageView image) {
        DialogBox dialogBox = getXuanDialog(text, image);

        dialogBox.dialog.setStyle(
                "-fx-background-color: #ffe5e5;"
                        + "-fx-text-fill: #b00020;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 10;"
                        + "-fx-padding: 10;"
        );

        return dialogBox;
    }

    /**
     * Returns whether the given line represents a help command.
     *
     * @param line the line to check
     * @return true if the line represents a command
     */
    private static boolean isHelpCommandLine(String line) {
        return !line.isBlank()
                && !line.startsWith("    ")
                && !line.startsWith("Xuan is ")
                && !line.equals("Here are the commands you can use:");
    }

    /**
     * Creates a dialog box for displaying the help message with rich text.
     *
     * @param text the help message
     * @param image Xuan's image
     * @return the formatted help dialog box
     */
    public static DialogBox getXuanHelpDialog(
            String text, ImageView image) {
        DialogBox dialogBox = new DialogBox("", image);

        TextFlow helpText = new TextFlow();
        helpText.setPrefWidth(300.0);
        helpText.setMaxWidth(300.0);
        helpText.setStyle(
                "-fx-background-color: #e8e8e8;"
                        + "-fx-background-radius: 10;"
                        + "-fx-padding: 10;"
        );

        String[] lines = text.split("\n", -1);

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String suffix = i < lines.length - 1 ? "\n" : "";

            Text textNode = new Text(line + suffix);

            double fontSize = textNode.getFont().getSize();

            if (isHelpCommandLine(line)) {
                textNode.setFont(Font.font(
                        textNode.getFont().getFamily(),
                        FontWeight.BOLD,
                        fontSize));
            } else if (line.startsWith("    ")) {
                textNode.setFont(Font.font(
                        "Serif",
                        FontPosture.ITALIC,
                        fontSize + 1));
            }

            helpText.getChildren().add(textNode);
        }

        dialogBox.getChildren().clear();
        dialogBox.getChildren().addAll(
                dialogBox.displayPicture,
                helpText
        );

        dialogBox.setAlignment(Pos.TOP_LEFT);

        return dialogBox;
    }

    /**
     * Moves the dialog box vertically with a short animation.
     *
     * @param y the target vertical translation
     */
    private void moveDialogBox(double y) {
        TranslateTransition transition =
                new TranslateTransition(Duration.millis(120), this);
        transition.setToY(y);
        transition.play();
    }
}
