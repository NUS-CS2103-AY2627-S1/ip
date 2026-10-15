package larry.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableNumberValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Represents a user command or a response from Larry.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 80.0;
    private static final double DIALOG_FONT_SIZE = 22.0;
    private static final double DIALOG_HORIZONTAL_PADDING = 16.0;
    private static final double DIALOG_VERTICAL_PADDING = 12.0;
    private static final double SPEAKER_FONT_SIZE = 30.0;
    private static final double USER_MESSAGE_WIDTH_RATIO = 0.76;
    private static final double LARRY_MESSAGE_HORIZONTAL_SPACE = 116.0;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;
    @FXML
    private Label speakerName;

    private DialogBox(String text, Image image, ObservableNumberValue interfaceScale,
            FontWeight dialogFontWeight) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box.", e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        bindResponsiveSizes(interfaceScale, dialogFontWeight);
    }

    /**
     * Scales the text, avatar, and bubble padding with the main window.
     *
     * @param interfaceScale Scale derived from the main window dimensions.
     * @param dialogFontWeight Weight used for the message text.
     */
    private void bindResponsiveSizes(ObservableNumberValue interfaceScale,
            FontWeight dialogFontWeight) {
        displayPicture.fitHeightProperty().bind(Bindings.multiply(AVATAR_SIZE, interfaceScale));
        displayPicture.fitWidthProperty().bind(Bindings.multiply(AVATAR_SIZE, interfaceScale));

        Circle avatarClip = new Circle();
        avatarClip.centerXProperty().bind(displayPicture.fitWidthProperty().divide(2.0));
        avatarClip.centerYProperty().bind(displayPicture.fitHeightProperty().divide(2.0));
        avatarClip.radiusProperty().bind(displayPicture.fitWidthProperty().divide(2.0));
        displayPicture.setClip(avatarClip);

        dialog.fontProperty().bind(Bindings.createObjectBinding(() ->
                Font.font("System", dialogFontWeight,
                        DIALOG_FONT_SIZE * interfaceScale.doubleValue()),
                interfaceScale));
        dialog.paddingProperty().bind(Bindings.createObjectBinding(() ->
                new Insets(DIALOG_VERTICAL_PADDING * interfaceScale.doubleValue(),
                        DIALOG_HORIZONTAL_PADDING * interfaceScale.doubleValue(),
                        DIALOG_VERTICAL_PADDING * interfaceScale.doubleValue(),
                        DIALOG_HORIZONTAL_PADDING * interfaceScale.doubleValue()),
                interfaceScale));
        speakerName.fontProperty().bind(Bindings.createObjectBinding(() ->
                Font.font("Unutterable", SPEAKER_FONT_SIZE * interfaceScale.doubleValue()),
                interfaceScale));
    }

    /**
     * Creates a right-aligned dialog for a user's input.
     *
     * @param text User's input.
     * @param interfaceScale Scale derived from the main window dimensions.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text, ObservableNumberValue interfaceScale) {
        DialogBox dialogBox = new DialogBox(text, null, interfaceScale, FontWeight.NORMAL);
        dialogBox.displayPicture.setManaged(false);
        dialogBox.displayPicture.setVisible(false);
        dialogBox.speakerName.setManaged(false);
        dialogBox.speakerName.setVisible(false);
        dialogBox.dialog.maxWidthProperty().bind(
                Bindings.max(120.0, dialogBox.widthProperty().multiply(USER_MESSAGE_WIDTH_RATIO)));
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned, command-styled dialog for Larry's response.
     *
     * @param text Larry's response.
     * @param image Larry's avatar.
     * @param commandType Type of command that produced the response.
     * @param interfaceScale Scale derived from the main window dimensions.
     * @return Larry dialog box.
     */
    public static DialogBox getLarryDialog(String text, Image image, String commandType,
            ObservableNumberValue interfaceScale) {
        FontWeight fontWeight = commandType.equals("Error") ? FontWeight.BOLD : FontWeight.NORMAL;
        DialogBox dialogBox = new DialogBox(text, image, interfaceScale, fontWeight);
        dialogBox.flip();
        dialogBox.dialog.maxWidthProperty().bind(
                Bindings.max(120.0, dialogBox.widthProperty().subtract(
                        Bindings.multiply(LARRY_MESSAGE_HORIZONTAL_SPACE, interfaceScale))));
        dialogBox.getStyleClass().add("larry-dialog");
        dialogBox.changeDialogStyle(commandType);
        return dialogBox;
    }

    /**
     * Places the avatar on the left and points the reply bubble toward it.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("larry-label");
    }

    /**
     * Applies a response color that communicates the result of the command.
     *
     * @param commandType Simple name of the command class, or {@code Error}.
     */
    private void changeDialogStyle(String commandType) {
        switch (commandType) {
            case "AddCommand" -> dialog.getStyleClass().add("success-label");
            case "MarkCommand", "UnmarkCommand" -> dialog.getStyleClass().add("status-label");
            case "DeleteCommand" -> dialog.getStyleClass().add("delete-label");
            case "Error" -> dialog.getStyleClass().add("error-label");
            default -> {
                // Keep the standard reply style for commands without a specialized color.
            }
        }
    }
}
