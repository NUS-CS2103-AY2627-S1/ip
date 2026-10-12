package duke;

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
import javafx.scene.layout.VBox;

/**
 * A dialog box with an avatar image and the speaker's text.
 * Ordinary chat text is shown as one normal text block, while each task line
 * gets its own grey rounded-rectangle background.
 */
public class DialogBox extends HBox {
    /** Maximum text width (px) before a line wraps, leaving room for the avatar and padding. */
    private static final double MAX_LINE_WIDTH = 300.0;

    @FXML
    private VBox lineContainer;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        displayPicture.setImage(img);
        buildContent(text);
    }

    /**
     * Groups consecutive non-task lines into one normal text block and renders
     * each task line as its own highlighted box.
     */
    private void buildContent(String text) {
        StringBuilder chat = new StringBuilder();
        for (String line : text.split("\n")) {
            if (isTaskLine(line)) {
                flushChat(chat);
                addLine(line, "task-line");
            } else {
                if (chat.length() > 0) {
                    chat.append("\n");
                }
                chat.append(line);
            }
        }
        flushChat(chat);
    }

    private void flushChat(StringBuilder chat) {
        if (chat.length() > 0) {
            addLine(chat.toString(), "dialog-line");
            chat.setLength(0);
        }
    }

    private void addLine(String content, String styleClass) {
        Label label = new Label(content);
        label.setWrapText(true);
        label.setMaxWidth(MAX_LINE_WIDTH);
        label.setMinHeight(Region.USE_PREF_SIZE);
        label.getStyleClass().add(styleClass);
        lineContainer.getChildren().add(label);
    }

    /**
     * Returns whether a line displays a task (i.e. contains a task type icon).
     */
    private static boolean isTaskLine(String line) {
        return line.contains("[T]") || line.contains("[D]") || line.contains("[E]");
    }

    /**
     * Flips the dialog box so the avatar is on the left and the text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        lineContainer.setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Returns a dialog box for the user's message (avatar on the right).
     *
     * @param text the user's input
     * @param img the user's avatar
     * @return the dialog box
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.lineContainer.getStyleClass().add("user-bubble");
        return db;
    }

    /**
     * Returns a dialog box for Ducke's reply (avatar flipped to the left).
     *
     * @param text Ducke's response
     * @param img Ducke's avatar
     * @return the dialog box
     */
    public static DialogBox getDukeDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        db.lineContainer.getStyleClass().add("duke-bubble");
        return db;
    }
}
