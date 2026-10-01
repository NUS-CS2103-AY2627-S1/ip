package bo.ui;

/** Represents a message waiting to be displayed in the graphical interface. */
public record GuiMessage(String text, MessageType type) {
    /** Identifies the visual treatment and owl expression for a GUI message. */
    public enum MessageType {
        NORMAL,
        SUCCESS,
        ERROR
    }
}
