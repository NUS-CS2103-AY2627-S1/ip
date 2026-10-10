package cassava.gui;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents a single dialog box in the GUI,
 * which is a message sent by the user or the chatbot.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;

    /**
     * Default DialogBox constructor.
     * @param text Message to display in the DialogBox.
     */
    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/views/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }

        dialog.setText(text);
    }

    /**
     * Flips the orientation of the DialogBox,
     * to be aligned to the left instead of aligned to the right.
     */
    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a DialogBox for a user message.
     * @param s Message to display.
     * @return DialogBox with user message.
     */
    public static DialogBox getUserDialog(String s) {
        return new DialogBox(s);
    }

    /**
     * Creates a DialogBox for a Cassava message.
     * @param s Message to display.
     * @return DialogBox with Cassava message.
     */
    public static DialogBox getCassavaDialog(String s) {
        var db = new DialogBox(s);
        db.flip();
        return db;
    }

    /**
     * Creates a DialogBox for a Cassava error message.
     * @param s Error message to display.
     * @return DialogBox with error message.
     */
    public static DialogBox getErrorDialog(String s) {
        var db = new DialogBox("[!] " + s);
        db.flip();
        db.dialog.getStyleClass().add("error-label");
        return db;
    }

}
