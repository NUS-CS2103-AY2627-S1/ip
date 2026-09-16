package eli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Runs the Eli task-list application and responds to user commands. */
public class Eli {
    private static final String DIVIDER = "____________________________________________________________";
    private static final Path DEFAULT_DATA_FILE = Path.of("eli.txt");
    private static final Path LEGACY_DATA_FILE = Path.of("duke.txt");

    private final TaskList tasks;
    private final boolean isStorageEnabled;
    private final Path dataFile;
    private final Path legacyDataFile;
    private boolean isStorageUsable = true;
    private String storageWarning;

    /** Creates Eli and loads any tasks saved previously. */
    public Eli() {
        this(new TaskList(), DEFAULT_DATA_FILE, LEGACY_DATA_FILE, true);
        loadTasks();
    }

    /** Creates Eli with an existing task list, primarily for isolated testing. */
    Eli(TaskList tasks) {
        this(tasks, DEFAULT_DATA_FILE, null, false);
    }

    /** Creates Eli with storage at a test-controlled path. */
    Eli(Path dataFile) {
        this(new TaskList(), dataFile, null, true);
        loadTasks();
    }

    /** Creates Eli with current and legacy storage paths for migration testing. */
    Eli(Path dataFile, Path legacyDataFile) {
        this(new TaskList(), dataFile, legacyDataFile, true);
        loadTasks();
    }

    /** Creates Eli with explicit task and storage dependencies. */
    private Eli(TaskList tasks, Path dataFile, Path legacyDataFile, boolean isStorageEnabled) {
        assert tasks != null : "Task list must not be null";
        assert dataFile != null : "Data file path must not be null";
        this.tasks = tasks;
        this.dataFile = dataFile;
        this.legacyDataFile = legacyDataFile;
        this.isStorageEnabled = isStorageEnabled;
    }

    /** Runs Eli using the original command-line interface. */
    public static void main(String[] args) {
        Eli eli = new Eli();
        Scanner input = new Scanner(System.in);

        System.out.println(eli.getWelcomeMessage());
        while (input.hasNextLine()) {
            String command = input.nextLine();
            System.out.println(DIVIDER);
            System.out.println(eli.getResponse(command));
            System.out.println(DIVIDER);
            if (eli.isExitCommand(command)) {
                break;
            }
        }
    }

    /**
     * Executes one command and returns Eli's response for either UI.
     *
     * @param input command entered by the user
     * @return Eli's response to the command
     */
    public String getResponse(String input) {
        String response = getCommandResponse(input);
        if (storageWarning == null) {
            return response;
        }

        String responseWithWarning = storageWarning
                + System.lineSeparator() + System.lineSeparator() + response;
        storageWarning = null;
        return responseWithWarning;
    }

    /** Executes one command after guarding against missing input. */
    private String getCommandResponse(String input) {
        if (input == null || input.isBlank()) {
            return getErrorMessage(
                    "Please enter a command.",
                    "请输入指令。");
        }

        String command = input.trim();
        try {
            if (isExitCommand(command)) {
                return getBilingualMessage(
                        "Bye for now!",
                        "再见，记得回来找我！");
            } else if (command.equals("list")) {
                return getTaskListResponse();
            } else if (command.equals("sort")) {
                return getSortResponse();
            } else if (command.equals("find")) {
                throw new EliException(getErrorMessage(
                        "Please enter a keyword after find.",
                        "请在 find 后输入关键词。"));
            } else if (command.startsWith("find ")) {
                return getFindResponse(command.substring(5).trim());
            } else if (command.equals("todo")) {
                throw new EliException(getErrorMessage(
                        "A todo needs a description.",
                        "请填写待办事项。"));
            } else if (command.startsWith("todo ")) {
                return addTodo(command.substring(5).trim());
            } else if (command.equals("deadline")) {
                throw new EliException(getErrorMessage(
                        "A deadline needs a description.",
                        "请填写截止事项。"));
            } else if (command.startsWith("deadline ")) {
                return addDeadline(command);
            } else if (command.equals("event")) {
                throw new EliException(getErrorMessage(
                        "An event needs a description.",
                        "请填写活动名称。"));
            } else if (command.startsWith("event ")) {
                return addEvent(command);
            } else if (command.equals("mark")) {
                throw new EliException(getMissingTaskNumberMessage("mark"));
            } else if (command.startsWith("mark ")) {
                return updateTaskStatus(command.substring(5), true);
            } else if (command.equals("unmark")) {
                throw new EliException(getMissingTaskNumberMessage("unmark"));
            } else if (command.startsWith("unmark ")) {
                return updateTaskStatus(command.substring(7), false);
            } else if (command.equals("delete")) {
                throw new EliException(getMissingTaskNumberMessage("delete"));
            } else if (command.startsWith("delete ")) {
                return deleteTask(command.substring(7));
            } else {
                throw new EliException(getErrorMessage(
                        "I don't recognize that command yet.",
                        "我暂时不认识这个指令。"));
            }
        } catch (EliException exception) {
            return exception.getMessage();
        }
    }

    /** Returns whether the command asks Eli to exit. */
    public boolean isExitCommand(String input) {
        String command = input.trim();
        return command.equals("bye") || command.equals("再见");
    }

    /** Returns the welcome text used by the command-line interface. */
    private String getWelcomeMessage() {
        String banner = " _______     _           _____ \n"
                + "|  _____|   | |         |_   _|\n"
                + "| |___      | |           | |  \n"
                + "|  ___|     | |           | |  \n"
                + "| |_____    | |_____     _| |_ \n"
                + "|_______|   |_______|   |_____|\n";
        return banner + DIVIDER + "\n"
                + getBilingualMessage(
                        "Hello! I'm Eli, your bilingual task buddy.",
                        "你好！准备好一起完成任务了吗？")
                + "\n" + DIVIDER;
    }

    /** Returns all currently stored tasks. */
    private String getTaskListResponse() {
        String taskLines = IntStream.range(0, tasks.size())
                .mapToObj(index -> (index + 1) + "." + tasks.get(index))
                .collect(Collectors.joining(System.lineSeparator()));
        return taskLines.isEmpty()
                ? getBilingualMessage(
                        "Your task list is clear!",
                        "任务清单空空如也！")
                : getBilingualMessage(
                        "Let's see what's on your list!",
                        "来看看你的任务清单吧：")
                        + System.lineSeparator() + taskLines;
    }

    /** Returns tasks whose descriptions contain the keyword. */
    private String getFindResponse(String keyword) throws EliException {
        if (keyword.isEmpty()) {
            throw new EliException(getErrorMessage(
                    "Please enter a keyword after find.",
                    "请在 find 后输入关键词。"));
        }

        String searchTerm = keyword.toLowerCase();
        String matches = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription().toLowerCase().contains(searchTerm))
                .mapToObj(index -> (index + 1) + "." + tasks.get(index))
                .collect(Collectors.joining(System.lineSeparator()));
        return matches.isEmpty()
                ? getBilingualMessage(
                        "No matching tasks found.",
                        "没有找到相关任务。")
                : getBilingualMessage(
                        "I found these tasks!",
                        "找到这些任务啦：")
                        + System.lineSeparator() + matches;
    }

    /** Sorts and saves the task list, then returns it grouped by task type. */
    private String getSortResponse() throws EliException {
        if (tasks.isEmpty()) {
            return getBilingualMessage(
                    "There are no tasks to sort yet.",
                    "暂时没有任务可以排序。");
        }

        TaskList originalOrder = new TaskList();
        originalOrder.addAll(tasks);
        tasks.sortTasks();
        try {
            saveTasks();
        } catch (EliException exception) {
            tasks.clear();
            tasks.addAll(originalOrder);
            throw exception;
        }
        String sections = Arrays.stream(TaskType.values())
                .sorted(Comparator.comparingInt(TaskType::getSortOrder))
                .map(this::formatTaskSection)
                .filter(section -> !section.isEmpty())
                .collect(Collectors.joining(System.lineSeparator()));
        return getBilingualMessage(
                "All sorted and ready!",
                "任务已经排好啦：")
                + System.lineSeparator() + sections;
    }

    /** Formats one non-empty task-type section using the list's current numbers. */
    private String formatTaskSection(TaskType taskType) {
        String taskLines = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getTaskType() == taskType)
                .mapToObj(index -> (index + 1) + "." + tasks.get(index))
                .collect(Collectors.joining(System.lineSeparator()));
        return taskLines.isEmpty()
                ? ""
                : taskType.getSectionName() + ":" + System.lineSeparator() + taskLines;
    }

    /** Adds a to-do task and returns a confirmation. */
    private String addTodo(String description) throws EliException {
        if (description.isEmpty()) {
            throw new EliException(getErrorMessage(
                    "A todo needs a description.",
                    "请填写待办事项。"));
        }
        return addTask(new Todo(description));
    }

    /** Adds a deadline task and returns a confirmation. */
    private String addDeadline(String command) throws EliException {
        int byIndex = command.indexOf(" /by ");
        if (byIndex == -1) {
            throw new EliException(getErrorMessage(
                    "A deadline needs a /by value.",
                    "请使用 /by 填写截止时间。"));
        } else if (byIndex != command.lastIndexOf(" /by ")) {
            throw new EliException(getErrorMessage(
                    "Use /by only once in a deadline.",
                    "每个截止事项只能使用一次 /by。"));
        } else if (byIndex <= 9) {
            throw new EliException(getErrorMessage(
                    "A deadline needs a description.",
                    "请填写截止事项。"));
        }

        String description = command.substring(9, byIndex).trim();
        String by = command.substring(byIndex + 5).trim();
        if (description.isEmpty()) {
            throw new EliException(getErrorMessage(
                    "A deadline needs a description.",
                    "请填写截止事项。"));
        } else if (by.isEmpty()) {
            throw new EliException(getErrorMessage(
                    "A deadline needs a /by value.",
                    "请使用 /by 填写截止时间。"));
        } else if (TaskDateTimeFormatter.isInvalidDateOrDateTime(by)) {
            throw new EliException(getErrorMessage(
                    "That deadline date is invalid. Use yyyy-MM-dd or yyyy-MM-dd HHmm.",
                    "截止日期无效。请使用 yyyy-MM-dd 或 yyyy-MM-dd HHmm。"));
        }
        return addTask(new Deadline(description, by));
    }

    /** Adds an event task and returns a confirmation. */
    private String addEvent(String command) throws EliException {
        int fromIndex = command.indexOf(" /from ");
        int toIndex = command.indexOf(" /to ");
        if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
            throw new EliException(getErrorMessage(
                    "An event needs /from and /to values.",
                    "请使用 /from 和 /to 填写活动时间。"));
        } else if (fromIndex != command.lastIndexOf(" /from ")
                || toIndex != command.lastIndexOf(" /to ")) {
            throw new EliException(getErrorMessage(
                    "Use /from and /to only once in an event.",
                    "每个活动只能使用一次 /from 和 /to。"));
        } else if (fromIndex <= 6) {
            throw new EliException(getErrorMessage(
                    "An event needs a description.",
                    "请填写活动名称。"));
        } else if (toIndex < fromIndex + 7) {
            throw new EliException(getErrorMessage(
                    "An event needs /from and /to values.",
                    "请使用 /from 和 /to 填写活动时间。"));
        }

        String description = command.substring(6, fromIndex).trim();
        String from = command.substring(fromIndex + 7, toIndex).trim();
        String to = command.substring(toIndex + 5).trim();
        if (description.isEmpty()) {
            throw new EliException(getErrorMessage(
                    "An event needs a description.",
                    "请填写活动名称。"));
        } else if (from.isEmpty() || to.isEmpty()) {
            throw new EliException(getErrorMessage(
                    "An event needs /from and /to values.",
                    "请使用 /from 和 /to 填写活动时间。"));
        } else if (TaskDateTimeFormatter.isInvalidDateTime(from)
                || TaskDateTimeFormatter.isInvalidDateTime(to)) {
            throw new EliException(getErrorMessage(
                    "An event date is invalid. Use yyyy-MM-dd HHmm.",
                    "活动日期无效。请使用 yyyy-MM-dd HHmm。"));
        }

        validateEventOrder(from, to);
        return addTask(new Event(description, from, to));
    }

    /** Adds and saves one task. */
    private String addTask(Task task) throws EliException {
        if (tasks.containsTaskWithSameDetails(task)) {
            throw new EliException(getErrorMessage(
                    "That exact task is already in your list.",
                    "清单中已经有完全相同的任务。"));
        }

        tasks.addTask(task);
        try {
            saveTasks();
        } catch (EliException exception) {
            tasks.remove(task);
            throw exception;
        }
        String english = "Task captured!\n  " + task
                + "\nYou now have " + tasks.size() + " tasks.";
        String chinese = "任务记下来啦：\n  " + task
                + "\n你现在有 " + tasks.size() + " 个任务。";
        return getBilingualMessage(english, chinese);
    }

    /** Marks or unmarks a task and returns a confirmation. */
    private String updateTaskStatus(String numberText, boolean isDone) throws EliException {
        int taskNumber = parseTaskNumber(numberText);
        if (!tasks.hasTaskNumber(taskNumber)) {
            throw new EliException(getErrorMessage(
                    "There is no task with that number.",
                    "没有这个编号的任务。"));
        }

        Task task = tasks.getTask(taskNumber);
        boolean wasDone = "X".equals(task.getStatusIcon());
        if (isDone) {
            task.markAsDone();
            saveTasksOrRestoreStatus(task, wasDone);
            return getBilingualMessage(
                    "Nice work!\nThis task is now complete:\n  " + task,
                    "做得好！\n这个任务已经完成：\n  " + task);
        }
        task.markAsNotDone();
        saveTasksOrRestoreStatus(task, wasDone);
        return getBilingualMessage(
                "No worries!\nThis task is back on your list:\n  " + task,
                "没关系！\n这个任务已回到清单：\n  " + task);
    }

    /** Deletes a task and returns a confirmation. */
    private String deleteTask(String numberText) throws EliException {
        int taskNumber = parseTaskNumber(numberText);
        if (!tasks.hasTaskNumber(taskNumber)) {
            throw new EliException(getErrorMessage(
                    "There is no task with that number.",
                    "没有这个编号的任务。"));
        }

        Task removedTask = tasks.removeTask(taskNumber);
        try {
            saveTasks();
        } catch (EliException exception) {
            tasks.add(taskNumber - 1, removedTask);
            throw exception;
        }
        String english = "Task cleared!\n  " + removedTask
                + "\nYou now have " + tasks.size() + " tasks.";
        String chinese = "已删除这个任务：\n  " + removedTask
                + "\n你现在有 " + tasks.size() + " 个任务。";
        return getBilingualMessage(english, chinese);
    }

    /** Converts task-number text to a number, or returns -1 when it is invalid. */
    private int parseTaskNumber(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    /** Loads saved tasks, falling back to the pre-Eli filename when necessary. */
    private void loadTasks() {
        Path sourceFile = getLoadSourceFile();
        try (FileInputStream fileInput = new FileInputStream(sourceFile.toFile());
                ObjectInputStream input = new ObjectInputStream(fileInput)) {
            tasks.addAll((TaskList) input.readObject());
        } catch (FileNotFoundException exception) {
            // It is normal for the data file not to exist on the first run.
        } catch (IOException | ClassNotFoundException | ClassCastException | SecurityException exception) {
            isStorageUsable = false;
            storageWarning = getErrorMessage(
                    "I could not read " + getDataFileName(sourceFile)
                            + ", so I started with an empty list."
                            + " Fix or remove that file before saving new tasks.",
                    "无法读取 " + getDataFileName(sourceFile) + "，因此我从空清单开始。"
                            + "请修复或移除该文件后再保存新任务。");
        }
    }

    /** Uses legacy storage only when the current Eli data file does not exist. */
    private Path getLoadSourceFile() {
        if (legacyDataFile != null && Files.notExists(dataFile) && Files.exists(legacyDataFile)) {
            return legacyDataFile;
        }
        return dataFile;
    }

    /** Saves the current task list to disk. */
    private void saveTasks() throws EliException {
        if (!isStorageEnabled) {
            return;
        }
        if (!isStorageUsable) {
            throw new EliException(getErrorMessage(
                    "I cannot save until " + getDataFileName() + " is fixed or removed.",
                    "修复或移除 " + getDataFileName() + " 后才能保存任务。"));
        }

        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(dataFile.toFile()))) {
            output.writeObject(tasks);
        } catch (IOException | SecurityException exception) {
            throw new EliException(getErrorMessage(
                    "I could not save your tasks.",
                    "任务保存失败。"));
        }
    }

    /** Returns the user-facing name of the configured storage file. */
    private String getDataFileName() {
        return getDataFileName(dataFile);
    }

    /** Returns the user-facing name of a storage file. */
    private String getDataFileName(Path file) {
        return file.getFileName().toString();
    }

    /** Saves a status change, restoring the previous status if persistence fails. */
    private void saveTasksOrRestoreStatus(Task task, boolean wasDone) throws EliException {
        try {
            saveTasks();
        } catch (EliException exception) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw exception;
        }
    }

    /** Rejects an event whose supported end date-time is not after its start. */
    private void validateEventOrder(String from, String to) throws EliException {
        Optional<LocalDateTime> start = TaskDateTimeFormatter.parseInputDateTime(from);
        Optional<LocalDateTime> end = TaskDateTimeFormatter.parseInputDateTime(to);
        if (start.isPresent() && end.isPresent() && !start.get().isBefore(end.get())) {
            throw new EliException(getErrorMessage(
                    "An event must end after it starts.",
                    "活动结束时间必须晚于开始时间。"));
        }
    }

    /** Builds the error shown when a task-number command has no number. */
    private String getMissingTaskNumberMessage(String command) {
        return getErrorMessage(
                "Please enter a task number after " + command + ".",
                "请在 " + command + " 后输入任务编号。");
    }

    /** Builds a consistent bilingual error response in Eli's friendly voice. */
    private static String getErrorMessage(String english, String chinese) {
        return getBilingualMessage(
                "Oops!" + System.lineSeparator() + english,
                "哎呀！" + System.lineSeparator() + chinese);
    }

    /** Places the complete English response before a separate Chinese response. */
    private static String getBilingualMessage(String english, String chinese) {
        return english + System.lineSeparator() + System.lineSeparator() + chinese;
    }
}
