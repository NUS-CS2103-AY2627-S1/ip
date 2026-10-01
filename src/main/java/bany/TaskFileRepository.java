package bany;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import bany.exceptions.InvalidTaskType;
import bany.tags.Tag;
import bany.tasks.Deadline;
import bany.tasks.Event;
import bany.tasks.Task;
import bany.tasks.ToDo;

/** Persists and reconstructs Bany tasks in a JSON file. */
public class TaskFileRepository {

    /** File used to store the task list. */
    private final Path path;
    /** JSON mapper used to convert task data to and from tree nodes. */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a repository for a task file.
     *
     * @param path file used for persistence.
     * @throws NullPointerException if {@code path} is null.
     */
    public TaskFileRepository(Path path) {
        this.path = Objects.requireNonNull(path, "Task file path cannot be null.");
    }

    /**
     * Loads startup data, preserving an unreadable file before starting an empty list.
     * Missing files retain the normal first-run behavior and do not trigger recovery.
     *
     * @param taskStorage storage to populate or reset after a load failure.
     * @param reportDirectory directory in which to preserve unreadable files.
     * @return true if the startup file was replaced after a load failure.
     * @throws IOException if the original cannot be archived or the replacement cannot be created.
     */
    public boolean loadWithRecovery(TaskStorage taskStorage, Path reportDirectory) throws IOException {
        Objects.requireNonNull(reportDirectory, "Report directory cannot be null.");
        try {
            load(taskStorage);
            return false;
        } catch (IOException loadFailure) {
            taskStorage.replaceTasks(List.of());
            Task.resetIdAllocator(1);
            try {
                if (Files.isDirectory(path)) {
                    throw new IOException("The task file path points to a directory.");
                }
                Files.createDirectories(reportDirectory);
                Path archivedFile = reportDirectory.resolve("bany-" + UUID.randomUUID() + ".txt");
                // Never overwrite the source until its contents have been preserved.
                Files.move(path, archivedFile);
                Files.writeString(path, "[]", StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                return true;
            } catch (IOException recoveryFailure) {
                recoveryFailure.addSuppressed(loadFailure);
                throw recoveryFailure;
            }
        }
    }

    /**
     * Converts one task into the JSON representation used by the repository.
     *
     * @param task task to convert.
     * @return JSON object containing the task data.
     * @throws InvalidTaskType if the task type is unsupported.
     */
    private ObjectNode toJson(Task task) {
        ObjectNode json = mapper.createObjectNode();
        // Task IDs are runtime-only and are reconstructed from file order when loading.
        json.put("type", task.getType());
        json.put("description", task.getDescription());
        json.put("done", task.isDone());
        ObjectNode tags = mapper.createObjectNode();
        for (Tag tag : task.getTags()) {
            tags.put(tag.name(), tag.value().orElse(""));
        }
        json.set("tags", tags);
        switch (task.getType()) {
            case "TODO":
                // Each concrete task supplies a fixed type; this guards the internal
                // type-to-subclass contract before persistence performs a downcast.
                assert task instanceof ToDo : "A TODO task must be a ToDo instance.";
                break;
            case "DEADLINE":
                // The type is produced by Deadline#getType(), so this downcast must
                // remain valid unless a future Task implementation breaks that contract.
                assert task instanceof Deadline
                        : "A DEADLINE task must be a Deadline instance.";
                Deadline deadline = (Deadline) task;
                json.put("by", deadline.getBy());
                break;
            case "EVENT":
                // See the equivalent DEADLINE assertion above.
                assert task instanceof Event : "An EVENT task must be an Event instance.";
                Event event = (Event) task;
                json.put("from", event.getFrom());
                json.put("to", event.getTo());
                break;
            default:
                throw new InvalidTaskType("Unknown task type!");
        }

        return json;
    }

    /**
     * Saves every task currently held by the supplied task storage as one JSON array.
     *
     * @param taskStorage storage whose tasks should be persisted.
     * @throws IOException if the file cannot be created or written.
     * @throws NullPointerException if {@code taskStorage} is null.
     */
    public void save(TaskStorage taskStorage) throws IOException {
        TaskStorage nonNullTaskStorage = Objects.requireNonNull(
                taskStorage, "Task storage cannot be null.");
        List<Task> tasks = nonNullTaskStorage.getTasks();

        ArrayNode jsonTasks = mapper.createArrayNode();

        for (Task task : tasks) {
            if (task == null) {
                throw new IllegalArgumentException("Task list cannot contain null tasks.");
            }
            jsonTasks.add(toJson(task));
        }

        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, mapper.writeValueAsString(jsonTasks));
    }

    /**
     * Loads all tasks from the JSON file directly into the supplied task storage.
     * Task IDs are reconstructed sequentially from the order of the saved
     * task entries; any persisted {@code id} fields are ignored.
     *
     * @param taskStorage storage to replace with the restored tasks.
     * @throws IOException if the file is malformed or cannot be read.
     * @throws NullPointerException if {@code taskStorage} is null.
     */
    public void load(TaskStorage taskStorage) throws IOException {
        TaskStorage nonNullTaskStorage = Objects.requireNonNull(
                taskStorage, "Task storage cannot be null.");
        if (Files.notExists(path)) {
            nonNullTaskStorage.replaceTasks(List.of());
            Task.resetIdAllocator(1);
            return;
        }

        String content = Files.readString(path);
        if (content.isBlank()) {
            nonNullTaskStorage.replaceTasks(List.of());
            Task.resetIdAllocator(1);
            return;
        }

        JsonNode jsonTasks = mapper.readTree(content);
        if (!jsonTasks.isArray()) {
            throw new IOException("Task file must contain a JSON array.");
        }

        List<Task> tasks = new ArrayList<>();
        long reconstructedId = 1;
        for (JsonNode json : jsonTasks) {
            if (reconstructedId > Integer.MAX_VALUE) {
                throw new IOException("The task file contains too many tasks.");
            }
            tasks.add(fromJson(json, (int) reconstructedId));
            reconstructedId++;
        }
        // IDs are allocated from one and incremented exactly once per loaded entry.
        // This protects the ID allocator's next-ID contract from future edits here.
        assert reconstructedId == (long) tasks.size() + 1
                : "The next task ID must follow the reconstructed task count.";
        Task.resetIdAllocator(reconstructedId);
        nonNullTaskStorage.replaceTasks(tasks);
    }

    /**
     * Reconstructs one task from its saved JSON representation.
     *
     * @param json saved task object.
     * @param reconstructedId runtime ID assigned according to file order.
     * @return reconstructed task.
     * @throws IOException if the object is malformed or contains invalid data.
     */
    private Task fromJson(JsonNode json, int reconstructedId) throws IOException {
        validateTaskJson(json);

        String type = json.get("type").asText();
        String description = json.get("description").asText();
        Task task = createTask(json, type, description, reconstructedId);
        restoreTaskState(task, json);
        return task;
    }

    /** Validates the fields shared by every saved task representation. */
    private void validateTaskJson(JsonNode json) throws IOException {
        if (json == null || !json.isObject()) {
            throw new IOException("Each saved task must be a JSON object.");
        }

        if (!json.hasNonNull("type") || !json.get("type").isTextual()
                || !json.hasNonNull("description") || !json.get("description").isTextual()) {
            throw new IOException("Task is missing its type or description.");
        }

        String type = json.get("type").asText();
        String description = json.get("description").asText();
        if (type.isBlank()) {
            throw new IOException("Task type cannot be blank.");
        }
        if (description.isBlank()) {
            throw new IOException("Task description cannot be blank.");
        }
        if (json.has("done") && !json.get("done").isBoolean()) {
            throw new IOException("Task done field must be a boolean.");
        }
    }

    /** Creates a task from its type-specific fields in a saved JSON object. */
    private Task createTask(JsonNode json, String type, String description,
                            int reconstructedId) throws IOException {
        switch (type) {
            case "TODO":
                try {
                    return new ToDo(description, reconstructedId);
                } catch (IllegalArgumentException e) {
                    throw new IOException("To-do contains invalid task data.", e);
                }
            case "DEADLINE":
                String deadlineValue = getTagValue(json, "by", "by");
                if (deadlineValue == null) {
                    throw new IOException("Deadline is missing its by field.");
                }
                try {
                    return new Deadline(description, deadlineValue, reconstructedId);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Deadline contains an invalid date-time.", e);
                }
            case "EVENT":
                String eventFrom = getTagValue(json, "from", "from");
                String eventTo = getTagValue(json, "to", "to");
                if (eventFrom == null || eventTo == null) {
                    throw new IOException("Event is missing its from or to field.");
                }
                try {
                    return new Event(description, eventFrom, eventTo, reconstructedId);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Event contains an invalid date-time.", e);
                }
            default:
                throw new IOException("Unknown task type: " + type);
        }
    }

    /** Restores generic tags and completion state from a saved JSON object. */
    private void restoreTaskState(Task task, JsonNode json) throws IOException {
        List<Tag> savedTags = readTags(json);
        if (!savedTags.isEmpty()) {
            try {
                task.updateTags(savedTags);
            } catch (IllegalArgumentException e) {
                throw new IOException("Task contains invalid tags.", e);
            }
        }

        if (json.path("done").asBoolean(false)) {
            task.mark();
        }
    }

    /**
     * Reads a tag value, falling back to the legacy top-level field.
     *
     * @param json saved task object.
     * @param tagName tag name in the tags object.
     * @param legacyField old top-level field name.
     * @return tag value, or null when neither representation contains it.
     * @throws IOException if the stored value is not textual.
     */
    private String getTagValue(JsonNode json, String tagName, String legacyField)
            throws IOException {
        JsonNode tags = json.get("tags");
        if (tags != null && !tags.isObject()) {
            throw new IOException("Task tags field must be an object.");
        }
        if (tags != null && tags.has(tagName)) {
            JsonNode value = tags.get(tagName);
            if (!value.isTextual()) {
                throw new IOException("Task tag values must be text.");
            }
            return value.asText();
        }
        if (!json.hasNonNull(legacyField)) {
            return null;
        }
        if (!json.get(legacyField).isTextual()) {
            throw new IOException("Task tag values must be text.");
        }
        return json.get(legacyField).asText();
    }

    /** Reads all tags from the optional generic tags object. */
    private List<Tag> readTags(JsonNode json) throws IOException {
        JsonNode tags = json.get("tags");
        if (tags == null) {
            return List.of();
        }
        if (!tags.isObject()) {
            throw new IOException("Task tags field must be an object.");
        }

        List<Tag> savedTags = new ArrayList<>();
        var fields = tags.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            if (!field.getValue().isTextual()) {
                throw new IOException("Task tag values must be text.");
            }
            try {
                savedTags.add(new Tag(field.getKey(), field.getValue().asText()));
            } catch (IllegalArgumentException e) {
                throw new IOException("Task contains an invalid tag.", e);
            }
        }
        return savedTags;
    }
}
