package friday;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import friday.model.Deadline;
import friday.model.Event;
import friday.model.Task;

/**
 * Readable task information rendered as a card in the JavaFX conversation.
 *
 * @param category The task's human-readable category.
 * @param isDone Whether the task is complete.
 * @param title The task description.
 * @param metadata Optional schedule information.
 */
public record TaskCard(Category category, boolean isDone, String title, String metadata) {
    private static final DateTimeFormatter DEADLINE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter EVENT_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, uuuu HH:mm", Locale.ENGLISH);

    /**
     * Creates a task card from a domain task.
     *
     * @param task Task to represent.
     * @return A card with a readable category and optional schedule details.
     */
    public static TaskCard from(Task task) {
        if (task instanceof Deadline deadline) {
            return new TaskCard(Category.DEADLINE, task.isDone(), task.getDescription(),
                    "Due " + deadline.getDueDate().format(DEADLINE_FORMAT));
        }
        if (task instanceof Event event) {
            return new TaskCard(Category.EVENT, task.isDone(), task.getDescription(),
                    event.getFrom().format(EVENT_FORMAT) + " – " + event.getTo().format(EVENT_FORMAT));
        }
        return new TaskCard(Category.TODO, task.isDone(), task.getDescription(), "");
    }

    /**
     * Categories shown in GUI task cards.
     */
    public enum Category {
        TODO("To-do", "todo"),
        DEADLINE("Deadline", "deadline"),
        EVENT("Event", "event");

        private final String label;
        private final String styleKey;

        Category(String label, String styleKey) {
            this.label = label;
            this.styleKey = styleKey;
        }

        /**
         * Returns the readable label shown on the task pill.
         *
         * @return The category label.
         */
        public String getLabel() {
            return label;
        }

        /**
         * Returns the CSS style key used for the task pill.
         *
         * @return The lower-case category style key.
         */
        public String getStyleKey() {
            return styleKey;
        }
    }
}
