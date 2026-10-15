package org.cs2103t.marquee.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cs2103t.marquee.cli.command.Code;
import org.cs2103t.marquee.cli.command.Codes;
import org.cs2103t.marquee.cli.command.Command;
import org.cs2103t.marquee.cli.command.CommandFormatter;
import org.cs2103t.marquee.cli.task.DeadlineTask;
import org.cs2103t.marquee.cli.task.EventTask;
import org.cs2103t.marquee.cli.task.TodoTask;
import org.cs2103t.marquee.core.DuplicateKeyException;
import org.cs2103t.marquee.core.Marquee;
import org.cs2103t.marquee.core.io.FileParseException;
import org.cs2103t.marquee.core.task.Task;
import org.cs2103t.marquee.core.task.TaskTag;
import org.cs2103t.marquee.core.time.DateTimeFormatter;

/**
 * Entry point for the CLI application.
 */
public class Application {
    private static Map<String, Class<? extends Task>> supportedTasks;
    private static BufferedReader inputReader;
    private static CommandFormatter commandFormatter;
    private static Dialogues dialogues;
    private static Marquee marquee;
    private static Path currentFile;

    private int[] parseIntArray(String input) throws NumberFormatException {
        return Arrays.stream(input.split("\\s+", -1))
                .mapToInt(str -> {
                    try {
                        return Integer.parseInt(str) - 1;
                    } catch (NumberFormatException _) {
                        throw new NumberFormatException(str);
                    }
                })
                .toArray();
    }

    private TaskTag[] parseTagArray(String input) {
        return Arrays.stream(input.split("\\s+", -1))
                .map(TaskTag::getTag)
                .filter(Objects::nonNull)
                .toArray(TaskTag[]::new);
    }

    private Path getLocalStoragePath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return Paths.get(System.getenv("LOCALAPPDATA"));
        } else if (os.contains("mac")) {
            return Paths.get(System.getProperty("user.home")).resolve("Library");
        } else {
            String dataHome = System.getenv("XDG_DATA_HOME");
            String userHome = System.getProperty("user.home");
            return Paths.get(dataHome != null && !dataHome.isEmpty() ? dataHome : userHome);
        }
    }

    private void load() {
        try {
            if (marquee.load(currentFile)) {
                dialogues.successLoad();
            } else {
                dialogues.warningLoadIncomplete();
            }
        } catch (NoSuchFileException _) {
            dialogues.warningFileNotFound();
        } catch (IOException e) {
            dialogues.errorSaveUnavailable(e.getMessage());
        } catch (FileParseException e) {
            dialogues.errorSaveWrongFormat();
        } catch (IllegalArgumentException e) {
            dialogues.errorSaveCorrupted();
        }
    }

    private void save() {
        try {
            if (marquee.save(currentFile)) {
                dialogues.successSave();
            } else {
                dialogues.warningSaveIncomplete();
            }
        } catch (IOException e) {
            dialogues.errorSaveUnavailable(e.getMessage());
        }
    }

    private List<Task> find(Command command) {
        return marquee.find(
                command.getArgument(),
                command.hasFlag("from")
                        ? DateTimeFormatter.parseDateTime(command.getFlag("from"))
                        : null,
                command.hasFlag("to")
                        ? DateTimeFormatter.parseDateTime(command.getFlag("to"))
                        : null,
                command.hasFlag("completed") != command.hasFlag("incomplete")
                        ? command.hasFlag("completed")
                        : null,
                command.hasFlag("tags")
                        ? Set.of(parseTagArray(command.getFlag("tags")))
                        : null,
                command.hasFlag("chain")
        );
    }

    private Function<Task, Task> modifyFactory(Command command) {
        return task -> {
            try {
                if (command.hasFlag("from")) {
                    task.setStart(DateTimeFormatter.parseDateTime(command.getFlag("from")));
                }
                if (command.hasFlag("to")) {
                    task.setEnd(DateTimeFormatter.parseDateTime(command.getFlag("to")));
                }
                if (command.hasFlag("description")) {
                    task.setDescription(command.getFlag("description"));
                }
                if (command.hasFlag("completed") || command.hasFlag("incomplete")) {
                    task.setMark(command.hasFlag("completed"));
                }
                return task;
            } catch (UnsupportedOperationException e) {
                System.out.println(e.getMessage());
                return null;
            }
        };
    }

    private String input() throws IOException {
        try {
            dialogues.prompt();
            String result = inputReader.readLine();
            if (result == null) {
                throw new IOException("No Standard Input specified");
            }
            return result;
        } catch (IOException e) {
            dialogues.errorIoUnavailable();
            try {
                marquee.save(currentFile);
                dialogues.successSave();
            } catch (IOException f) {
                dialogues.errorSaveUnavailable(f.getMessage());
            }
            dialogues.successExit();
            throw e;
        }
    }

    private Command parse(String input) {
        try {
            return commandFormatter.parseCommand(input);
        } catch (NoSuchElementException e) {
            dialogues.errorUnknownFlag(e.getMessage());
            return null;
        } catch (DuplicateKeyException e) {
            dialogues.errorDuplicateFlag(e.getKey());
            return null;
        } catch (IllegalArgumentException e) {
            dialogues.errorUnknownCommand(input);
            return null;
        }
    }

    void main() throws IOException, ClassNotFoundException {
        // require data classes
        Class.forName("org.cs2103t.marquee.cli.command.Codes");
        Class.forName("org.cs2103t.marquee.cli.task.TaskTags");

        supportedTasks = Stream.of(TodoTask.class, EventTask.class, DeadlineTask.class)
                .collect(Collectors.toUnmodifiableMap(Class::getSimpleName, c -> c));
        inputReader = new BufferedReader(new InputStreamReader(System.in));
        commandFormatter = new CommandFormatter("/", "/");
        dialogues = new Dialogues();

        currentFile = getLocalStoragePath().resolve("Marquee", "checklist.csv");
        marquee = new Marquee();
        marquee.setClassNameEncoding(Class::getSimpleName, supportedTasks::get);
        load();

        dialogues.banner();
        dialogues.infoSaveFilePath(currentFile);
        dialogues.greetings();
        mainLoop:
        while (true) {
            Command command = parse(input());
            if (command == null) {
                continue;
            }
            switch (command.getCode()) {
                case Code c when Codes.EXIT.equals(c) -> {
                    dialogues.successExit();
                    break mainLoop;
                }
                case Code c when Codes.HELP.equals(c) -> dialogues.help(Code.getCode(command.getArgument()));
                case Code c when Codes.SAVE.equals(c) -> save();
                case Code c when Codes.LOAD.equals(c) -> load();
                case Code c when Codes.LIST.equals(c) -> dialogues.successList(marquee.list());
                case Code c when Codes.TODO.equals(c) -> {
                    if (!command.hasArgument()) {
                        dialogues.errorTaskNameMissing();
                    } else {
                        Task newTask = new TodoTask(command.getArgument());
                        marquee.addTasks(newTask);
                        dialogues.successAdd(List.of(newTask));
                    }
                }
                case Code c when Codes.DEADLINE.equals(c) -> {
                    if (!command.hasArgument()) {
                        dialogues.errorTaskNameMissing();
                    } else if (!command.hasFlag("by")) {
                        dialogues.errorDeadlineMissing();
                    } else {
                        try {
                            Task newTask = new DeadlineTask(
                                    command.getArgument(),
                                    DateTimeFormatter.parseDateTime(command.getFlag("by"))
                            );
                            marquee.addTasks(newTask);
                            dialogues.successAdd(List.of(newTask));
                        } catch (DateTimeParseException e) {
                            dialogues.errorDatetime(e.getParsedString());
                        }
                    }
                }
                case Code c when Codes.EVENT.equals(c) -> {
                    if (!command.hasArgument()) {
                        dialogues.errorTaskNameMissing();
                    } else if (!command.hasFlag("from")) {
                        dialogues.errorStartTimeMissing();
                    } else if (!command.hasFlag("to")) {
                        dialogues.errorEndTimeMissing();
                    } else {
                        try {
                            Task newTask = new EventTask(
                                    command.getArgument(),
                                    DateTimeFormatter.parseDateTime(command.getFlag("from")),
                                    DateTimeFormatter.parseDateTime(command.getFlag("to"))
                            );
                            marquee.addTasks(newTask);
                            dialogues.successAdd(List.of(newTask));
                        } catch (DateTimeParseException e) {
                            dialogues.errorDatetime(e.getParsedString());
                        } catch (IllegalArgumentException _) {
                            dialogues.errorEventEndBeforeStart();
                        }
                    }
                }
                case Code c when Codes.FIND.equals(c) -> {
                    try {
                        dialogues.successFind(find(command));
                    } catch (DateTimeParseException e) {
                        dialogues.errorDatetime(e.getParsedString());
                    }
                }
                case Code c when Codes.EDIT.equals(c) -> {
                    try {
                        dialogues.successModify(marquee.forTasks(
                                modifyFactory(command),
                                command.hasFlag("chain"),
                                parseIntArray(command.getArgument())
                        ));
                    } catch (NumberFormatException e) {
                        dialogues.errorNan(e.getMessage());
                    } catch (IndexOutOfBoundsException e) {
                        dialogues.errorIndex(e.getMessage());
                    }
                }
                case Code c when Codes.EDIT_ALL.equals(c) -> dialogues.successModify(
                        marquee.forAllTasks(modifyFactory(command), command.hasFlag("chain"))
                );
                case Code c when Codes.DELETE.equals(c) -> {
                    try {
                        dialogues.successDelete(marquee.deleteTasks(
                                command.hasFlag("chain"),
                                parseIntArray(command.getArgument())
                        ));
                    } catch (NumberFormatException e) {
                        dialogues.errorNan(e.getMessage());
                    } catch (IndexOutOfBoundsException e) {
                        dialogues.errorIndex(e.getMessage());
                    }
                }
                case Code c when Codes.DELETE_ALL.equals(c) -> dialogues.successDelete(
                        marquee.deleteAllTasks(command.hasFlag("chain"))
                );
                case Code c when Codes.DELETE_MATCHING.equals(c) -> {
                    try {
                        if (command.hasFlag("chain")) {
                            find(command);
                        }
                        dialogues.successDelete(marquee.deleteAllTasks(command.hasFlag("chain")));
                    } catch (DateTimeParseException e) {
                        dialogues.errorDatetime(e.getParsedString());
                    }
                }
                case Code c when Codes.MARK.equals(c) -> {
                    try {
                        dialogues.successMark(marquee.markTasks(
                                command.hasFlag("chain"),
                                parseIntArray(command.getArgument())
                        ));
                    } catch (NumberFormatException e) {
                        dialogues.errorNan(e.getMessage());
                    } catch (IndexOutOfBoundsException e) {
                        dialogues.errorIndex(e.getMessage());
                    }
                }
                case Code c when Codes.MARK_ALL.equals(c) -> dialogues.successMark(
                        marquee.markAllTasks(command.hasFlag("chain"))
                );
                case Code c when Codes.MARK_MATCHING.equals(c) -> {
                    try {
                        if (command.hasFlag("chain")) {
                            find(command);
                        }
                        dialogues.successMark(marquee.markAllTasks(command.hasFlag("chain")));
                    } catch (DateTimeParseException e) {
                        dialogues.errorDatetime(e.getParsedString());
                    }
                }
                case Code c when Codes.UNMARK.equals(c) -> {
                    try {
                        dialogues.successUnmark(marquee.unmarkTasks(
                                command.hasFlag("chain"),
                                parseIntArray(command.getArgument())
                        ));
                    } catch (NumberFormatException e) {
                        dialogues.errorNan(e.getMessage());
                    } catch (IndexOutOfBoundsException e) {
                        dialogues.errorIndex(e.getMessage());
                    }
                }
                case Code c when Codes.UNMARK_ALL.equals(c) -> dialogues.successUnmark(
                        marquee.unmarkAllTasks(command.hasFlag("chain"))
                );
                case Code c when Codes.UNMARK_MATCHING.equals(c) -> {
                    try {
                        if (command.hasFlag("chain")) {
                            find(command);
                        }
                        dialogues.successUnmark(marquee.unmarkAllTasks(command.hasFlag("chain")));
                    } catch (DateTimeParseException e) {
                        dialogues.errorDatetime(e.getParsedString());
                    }
                }
                default -> dialogues.errorUnsupportedCommand(command.getCode());
            }
        }
        input();
    }
}
