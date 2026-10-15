package labubu;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Displays the command guide inside the conversation area. */
public class HelpPanel extends VBox {
    /**
     * Creates a help panel with the supplied instructions and close action.
     *
     * @param helpText Instructions to display in the panel.
     * @param closeHandler Action to run when the panel is closed.
     */
    public HelpPanel(String helpText, Runnable closeHandler) {
        Label title = new Label("Help guide");
        title.getStyleClass().add("help-title");

        TextArea instructions = new TextArea(helpText);
        instructions.setEditable(false);
        instructions.setWrapText(true);
        instructions.getStyleClass().add("help-content");
        VBox.setVgrow(instructions, Priority.ALWAYS);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> closeHandler.run());

        setAlignment(Pos.TOP_LEFT);
        setMaxWidth(Double.MAX_VALUE);
        getStyleClass().add("help-panel");
        getChildren().addAll(title, instructions, closeButton);
    }
}
