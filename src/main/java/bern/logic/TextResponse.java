package bern.logic;

import java.util.List;

import bern.ui.DialogBox;
import javafx.scene.Node;

/**
 * Represents a response from Bern that contains only text.
 */
public class TextResponse extends Response {
    /**
     * Creates a response with the supplied message.
     *
     * @param text The message to display in Bern's dialog box.
     */
    public TextResponse(String text) {
        this.text = text;
    }

    @Override
    public List<Node> getResponseNodes() {
        return List.of(DialogBox.getBernDialog(text));
    }

    /**
     * Returns the response message as plain text.
     *
     * @return The message supplied when this response was created.
     */
    @Override
    public String toString() {
        return text;
    }
}
