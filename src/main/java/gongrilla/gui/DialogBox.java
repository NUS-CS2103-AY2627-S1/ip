package gongrilla.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Displays a message together with its speaker's picture. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load a dialog box", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Creates a dialog box for the user.
     *
     * @param text message to display.
     * @param image user's display picture.
     * @return dialog box with the user positioned on the right.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a styled reply from Gongrilla.
     *
     * @param text message to display.
     * @param image Gongrilla's display picture.
     * @param commandType type of command that produced the response.
     * @return dialog box with Gongrilla positioned on the left.
     */
    public static DialogBox getGongrillaDialog(String text, Image image, String commandType) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.applyCommandStyle(commandType);
        if ("HelpCommand".equals(commandType)) {
            dialogBox.showHelpSections(text);
        }
        return dialogBox;
    }

    /** Presents the same help text as the console in expandable, full-width sections. */
    private void showHelpSections(String text) {
        String[] sections = text.replace("\r\n", "\n").split("\n\\h*\n");
        Label heading = new Label(sections[0]);
        heading.getStyleClass().add("help-heading");
        heading.setWrapText(true);
        Accordion accordion = new Accordion();
        accordion.setMinWidth(0);
        for (int i = 1; i < sections.length; i++) {
            String[] section = sections[i].split("\\R", 2);
            VBox rows = new VBox(10);
            rows.getStyleClass().add("help-rows");
            if (section.length > 1) {
                for (String line : section[1].split("\\R")) {
                    Label row = new Label(line);
                    row.setWrapText(true);
                    row.setMinWidth(0);
                    row.setMinHeight(USE_PREF_SIZE);
                    row.setMaxWidth(Double.MAX_VALUE);
                    rows.getChildren().add(row);
                }
            }
            TitledPane pane = new TitledPane(section[0], rows);
            pane.setAnimated(false);
            accordion.getPanes().add(pane);
        }
        if (!accordion.getPanes().isEmpty()) {
            accordion.setExpandedPane(accordion.getPanes().get(0));
        }
        VBox card = new VBox(10, heading, accordion);
        card.setMinWidth(0);
        card.getStyleClass().add("help-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        getChildren().setAll(card);
        setMinWidth(0);
    }

    /** Places Gongrilla's picture on the left and its response on the right. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /** Applies the response color associated with the executed command. */
    private void applyCommandStyle(String commandType) {
        String styleClass = switch (commandType) {
            case "Error" -> "error-label";
            case "AddCommand" -> "add-label";
            case "MarkCommand" -> "marked-label";
            case "DeleteCommand" -> "delete-label";
            default -> null;
        };

        if (styleClass != null) {
            dialog.getStyleClass().add(styleClass);
        }
        if ("Error".equals(commandType)) {
            dialog.setText("ERROR\n" + dialog.getText());
        }
    }
}
