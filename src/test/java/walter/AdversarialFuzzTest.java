package walter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import walter.place.Place;
import walter.storage.Storage;
import walter.task.Task;

/**
 * Runs deterministic hostile command sequences against Walter and a small reference model.
 */
public class AdversarialFuzzTest {
    private static final long SEED = 2103_2026L;
    private static final int SEQUENCE_COUNT = 20;
    private static final int COMMANDS_PER_SEQUENCE = 250;
    private static final int MAX_TASKS = 100;
    private static final String UNKNOWN_COMMAND_HELP = "Unknown command. Try: todo DESCRIPTION; "
            + "deadline DESCRIPTION /by yyyy-MM-dd; event DESCRIPTION /at TIME; list; "
            + "find KEYWORD; mark INDEX; unmark INDEX; delete INDEX; on yyyy-MM-dd; or bye.";
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final String[] HOSTILE_INDEXES = {
        "", "0", "-0", "-1", "-2", "-999", "+1", "+0", "01", "001",
        "1", "2", "999999", "2147483647", "2147483648", "2147483649",
        "-2147483648", "-2147483649", "9223372036854775807", "1.0", "1.5",
        ".5", "1e3", "NaN", "Infinity", "abc", "1abc", "abc1", "--", "++",
        "1 2"
    };
    private static final String[] INVALID_COMMANDS = {
        "", " ", "\t", "LIST", "List", "todoabc x", "by", "/by", "/at",
        "unknown", "hello world", "list x", "places 1", "bye extra"
    };
    private static final String[] INVALID_STRUCTURED_COMMANDS = {
        "deadline task", "deadline /by 2026-01-01", "deadline task /by",
        "deadline task/by 2026-01-01", "deadline task /by2026-01-01",
        "deadline task /by 2026-01-01 extra", "deadline task /by 0000-01-01",
        "event meeting", "event /at 5pm", "event meeting /at",
        "event meeting /at a /at b", "event meeting /from a",
        "event meeting /to b", "event meeting /to b /from a",
        "event x /at a /from b /to c", "place home", "place /at address",
        "place home /at", "place a /at b /at c", "on", "on 2026-02-30"
    };

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void fixedSeedHostileSequences_outputsStateAndPersistenceMatchModel()
            throws DukeException {
        Random random = new Random(SEED);
        for (int sequence = 0; sequence < SEQUENCE_COUNT; sequence++) {
            Storage storage = new Storage(temporaryDirectory.resolve("sequence-" + sequence)
                    .resolve("walter.txt"));
            Walter walter = new Walter(storage);
            List<ModelTask> tasks = new ArrayList<>();
            List<ModelPlace> places = new ArrayList<>();

            for (int step = 0; step < COMMANDS_PER_SEQUENCE; step++) {
                FuzzCase fuzzCase = generateCase(random, sequence, step, tasks, places);
                String actual = walter.getResponse(fuzzCase.command());
                String context = failureContext(sequence, step, fuzzCase.command(),
                        fuzzCase.expectedResponse(), actual, tasks, places);
                assertEquals(fuzzCase.expectedResponse(), actual, context);
                assertPersistedState(storage, tasks, places, context);

                if ((step + 1) % 50 == 0) {
                    walter = new Walter(storage);
                    assertPersistedState(storage, tasks, places, context + " after restart");
                }
            }
        }
    }

    /** Generates one command and updates the reference model only for a valid mutation. */
    private FuzzCase generateCase(Random random, int sequence, int step,
            List<ModelTask> tasks, List<ModelPlace> places) {
        int operation = random.nextInt(16);
        return switch (operation) {
            case 0 -> addTodo(sequence, step, tasks);
            case 1 -> addDeadline(sequence, step, tasks);
            case 2 -> addAtEvent(sequence, step, tasks);
            case 3 -> addRangeEvent(sequence, step, tasks);
            case 4 -> changeState(random, "mark", tasks, true);
            case 5 -> changeState(random, "done", tasks, true);
            case 6 -> changeState(random, "unmark", tasks, false);
            case 7 -> deleteTask(random, tasks);
            case 8 -> findTasks(random, tasks);
            case 9 -> queryDate(random, tasks);
            case 10 -> addPlace(sequence, step, places);
            case 11 -> deletePlace(random, places);
            case 12 -> new FuzzCase("list", renderTaskList(tasks));
            case 13 -> new FuzzCase("places", renderPlaceList(places));
            case 14 -> invalidRawCommand(random);
            default -> invalidStructuredCommand(random);
        };
    }

    private FuzzCase addTodo(int sequence, int step, List<ModelTask> tasks) {
        String description = "todo-" + sequence + "-" + step + " Ω 🚀 /by /at";
        return addTask("todo " + description, ModelTask.todo(description), tasks);
    }

    private FuzzCase addDeadline(int sequence, int step, List<ModelTask> tasks) {
        String description = "deadline-" + sequence + "-" + step;
        LocalDate date = LocalDate.of(2026, 1 + step % 12, 1 + step % 28);
        return addTask("deadline " + description + " /by " + date,
                ModelTask.deadline(description, date), tasks);
    }

    private FuzzCase addAtEvent(int sequence, int step, List<ModelTask> tasks) {
        String description = "event-at-" + sequence + "-" + step;
        String time = "Monday 2:30 PM 中文";
        return addTask("event " + description + " /at " + time,
                ModelTask.atEvent(description, time), tasks);
    }

    private FuzzCase addRangeEvent(int sequence, int step, List<ModelTask> tasks) {
        String description = "event-range-" + sequence + "-" + step;
        String start = "1pm Ω";
        String end = "2pm 🚀";
        return addTask("event " + description + " /from " + start + " /to " + end,
                ModelTask.rangeEvent(description, start, end), tasks);
    }

    private FuzzCase addTask(String command, ModelTask task, List<ModelTask> tasks) {
        if (tasks.size() >= MAX_TASKS) {
            return new FuzzCase(command, error("Task list is full."));
        }
        tasks.add(task);
        return new FuzzCase(command, "Good. That's on the list now:\n"
                + task.render() + "\nThe list now contains " + tasks.size() + " "
                + (tasks.size() == 1 ? "task." : "tasks."));
    }

    private FuzzCase changeState(Random random, String commandWord,
            List<ModelTask> tasks, boolean targetDone) {
        String indexText = chooseIndex(random, tasks.size());
        String command = indexText.isEmpty() ? commandWord : commandWord + " " + indexText;
        IndexResult result = parseIndex(indexText, tasks.size(), "Task");
        if (result.error() != null) {
            return new FuzzCase(command, error(result.error()));
        }

        ModelTask task = tasks.get(result.index());
        if (task.done == targetDone) {
            String message = targetDone
                    ? "Task is already marked as done."
                    : "Task is already not done.";
            return new FuzzCase(command, error(message));
        }
        task.done = targetDone;
        String response = targetDone
                ? "Done. Consider this one handled:\n" + task.render()
                : "Understood. This goes back into the mix:\n" + task.render();
        return new FuzzCase(command, response);
    }

    private FuzzCase deleteTask(Random random, List<ModelTask> tasks) {
        String indexText = chooseIndex(random, tasks.size());
        String command = indexText.isEmpty() ? "delete" : "delete " + indexText;
        IndexResult result = parseIndex(indexText, tasks.size(), "Task");
        if (result.error() != null) {
            return new FuzzCase(command, error(result.error()));
        }
        ModelTask deleted = tasks.remove(result.index());
        return new FuzzCase(command, "Done. I've removed this from the list:\n"
                + deleted.render() + "\nThe list now contains " + tasks.size() + " "
                + (tasks.size() == 1 ? "task." : "tasks."));
    }

    private FuzzCase findTasks(Random random, List<ModelTask> tasks) {
        String keyword = tasks.isEmpty() || random.nextBoolean()
                ? "absent-关键词"
                : tasks.get(random.nextInt(tasks.size())).description.substring(0, 4)
                        .toUpperCase(Locale.ROOT);
        List<ModelTask> matches = tasks.stream()
                .filter(task -> task.description.toLowerCase(Locale.ROOT)
                        .contains(keyword.toLowerCase(Locale.ROOT)))
                .toList();
        if (matches.isEmpty()) {
            return new FuzzCase("find " + keyword,
                    "I found no tasks matching that keyword.");
        }
        return new FuzzCase("find " + keyword,
                "Here's what I found:\n" + numberedFindResults(tasks, keyword));
    }

    private FuzzCase queryDate(Random random, List<ModelTask> tasks) {
        LocalDate date = LocalDate.of(2026, 1 + random.nextInt(12), 1 + random.nextInt(28));
        List<ModelTask> matches = tasks.stream()
                .filter(task -> date.equals(task.date))
                .toList();
        String displayDate = date.format(DISPLAY_DATE);
        if (matches.isEmpty()) {
            return new FuzzCase("on " + date,
                    "No deadlines are scheduled for " + displayDate + ".");
        }
        return new FuzzCase("on " + date,
                "Here is the schedule for " + displayDate + ":\n" + numberedTasks(matches));
    }

    private FuzzCase addPlace(int sequence, int step, List<ModelPlace> places) {
        ModelPlace place = new ModelPlace(
                "place-" + sequence + "-" + step + " 中文",
                "address /by /from 🚀 " + step);
        places.add(place);
        return new FuzzCase("place " + place.name + " /at " + place.address,
                "Good. I've recorded this location:\n" + place.render()
                + "\nThe location list now contains " + places.size() + " "
                + (places.size() == 1 ? "place." : "places."));
    }

    private FuzzCase deletePlace(Random random, List<ModelPlace> places) {
        String indexText = chooseIndex(random, places.size());
        String command = indexText.isEmpty() ? "deleteplace" : "deleteplace " + indexText;
        IndexResult result = parseIndex(indexText, places.size(), "Place");
        if (result.error() != null) {
            return new FuzzCase(command, error(result.error()));
        }
        ModelPlace deleted = places.remove(result.index());
        return new FuzzCase(command, "Done. I've removed this location:\n"
                + deleted.render() + "\nThe location list now contains " + places.size() + " "
                + (places.size() == 1 ? "place." : "places."));
    }

    private FuzzCase invalidRawCommand(Random random) {
        String command = INVALID_COMMANDS[random.nextInt(INVALID_COMMANDS.length)];
        String normalized = command.strip();
        String message;
        if (normalized.isBlank()) {
            message = "Command cannot be blank.";
        } else if (normalized.equals("list x")) {
            message = "list does not take arguments.";
        } else if (normalized.equals("places 1")) {
            message = "places does not take arguments.";
        } else if (normalized.equals("bye extra")) {
            message = "bye does not take arguments.";
        } else {
            message = UNKNOWN_COMMAND_HELP;
        }
        return new FuzzCase(command, error(message));
    }

    private FuzzCase invalidStructuredCommand(Random random) {
        String command = INVALID_STRUCTURED_COMMANDS[
                random.nextInt(INVALID_STRUCTURED_COMMANDS.length)];
        String response = switch (command) {
            case "deadline task" -> error("Deadline requires /by.");
            case "deadline /by 2026-01-01" -> error("Deadline description cannot be empty.");
            case "deadline task /by" -> error("Deadline date/time cannot be empty.");
            case "deadline task/by 2026-01-01", "deadline task /by2026-01-01" ->
                error("Deadline requires /by.");
            case "deadline task /by 2026-01-01 extra",
                    "deadline task /by 0000-01-01" ->
                error("Deadline date must be in yyyy-MM-dd format.");
            case "event meeting" -> error("Event requires /at or /from and /to.");
            case "event /at 5pm" -> error("Event description cannot be empty.");
            case "event meeting /at" -> error("Event date/time cannot be empty.");
            case "event meeting /at a /at b" ->
                error("Event must use either one /at or one /from and one /to.");
            case "event meeting /from a" ->
                error("Event requires /to command when given /from command.");
            case "event meeting /to b" ->
                error("Event requires /from command when given /to command.");
            case "event meeting /to b /from a" ->
                error("Event /from must appear before /to.");
            case "event x /at a /from b /to c" ->
                error("Event must use either one /at or one /from and one /to.");
            case "place home" -> error("Place requires exactly one /at.");
            case "place /at address" -> error("Place name cannot be empty.");
            case "place home /at" -> error("Place address cannot be empty.");
            case "place a /at b /at c" -> error("Place requires exactly one /at.");
            case "on" -> error("Date is required for the on command.");
            case "on 2026-02-30" -> error("Date is not a valid calendar date.");
            default -> throw new AssertionError("Unhandled fuzz command: " + command);
        };
        return new FuzzCase(command, response);
    }

    private String chooseIndex(Random random, int size) {
        if (size > 0 && random.nextBoolean()) {
            return Integer.toString(1 + random.nextInt(size));
        }
        return HOSTILE_INDEXES[random.nextInt(HOSTILE_INDEXES.length)];
    }

    private IndexResult parseIndex(String text, int size, String entity) {
        if (text.isEmpty()) {
            return new IndexResult(-1, entity + " number is required.");
        }
        int oneBased;
        try {
            oneBased = Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            return new IndexResult(-1, entity + " number must be an integer.");
        }
        int index = oneBased - 1;
        if (index < 0 || index >= size) {
            return new IndexResult(-1, entity + " number is out of range.");
        }
        return new IndexResult(index, null);
    }

    private void assertPersistedState(Storage storage, List<ModelTask> tasks,
            List<ModelPlace> places, String context) throws DukeException {
        List<Task> storedTasks = storage.load();
        List<Place> storedPlaces = storage.loadPlaces();
        assertEquals(tasks.stream().map(ModelTask::render).toList(),
                storedTasks.stream().map(Task::toString).toList(), context + " task storage");
        assertEquals(places.stream().map(ModelPlace::render).toList(),
                storedPlaces.stream().map(Place::toString).toList(), context + " place storage");
    }

    private static String renderTaskList(List<ModelTask> tasks) {
        return tasks.isEmpty()
                ? "The board is clean. Nothing needs doing."
                : "Here are your tasks:\n" + numberedTasks(tasks);
    }

    private static String renderPlaceList(List<ModelPlace> places) {
        if (places.isEmpty()) {
            return "The location list is empty.";
        }
        StringBuilder result = new StringBuilder("Here are the recorded locations:");
        for (int i = 0; i < places.size(); i++) {
            result.append('\n').append(i + 1).append(". ").append(places.get(i).render());
        }
        return result.toString();
    }

    private static String numberedTasks(List<ModelTask> tasks) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                result.append('\n');
            }
            result.append(i + 1).append(". ").append(tasks.get(i).render());
        }
        return result.toString();
    }

    private static String numberedFindResults(List<ModelTask> tasks, String keyword) {
        StringBuilder result = new StringBuilder();
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        for (int i = 0; i < tasks.size(); i++) {
            if (!tasks.get(i).description.toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                continue;
            }
            if (!result.isEmpty()) {
                result.append('\n');
            }
            result.append(i + 1).append(". ").append(tasks.get(i).render());
        }
        return result.toString();
    }

    private static String error(String message) {
        return message + " Stay sharp, Jesse.";
    }

    private static String failureContext(int sequence, int step, String command,
            String expected, String actual, List<ModelTask> tasks, List<ModelPlace> places) {
        return "seed=" + SEED + ", sequence=" + sequence + ", step=" + step
                + ", command=" + command + "\nexpected=" + expected + "\nactual=" + actual
                + "\ntasks=" + tasks.stream().map(ModelTask::render).toList()
                + "\nplaces=" + places.stream().map(ModelPlace::render).toList();
    }

    private record FuzzCase(String command, String expectedResponse) {
    }

    private record IndexResult(int index, String error) {
    }

    private static class ModelTask {
        private final String type;
        private final String description;
        private final String details;
        private final LocalDate date;
        private boolean done;

        private ModelTask(String type, String description, String details, LocalDate date) {
            this.type = type;
            this.description = description;
            this.details = details;
            this.date = date;
        }

        static ModelTask todo(String description) {
            return new ModelTask("T", description, "", null);
        }

        static ModelTask deadline(String description, LocalDate date) {
            return new ModelTask("D", description,
                    " (by: " + date.format(DISPLAY_DATE) + ")", date);
        }

        static ModelTask atEvent(String description, String time) {
            return new ModelTask("E", description, " (at: " + time + ")", null);
        }

        static ModelTask rangeEvent(String description, String start, String end) {
            return new ModelTask("E", description,
                    " (from: " + start + " to: " + end + ")", null);
        }

        String render() {
            return "[" + type + "][" + (done ? "X" : " ") + "] " + description + details;
        }
    }

    private record ModelPlace(String name, String address) {
        String render() {
            return name + " — " + address;
        }
    }
}
