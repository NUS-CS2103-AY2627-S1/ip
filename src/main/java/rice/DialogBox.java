package rice;

import java.io.IOException;
import java.util.Collections;

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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;


/**
 * Represents one chat message with text and a display picture.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;
    @FXML
    private Label inputLabel;
    @FXML
    private ImageView displayPicture;
    @FXML
    private StackPane bubbleContainer;
    @FXML
    private Label warningIcon;
    @FXML
    private Label suggestionIcon;
    @FXML
    private Label suggestion;
    @FXML
    private HBox suggestionRow;
    @FXML
    private Label headerLabel;


    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        warningIcon.setVisible(false);
        warningIcon.setManaged(false);
        inputLabel.setVisible(false);
        inputLabel.setManaged(false);
        suggestionRow.setVisible(false);
        suggestionRow.setManaged(false);
        headerLabel.setVisible(false);
        headerLabel.setManaged(false);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    public static DialogBox getUserDialog(String s, Image i) {
        return new DialogBox(s, i);
    }

    public static DialogBox getRiceDialog(String s, Image i) {
        var db = new DialogBox(s, i);
        db.headerLabel.setVisible(true);
        db.headerLabel.setManaged(true);
        db.flip();
        return db;
    }

    /**
     * Creates a Rice error dialog with a warning indicator.
     *
     * @param message error message to display
     * @param input rejected user input
     * @param image Rice's display picture
     * @return warning dialog box
     */
    public static DialogBox getErrorDialog(String message, String input, Image image) {
        var db = new DialogBox(message, image);
        db.warningIcon.setVisible(true);
        db.warningIcon.setManaged(true);
        db.inputLabel.setText("Your input: \"" + input + "\"");
        db.inputLabel.setVisible(true);
        db.inputLabel.setManaged(true);
        db.bubbleContainer.setStyle("-fx-background-color: #fff4e5;"
                + "-fx-border-color: #e07a2d;"
                + "-fx-border-width: 1.5;"
                + "-fx-border-radius: 14;"
                + "-fx-background-radius: 14;");
        db.flip();
        return db;
    }

    /**
     * Creates a Rice dialog containing a suggested correction.
     *
     * @param suggestionText suggested correction for the user
     * @param image Rice's display picture
     * @return suggestion dialog box
     */
    public static DialogBox getSuggestionDialog(String suggestionText, Image image) {
        var db = new DialogBox("Suggestion: " + suggestionText, image);
        db.suggestionRow.setVisible(true);
        db.suggestionRow.setManaged(true);
        db.suggestion.setText("");
        db.dialog.setPrefWidth(265.0);
        db.dialog.setMaxWidth(265.0);
        db.dialog.setMinHeight(Region.USE_PREF_SIZE);
        db.bubbleContainer.setStyle("-fx-background-color: #e8f5e9;"
                + "-fx-border-color: #2f855a;"
                + "-fx-border-width: 1.5;"
                + "-fx-border-radius: 14;"
                + "-fx-background-radius: 14;");
        db.flip();
        return db;
    }
}
