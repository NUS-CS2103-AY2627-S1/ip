package bobo.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Custom control representing a dialog box with an avatar ImageView and a Label for message text.
 */
public class DialogBox extends HBox {

    private enum DialogType {
        USER,
        BOBO_NORMAL,
        BOBO_ERROR
    }

    private final Label textLabel;
    private final ImageView displayPicture;

    private DialogBox(String text, Image img, DialogType dialogType) {
        textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(310);

        displayPicture = new ImageView();
        displayPicture.setFitWidth(36);
        displayPicture.setFitHeight(36);
        displayPicture.setPreserveRatio(true);

        if (img != null && !img.isError()) {
            displayPicture.setImage(img);
            Circle clip = new Circle(18, 18, 18);
            displayPicture.setClip(clip);
        }

        setSpacing(10);
        setupStyle(dialogType);
    }

    private void setupStyle(DialogType dialogType) {
        if (dialogType == DialogType.USER) {
            textLabel.setStyle("-fx-background-color: #007AFF; -fx-text-fill: #FFFFFF; "
                    + "-fx-background-radius: 16px 16px 4px 16px; -fx-padding: 10px 14px; "
                    + "-fx-font-size: 13px; -fx-font-family: 'Segoe UI', sans-serif;");
            setAlignment(Pos.TOP_RIGHT);
            getChildren().addAll(textLabel, displayPicture);
        } else if (dialogType == DialogType.BOBO_ERROR) {
            textLabel.setStyle("-fx-background-color: #FFF5F5; -fx-text-fill: #9B2C2C; "
                    + "-fx-border-color: #FEB2B2; -fx-border-radius: 16px 16px 16px 4px; "
                    + "-fx-background-radius: 16px 16px 16px 4px; -fx-padding: 10px 14px; "
                    + "-fx-font-size: 13px; -fx-font-family: 'Segoe UI', sans-serif; "
                    + "-fx-effect: dropshadow(three-pass-box, rgba(155,44,44,0.12), 6, 0, 0, 2);");
            setAlignment(Pos.TOP_LEFT);
            getChildren().addAll(displayPicture, textLabel);
        } else {
            textLabel.setStyle("-fx-background-color: #EBF8FF; -fx-text-fill: #1A202C; "
                    + "-fx-border-color: #BAE6FD; -fx-border-radius: 16px 16px 16px 4px; "
                    + "-fx-background-radius: 16px 16px 16px 4px; -fx-padding: 10px 14px; "
                    + "-fx-font-size: 13px; -fx-font-family: 'Segoe UI', sans-serif; "
                    + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 6, 0, 0, 2);");
            setAlignment(Pos.TOP_LEFT);
            getChildren().addAll(displayPicture, textLabel);
        }
    }

    /**
     * Creates a user dialog box with avatar on the right.
     *
     * @param text Message text.
     * @param img  User avatar image.
     * @return User DialogBox instance.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img, DialogType.USER);
    }

    /**
     * Creates a normal Bobo dialog box with avatar on the left.
     *
     * @param text Message text.
     * @param img  Bobo avatar image.
     * @return Bobo DialogBox instance.
     */
    public static DialogBox getBoboDialog(String text, Image img) {
        return new DialogBox(text, img, DialogType.BOBO_NORMAL);
    }

    /**
     * Creates an error Bobo dialog box with crimson highlight styling.
     *
     * @param text Error message text.
     * @param img  Bobo avatar image.
     * @return Bobo Error DialogBox instance.
     */
    public static DialogBox getBoboErrorDialog(String text, Image img) {
        return new DialogBox(text, img, DialogType.BOBO_ERROR);
    }
}
