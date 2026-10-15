package gnaix;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Represents a dialog box in the Gnaix GUI.
 */
public class DialogBox extends HBox {
    private static final double USER_BUBBLE_MAX_WIDTH = 260.0;
    private static final double GNAIX_BUBBLE_MAX_WIDTH = 520.0;
    private static final double USER_BUBBLE_WIDTH_RATIO = 0.68;
    private static final double GNAIX_BUBBLE_WIDTH_RATIO = 0.88;

    @FXML
    private Label dialog;

    @FXML
    private VBox contentContainer;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load dialog box.", e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        contentContainer.setFillWidth(true);
        getStyleClass().add("user-dialog");
        bindContentWidth(USER_BUBBLE_WIDTH_RATIO, USER_BUBBLE_MAX_WIDTH);
    }

    /**
     * Flips the dialog box to display Gnaix's replies on the left.
     */
    private void flip() {
        ObservableList<Node> children =
                FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().remove("user-dialog");
        getStyleClass().add("gnaix-dialog");
        bindContentWidth(GNAIX_BUBBLE_WIDTH_RATIO, GNAIX_BUBBLE_MAX_WIDTH);
        contentContainer.getStyleClass().add("reply-bubble");
    }

    /**
     * Keeps message bubbles readable at narrow and wide window sizes.
     *
     * @param widthRatio Portion of the dialog row the bubble may use.
     * @param maxWidth Absolute maximum bubble width.
     */
    private void bindContentWidth(double widthRatio, double maxWidth) {
        contentContainer.maxWidthProperty().unbind();
        contentContainer.maxWidthProperty().bind(
                Bindings.min(widthProperty().multiply(widthRatio), maxWidth));
    }

    /**
     * Creates a dialog box containing user input.
     *
     * @param text User input.
     * @param image User profile image.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a dialog box containing Gnaix's response.
     *
     * @param text Gnaix response.
     * @param image Gnaix profile image.
     * @return Gnaix dialog box.
     */
    public static DialogBox getGnaixDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a dialog box containing Gnaix's structured GUI response.
     *
     * @param response Gnaix response with optional task display data.
     * @param image Gnaix profile image.
     * @return Gnaix dialog box.
     */
    public static DialogBox getGnaixDialog(GuiResponse response, Image image) {
        if (!response.hasTasks()) {
            DialogBox dialogBox = getGnaixDialog(response.getText(), image);
            dialogBox.applyErrorStyle(response);
            return dialogBox;
        }

        DialogBox dialogBox = new DialogBox("", image);
        dialogBox.contentContainer.getChildren().setAll(dialogBox.createResponseContent(response));
        dialogBox.flip();
        dialogBox.applyErrorStyle(response);
        return dialogBox;
    }

    /**
     * Applies subtle error styling when a GUI response represents a problem.
     *
     * @param response Response to inspect.
     */
    private void applyErrorStyle(GuiResponse response) {
        if (!response.isError()) {
            return;
        }

        contentContainer.getStyleClass().add("error-bubble");
        dialog.getStyleClass().add("error-text");
        dialog.setText("! " + dialog.getText());
    }

    /**
     * Builds the rich task response content for this dialog.
     *
     * @param response Response containing task data.
     * @return Nodes used inside the response bubble.
     */
    private VBox createResponseContent(GuiResponse response) {
        VBox content = new VBox();
        content.getStyleClass().add("task-response");
        content.setFillWidth(true);

        Label heading = new Label(response.getHeading());
        heading.getStyleClass().add("dialog-text");
        heading.setWrapText(true);
        heading.maxWidthProperty().bind(contentContainer.widthProperty());
        content.getChildren().add(heading);

        response.getTasks().forEach(task -> content.getChildren().add(new TaskCard(task)));

        if (!response.getFooter().isBlank()) {
            Label footer = new Label(response.getFooter());
            footer.getStyleClass().add("dialog-text");
            footer.setWrapText(true);
            footer.maxWidthProperty().bind(contentContainer.widthProperty());
            content.getChildren().add(footer);
        }

        return content;
    }
}
