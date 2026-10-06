package com.gihun456.gui;

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
import javafx.scene.shape.Rectangle;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
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

        dialog.setText(text);
        displayPicture.setImage(img);
        Rectangle avatarClip = new Rectangle(99, 99);
        avatarClip.setArcWidth(24);
        avatarClip.setArcHeight(24);
        displayPicture.setClip(avatarClip);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    public static DialogBox getUserDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.dialog.getStyleClass().add("user-label");
        return db;
    }

    public static DialogBox getGihunDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        return db;
    }

    /**
     * Creates a bot dialog with a semantic colour style.
     *
     * @param text Text to display.
     * @param img Image representing the bot.
     * @param styleClass CSS class describing the response type.
     * @return A styled bot dialog box.
     */
    public static DialogBox getGihunDialog(String text, Image img, String styleClass) {
        var db = getGihunDialog(text, img);
        db.dialog.getStyleClass().add(styleClass);
        return db;
    }

    /**
     * Creates a bot dialog styled for preserving the alignment of ASCII art.
     *
     * @param text ASCII art and accompanying text to display.
     * @param img Image representing the bot.
     * @return A bot dialog box with monospaced text.
     */
    public static DialogBox getGihunBannerDialog(String text, Image img) {
        var db = getGihunDialog(text, img);
        db.dialog.getStyleClass().add("banner-label");
        return db;
    }
}
