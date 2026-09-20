package duchess.gui;

import java.io.IOException;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

/** A reusable JavaFX strip containing shortcuts for common Duchess commands. */
public class QuickCommands extends FlowPane {
    /** Receives the canonical command selected from the squad shortcuts. */
    private Consumer<String> commandAction = command -> { };

    /** Loads the quick-command layout from its FXML view. */
    public QuickCommands() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(QuickCommands.class.getResource("/view/QuickCommands.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException | NullPointerException exception) {
            throw new IllegalStateException("Unable to load the quick-command resource.", exception);
        }
    }

    /** Sends the selected shortcut's canonical command to the main controller. */
    @FXML
    private void handleCommand(ActionEvent event) {
        Button button = (Button) event.getSource();
        commandAction.accept((String) button.getUserData());
    }

    /**
     * Sets the action to run when any squad shortcut is selected.
     *
     * @param commandAction the action receiving the selected command name
     */
    public void setCommandAction(Consumer<String> commandAction) {
        this.commandAction = commandAction;
    }
}
