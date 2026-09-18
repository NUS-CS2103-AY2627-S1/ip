package cookie.ui;

import java.util.ArrayList;
import java.util.List;

/** Collects messages so non-console frontends and tests can retrieve a complete reply. */
public class ReplyCollector implements Output {
    /** Messages collected since the most recent clear. */
    private final List<String> messages = new ArrayList<>();

    /** Adds one message to the current reply. */
    @Override
    public void show(String message) {
        messages.add(message);
    }

    /**
     * Returns all collected messages in their original order.
     *
     * @return The collected reply, with separate messages on separate lines.
     */
    public String getReply() {
        return String.join(System.lineSeparator(), messages);
    }

    /** Removes all previously collected messages. */
    public void clear() {
        messages.clear();
    }
}
