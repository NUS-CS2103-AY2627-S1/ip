package stewie.model;

/**
 * Creates validated task replacements without mutating the original task.
 */
final class TaskUpdater {
    private TaskUpdater() {
    }

    /**
     * Builds a replacement while preserving the original completion status.
     */
    static Task update(Task existing, String description, String deadline, String from, String to) {
        Task replacement = createTaskWithUpdatedFields(existing, description, deadline, from, to);
        if (existing.isDone()) {
            replacement.markAsDone();
        }
        return replacement;
    }

    /**
     * Selects the field-update rules for the existing task's type.
     */
    private static Task createTaskWithUpdatedFields(Task existing, String description, String deadline,
            String from, String to) {
        String updatedDescription = description == null ? existing.getDescription() : description;
        if (existing instanceof Deadline deadlineTask) {
            return createUpdatedDeadline(deadlineTask, updatedDescription, deadline, from, to);
        }
        if (existing instanceof Event event) {
            return createUpdatedEvent(event, updatedDescription, deadline, from, to);
        }
        return createUpdatedTodo(updatedDescription, deadline, from, to);
    }

    /**
     * Validates deadline fields and retains the date when it was omitted.
     */
    private static Task createUpdatedDeadline(Deadline existing, String description, String deadline,
            String from, String to) {
        if (from != null || to != null) {
            throw new IllegalArgumentException("Deadline tasks only support descriptions and deadlines.");
        }
        String updatedDeadline = deadline == null ? existing.getDeadline() : deadline;
        return new Deadline(description, updatedDeadline);
    }

    /**
     * Validates event fields and retains each boundary when it was omitted.
     */
    private static Task createUpdatedEvent(Event existing, String description, String deadline,
            String from, String to) {
        if (deadline != null) {
            throw new IllegalArgumentException("Event tasks only support descriptions and event times.");
        }
        String updatedFrom = from == null ? existing.getFrom() : from;
        String updatedTo = to == null ? existing.getTo() : to;
        return new Event(description, updatedFrom, updatedTo);
    }

    /**
     * Rejects date fields for todos before constructing the replacement.
     */
    private static Task createUpdatedTodo(String description, String deadline, String from, String to) {
        if (deadline != null || from != null || to != null) {
            throw new IllegalArgumentException("Todo tasks only support descriptions.");
        }
        return new ToDo(description);
    }

    /**
     * Copies all task details and applies the requested completion state to the copy.
     */
    static Task withStatus(Task existing, boolean isDone) {
        Task replacement = createTaskWithUpdatedFields(existing, null, null, null, null);
        if (isDone) {
            replacement.markAsDone();
        }
        return replacement;
    }
}
