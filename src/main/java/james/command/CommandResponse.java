package james.command;

import java.util.Objects;

/**
 * Represents the result of processing one user command.
 */
public class CommandResponse {
    /**
     * Describes the response category used by graphical styling.
     */
    public enum Type { NORMAL, ADD, MARK, DELETE, ERROR }

    /**
     * Describes how the graphical interface displays the response.
     */
    public enum DisplayMode { TEXT_ONLY, STICKER_ONLY, STICKER_WITH_TEXT }

    private final String message;
    private final Type type;
    private final boolean isExit;
    private final Sticker sticker;
    private final DisplayMode displayMode;

    /**
     * Creates a text response.
     *
     * @param message Response text.
     * @param type Response category.
     * @param isExit Whether the application should exit.
     */
    public CommandResponse(String message, Type type, boolean isExit) {
        this(message, type, isExit, null, DisplayMode.TEXT_ONLY);
    }

    /**
     * Creates a response without trailing line breaks, preserving its internal text layout.
     */
    private CommandResponse(String message, Type type, boolean isExit, Sticker sticker, DisplayMode displayMode) {
        this.message = message.replaceAll("[\\r\\n]+$", "");
        this.type = type;
        this.isExit = isExit;
        this.sticker = sticker;
        this.displayMode = displayMode;
    }

    /**
     * Creates a reply containing a text followed by its sticker.
     *
     * @param message Response text.
     * @param type Response category.
     * @param sticker Sticker accompanying the text.
     * @return A non-exiting response containing both sticker and text.
     */
    public static CommandResponse createWithSticker(String message, Type type, Sticker sticker) {
        return new CommandResponse(message, type, false,
                Objects.requireNonNull(sticker), DisplayMode.STICKER_WITH_TEXT);
    }

    /**
     * Creates a graphical sticker-only reply with a console fallback.
     *
     * @param message Text displayed by the console.
     * @param sticker Selected sticker.
     * @return A non-exiting sticker response.
     */
    public static CommandResponse createStickerOnly(String message, Sticker sticker) {
        return new CommandResponse(message, Type.NORMAL, false,
                Objects.requireNonNull(sticker), DisplayMode.STICKER_ONLY);
    }

    public String getStickerPath() {
        return sticker == null ? null : sticker.getResourcePath();
    }

    public Sticker getSticker() {
        return sticker;
    }

    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    public String getMessage() {
        return message;
    }

    public Type getType() {
        return type;
    }

    public boolean isExit() {
        return isExit;
    }
}
