package stewie.storage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import stewie.model.Deadline;
import stewie.model.Event;
import stewie.model.Task;
import stewie.model.ToDo;

/**
 * Encodes and validates Stewie's UTF-8 task records independently of filesystem operations.
 */
final class TaskCodec {
    private TaskCodec() {
    }

    /**
     * Encodes all task records using the same UTF-8 format as the loader.
     */
    static byte[] encode(List<Task> tasks) {
        String content = tasks.stream().map(TaskCodec::serializeTask).collect(Collectors.joining("\n"));
        return content.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Decodes UTF-8 strictly and retains empty records so corruption remains visible.
     */
    static String[] decodeLines(byte[] content) throws IOException {
        String decoded = StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(content)).toString();
        return decoded.split("\\R", -1);
    }

    /**
     * Validates the entire storage record, including its completion status and exact field count.
     */
    static Task parse(String line) {
        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 3 || !(parts[1].equals("0") || parts[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid task status or field count");
        }
        Task task;
        if (parts[0].equals("T") && parts.length == 3) {
            task = new ToDo(parts[2]);
        } else if (parts[0].equals("D") && parts.length == 4) {
            task = new Deadline(parts[2], parts[3]);
        } else if (parts[0].equals("E") && parts.length == 5) {
            task = new Event(parts[2], parts[3], parts[4]);
        } else {
            throw new IllegalArgumentException("Invalid task type or field count");
        }
        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Converts a validated task to the pipe-separated format used by the loader.
     */
    private static String serializeTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return String.format("D | %s | %s | %s", status, task.getDescription(), deadline.getDeadline());
        }
        if (task instanceof Event event) {
            return String.format("E | %s | %s | %s | %s", status, task.getDescription(),
                    event.getFrom(), event.getTo());
        }
        return String.format("T | %s | %s", status, task.getDescription());
    }
}
