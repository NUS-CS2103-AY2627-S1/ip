package labubu;

import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/** Provides the command input field and send button. */
public class InputField extends HBox {
    private final TextField input = new TextField();

    /** Creates an input field that sends commands to the supplied handler. */
    public InputField(Consumer<String> commandHandler) {
        setSpacing(8);
        getStyleClass().add("composer");
        input.setPromptText("Type a command, e.g. list or todo read book");
        Button send = new Button("Send");
        send.setOnAction(event -> submit(commandHandler));
        input.setOnAction(event -> submit(commandHandler));
        HBox.setHgrow(input, javafx.scene.layout.Priority.ALWAYS);
        getChildren().addAll(input, send);
    }

    private void submit(Consumer<String> commandHandler) {
        String command = input.getText().trim();
        if (!command.isEmpty()) {
            commandHandler.accept(command);
            input.clear();
        }
    }
}
