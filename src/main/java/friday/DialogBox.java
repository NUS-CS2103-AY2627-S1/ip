package friday;

import java.io.IOException;
import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableNumberValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

/**
 * Reusable, responsive chat message control backed by {@code DialogBox.fxml}.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private HBox bodyRow;

    @FXML
    private VBox avatar;

    @FXML
    private VBox card;

    @FXML
    private VBox actions;

    @FXML
    private VBox taskList;

    @FXML
    private Label footer;

    @FXML
    private SVGPath warningIcon;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box and loads its FXML-defined child controls.
     *
     * @param text Message text to display.
     * @param image Conversation participant profile image.
     */
    private DialogBox(String text, Image image) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DialogBox.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load the dialog box view.", e);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(14, 14, 14));
    }

    /**
     * Creates a compact, right-aligned user command dialog.
     *
     * @param text User command.
     * @param image User profile image.
     * @return A dialog containing the user command.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.card.getStyleClass().setAll("bubble-user");
        dialogBox.actions.setManaged(false);
        dialogBox.actions.setVisible(false);
        dialogBox.getStyleClass().setAll("dialog-row-user");
        dialogBox.getChildren().setAll(dialogBox.card, dialogBox.avatar);
        dialogBox.setAlignment(Pos.BOTTOM_RIGHT);
        return dialogBox;
    }

    /**
     * Creates a left-aligned FRIDAY reply dialog.
     *
     * @param text Chatbot message.
     * @param image FRIDAY profile image.
     * @return A dialog containing the chatbot message.
     */
    public static DialogBox getChatbotDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a chatbot-aligned error dialog.
     *
     * @param text Error message.
     * @param image FRIDAY profile image.
     * @return An error dialog that draws attention to invalid input.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.card.getStyleClass().setAll("card-error");
        dialogBox.bodyRow.getStyleClass().add("error-body-row");
        dialogBox.warningIcon.setManaged(true);
        dialogBox.warningIcon.setVisible(true);
        dialogBox.actions.setManaged(false);
        dialogBox.actions.setVisible(false);
        return dialogBox;
    }

    /**
     * Binds the message card to the available conversation width.
     *
     * @param conversationWidth Width of the scrollable conversation area.
     */
    public void bindMessageWidth(ObservableNumberValue conversationWidth) {
        double reservedWidth = getStyleClass().contains("dialog-row-user") ? 36 : 76;
        card.maxWidthProperty().bind(Bindings.min(480,
                Bindings.max(160, Bindings.subtract(conversationWidth, reservedWidth))));
        dialog.maxWidthProperty().bind(card.maxWidthProperty().subtract(30));
    }

    /**
     * Displays structured task cards beneath an optional reply introduction.
     *
     * @param taskCards Tasks to display.
     * @param footerText Optional summary displayed after the cards.
     */
    public void setTaskCards(List<TaskCard> taskCards, String footerText) {
        taskList.getChildren().setAll(taskCards.stream().map(DialogBox::buildTaskRow).toList());
        boolean hasTasks = !taskCards.isEmpty();
        taskList.setManaged(hasTasks);
        taskList.setVisible(hasTasks);
        boolean hasFooter = footerText != null && !footerText.isBlank();
        footer.setText(hasFooter ? footerText : "");
        footer.setManaged(hasFooter);
        footer.setVisible(hasFooter);
    }

    private static HBox buildTaskRow(TaskCard taskCard) {
        StackPane checkbox = new StackPane();
        checkbox.getStyleClass().add(taskCard.isDone() ? "task-check-done" : "task-check-empty");
        if (taskCard.isDone()) {
            SVGPath check = new SVGPath();
            check.setContent("M4 12.5 L9.5 18 L20 6");
            check.getStyleClass().add("task-check-icon");
            checkbox.getChildren().add(check);
        }

        Label title = new Label(taskCard.title());
        title.getStyleClass().add("task-title");
        title.setWrapText(true);
        Label metadata = new Label(taskCard.metadata());
        metadata.getStyleClass().add("task-meta");
        boolean hasMetadata = !taskCard.metadata().isBlank();
        metadata.setManaged(hasMetadata);
        metadata.setVisible(hasMetadata);
        VBox textColumn = new VBox(3, title, metadata);
        HBox.setHgrow(textColumn, Priority.ALWAYS);

        Label category = new Label(taskCard.category().getLabel());
        category.getStyleClass().addAll("task-pill", "task-pill-" + taskCard.category().getStyleKey());
        HBox row = new HBox(10, checkbox, textColumn, category);
        row.getStyleClass().add("task-row");
        row.setAlignment(Pos.TOP_LEFT);
        return row;
    }

    /**
     * Copies this chatbot response to the system clipboard.
     */
    @FXML
    private void handleCopy() {
        ClipboardContent content = new ClipboardContent();
        content.putString(dialog.getText());
        Clipboard.getSystemClipboard().setContent(content);
    }
}
