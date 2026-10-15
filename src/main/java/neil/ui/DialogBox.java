package neil.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Represents one message and speaker placeholder in the chat window.
 */
public class DialogBox extends HBox {
    private static final String ERROR_TITLE = "Couldn't process that";
    private static final double NEIL_CONTENT_WIDTH_RATIO = 0.86;
    private static final double USER_CONTENT_WIDTH_RATIO = 0.70;
    private static final String NEIL_PLACEHOLDER = "N";

    @FXML
    private Label dialog;
    @FXML
    private Label displayPicture;
    @FXML
    private Label errorTitle;
    @FXML
    private Label helpTable;
    @FXML
    private ScrollPane helpScrollPane;
    @FXML
    private Label welcomeBanner;
    @FXML
    private VBox dialogContent;

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box view", e);
        }

        assert dialog != null : "FXML must inject dialog";
        assert displayPicture != null : "FXML must inject displayPicture";
        assert errorTitle != null : "FXML must inject errorTitle";
        assert helpTable != null : "FXML must inject helpTable";
        assert helpScrollPane != null : "FXML must inject helpScrollPane";
        assert welcomeBanner != null : "FXML must inject welcomeBanner";
        assert dialogContent != null : "FXML must inject dialogContent";

        dialog.setText(text);
    }

    private void limitContentWidth(double maximumWidthRatio) {
        dialogContent.maxWidthProperty().bind(widthProperty().multiply(maximumWidthRatio));
    }

    private void hideDisplayPicture() {
        displayPicture.setManaged(false);
        displayPicture.setVisible(false);
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    private void formatWelcomeMessage() {
        String[] welcomeParts = dialog.getText().split("\\n\\n", 2);
        if (welcomeParts.length != 2) {
            return;
        }

        welcomeBanner.setText(welcomeParts[0]);
        welcomeBanner.setManaged(true);
        welcomeBanner.setVisible(true);
        dialog.setText(welcomeParts[1]);
    }

    private void formatErrorMessage() {
        errorTitle.setText(ERROR_TITLE);
        errorTitle.setManaged(true);
        errorTitle.setVisible(true);
    }

    private void formatHelpMessage() {
        helpTable.setText(dialog.getText());
        helpScrollPane.setManaged(true);
        helpScrollPane.setVisible(true);
        dialog.setManaged(false);
        dialog.setVisible(false);
    }

    /**
     * Returns a right-aligned dialog containing a user message.
     *
     * @param text message entered by the user.
     * @return user dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.hideDisplayPicture();
        dialogBox.limitContentWidth(USER_CONTENT_WIDTH_RATIO);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Returns a left-aligned dialog containing a Neil response.
     *
     * @param text response produced by Neil.
     * @return Neil dialog box.
     */
    public static DialogBox getNeilDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.displayPicture.setText(NEIL_PLACEHOLDER);
        dialogBox.limitContentWidth(NEIL_CONTENT_WIDTH_RATIO);
        dialogBox.flip();
        dialogBox.getStyleClass().add("neil-dialog");
        return dialogBox;
    }

    /**
     * Returns a Neil dialog containing a horizontally scrollable help table.
     *
     * @param text formatted help table produced by Neil.
     * @return Neil help dialog box.
     */
    public static DialogBox getNeilHelpDialog(String text) {
        DialogBox dialogBox = getNeilDialog(text);
        dialogBox.formatHelpMessage();
        dialogBox.getStyleClass().add("help-dialog");
        return dialogBox;
    }

    /**
     * Returns a visually highlighted Neil dialog containing an error message.
     *
     * @param text error response produced by Neil.
     * @return Neil error dialog box.
     */
    public static DialogBox getNeilErrorDialog(String text) {
        DialogBox dialogBox = getNeilDialog(text);
        dialogBox.formatErrorMessage();
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }

    /**
     * Returns a Neil dialog with a monospaced banner and regular greeting text.
     *
     * @param text welcome message containing the banner and greeting.
     * @return Neil welcome dialog box.
     */
    public static DialogBox getNeilWelcomeDialog(String text) {
        DialogBox dialogBox = getNeilDialog(text);
        dialogBox.formatWelcomeMessage();
        return dialogBox;
    }
}
