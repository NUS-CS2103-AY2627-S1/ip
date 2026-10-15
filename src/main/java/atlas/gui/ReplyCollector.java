package atlas.gui;

import java.util.ArrayList;
import java.util.List;

import atlas.AtlasSession;

/**
 * Collects the lines Atlas produces for one command so they can be shown in
 * the GUI, one bubble per line. Each {@code speak} message, each
 * {@code speakError} message and each plain {@code print} line becomes one
 * entry of the reply, and the reply remembers whether Atlas was reporting an
 * error so the GUI can show it in a style that catches the eye.
 */
public class ReplyCollector implements AtlasSession.Output {

    private final List<String> lines = new ArrayList<>();
    private boolean error;

    /**
     * Appends a speech-bubble message to the pending reply.
     *
     * @param message the message to append.
     */
    @Override
    public void speak(String message) {
        lines.add(message);
    }

    /**
     * Appends a plain line (e.g. one task listing row) to the pending reply.
     *
     * @param text the line to append.
     */
    @Override
    public void print(String text) {
        lines.add(text);
    }

    /**
     * Records a message Atlas sends while explaining an error, so the GUI can
     * show it in a bubble that catches the eye. A rejected command produces no
     * other output, because a command is validated before it changes anything,
     * so one flag per reply is enough.
     *
     * @param message the error message to record.
     */
    @Override
    public void speakError(String message) {
        lines.add(message);
        error = true;
    }

    /**
     * Reports whether the pending reply explains an error.
     *
     * @return {@code true} when the reply came from {@link #speakError}.
     */
    public boolean isError() {
        return error;
    }

    /**
     * Returns the reply collected since the last clear, in order, with each
     * line on its own row ready for a single multi-line bubble.
     *
     * @return the pending reply text, or an empty string when nothing arrived.
     */
    public String getReply() {
        return String.join("\n", lines);
    }

    /** Discards the current reply so the next command starts fresh. */
    public void clear() {
        lines.clear();
        error = false;
    }
}
