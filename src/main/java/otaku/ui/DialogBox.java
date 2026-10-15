package otaku.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/** A reusable chat bubble containing a speaker badge and message. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private StackPane avatar;
    @FXML
    private ImageView otakuLogo;
    @FXML
    private Label errorIcon;

    private DialogBox(String text) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box view", e);
        }
        dialog.setText(text);
        widthProperty().addListener((observable, oldWidth, newWidth) ->
                dialog.setMaxWidth(Math.max(240, newWidth.doubleValue() * 0.78 - 54)));
    }

    /** Creates a right-aligned message from the user. */
    public static DialogBox getUserDialog(String text) {
        DialogBox box = new DialogBox(text);
        box.avatar.setManaged(false);
        box.avatar.setVisible(false);
        box.getStyleClass().add("user-dialog");
        return box;
    }

    /** Creates a left-aligned message from Otaku. */
    public static DialogBox getOtakuDialog(String text) {
        return getOtakuDialog(text, false);
    }

    /** Creates Otaku's startup greeting with formatting that preserves its ASCII-art banner. */
    public static DialogBox getOtakuGreetingDialog(String text) {
        DialogBox box = getOtakuDialog(text);
        int plainTextStart = text.indexOf("\nHello!");
        if (plainTextStart < 0) {
            return box;
        }

        Label banner = new Label(text.substring(0, plainTextStart));
        banner.setFont(Font.font("Monospaced", FontWeight.NORMAL, FontPosture.REGULAR, 12));

        Label plainText = new Label(text.substring(plainTextStart + 1));
        plainText.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 14));
        plainText.setWrapText(true);

        box.dialog.setText("");
        box.dialog.setGraphic(new VBox(4, banner, plainText));
        box.dialog.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        return box;
    }

    /** Creates a left-aligned message from Otaku, highlighting it when it is an error. */
    public static DialogBox getOtakuDialog(String text, boolean isError) {
        DialogBox box = new DialogBox(text);
        box.otakuLogo.setVisible(!isError);
        box.errorIcon.setVisible(isError);
        box.getStyleClass().add("otaku-dialog");
        if (isError) {
            box.getStyleClass().add("error-dialog");
        }
        ObservableList<Node> reversed = FXCollections.observableArrayList(box.getChildren());
        Collections.reverse(reversed);
        box.getChildren().setAll(reversed);
        box.setAlignment(Pos.TOP_LEFT);
        return box;
    }
}
