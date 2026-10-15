package walle;

import java.io.IOException;

import walle.exceptions.SaveFailedException;
import walle.exceptions.WALLEException;
import walle.note.Note;
import walle.note.NoteList;
import walle.parser.CommandType;
import walle.parser.Parser;
import walle.storage.NoteLoadResult;
import walle.storage.Storage;
import walle.storage.TaskLoadResult;
import walle.task.Task;
import walle.task.TaskList;
import walle.ui.Ui;

/**
 * Core of the WALLE chatbot. Wires together the {@link Ui}, {@link Storage},
 * {@link Parser}, and {@link TaskList} components, loads any previously saved
 * tasks, and turns one line of user input into a response at a time via
 * {@link #getResponse(String)} -- used by both the CLI loop in {@link #main}
 * and the JavaFX GUI.
 */
// CHECKSTYLE.OFF: AbbreviationAsWordInName - "WALLE" is the chatbot's actual name, not an abbreviation to shorten.
public class WALLE {
    // CHECKSTYLE.ON: AbbreviationAsWordInName

    private final TaskList tasks;
    private final NoteList notes;
    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private final String greeting;

    /**
     * Creates a WALLE instance: sets up its components and loads any
     * previously saved tasks, folding any load error into the greeting so
     * both the CLI and the GUI surface it the same way.
     */
    public WALLE() {
        this.ui = new Ui();
        this.storage = new Storage("src/main/data/memory.txt", "src/main/data/notes.txt");
        this.parser = new Parser();

        String greetingText = ui.formatGreeting();

        TaskList loadedTasks;
        try {
            TaskLoadResult result = storage.load();
            loadedTasks = new TaskList(result.tasks());
            if (result.skippedLines() > 0) {
                greetingText += ui.formatSkippedLinesWarning(result.skippedLines());
            }
        } catch (Exception e) {
            loadedTasks = new TaskList();
            greetingText += ui.formatErrorMessage(e);
        }
        this.tasks = loadedTasks;

        NoteList loadedNotes;
        try {
            NoteLoadResult result = storage.loadNotes();
            loadedNotes = new NoteList(result.notes());
            if (result.skippedLines() > 0) {
                greetingText += ui.formatSkippedLinesWarning(result.skippedLines());
            }
        } catch (Exception e) {
            loadedNotes = new NoteList();
            greetingText += ui.formatErrorMessage(e);
        }
        this.notes = loadedNotes;

        this.greeting = greetingText;
    }

    /**
     * Starts WALLE's text-based command-line interface.
     *
     * @param args unused command-line arguments.
     */
    public static void main(String[] args) {
        new WALLE().runCli();
    }

    // Drive the CLI loop until the user says 'bye'
    private void runCli() {
        System.out.println(greeting);

        Response response;
        do {
            String input = ui.readCommand();
            response = getResponse(input);
            System.out.println(response.text());
        } while (!response.isExit());

        ui.closeScanner();
    }

    /**
     * Returns WALLE's greeting, e.g. to show as the first message in a GUI.
     *
     * @return the greeting message.
     */
    public String getGreeting() {
        return greeting;
    }

    /**
     * Processes one line of user input and returns WALLE's response,
     * persisting any change to the task list as a side effect.
     *
     * @param input the raw command string entered by the user.
     * @return the response to show the user, flagged as an error if the
     *         command failed.
     */
    public Response getResponse(String input) {
        try {
            // Collapse leading/trailing/repeated whitespace so stray spacing (e.g.
            // "  mark  1 ") doesn't stop the parser from recognising the command.
            String normalized = input.strip().replaceAll("\\s+", " ");
            CommandType type = parser.parseCommandType(normalized);
            return new Response(processCommand(type, normalized), false, type == CommandType.BYE);
        } catch (WALLEException e) {
            return new Response(ui.formatErrorMessage(e), true, false);
        } catch (RuntimeException e) {
            // Last-resort catch-all for unanticipated bugs, so a caller (CLI or GUI)
            // gets a visible message instead of the whole interface crashing/hanging.
            return new Response(ui.formatErrorMessage(e), true, false);
        }
    }

    // Runs one already-classified command and returns its reply text; throws on
    // failure so getResponse can turn that into an error-flagged Response.
    private String processCommand(CommandType type, String input) {
        switch (type) {
            case BYE:
                return ui.formatFarewell();

            case LIST:
                return ui.formatTaskList(tasks.getTasks());

            case MARK: {
                String response = ui.formatTaskMarkedUpdate(tasks.mark(parser.parseIndex(input)));
                updateMemoryFile();
                return response;
            }

            case UNMARK: {
                String response = ui.formatTaskUnmarkedUpdate(tasks.unmark(parser.parseIndex(input)));
                updateMemoryFile();
                return response;
            }

            case DELETE: {
                int pos = parser.parseIndex(input);
                Task tmp = tasks.delete(pos);
                updateMemoryFile();
                return ui.formatTaskDeletionUpdate(tmp, tasks.size());
            }

            case TODO:
            case DEADLINE:
            case EVENT: {
                Task newTask = parser.parseTask(type, input);
                tasks.add(newTask);
                updateMemoryFile();
                return ui.formatTaskAdditionUpdate(newTask, tasks.size());
            }

            case FIND: {
                String keyword = parser.parseKeyword(input);
                return ui.formatMatchingTasks(tasks.find(keyword));
            }

            case NOTE: {
                Note newNote = new Note(parser.parseNoteText(input));
                notes.add(newNote);
                updateMemoryFile();
                return ui.formatNoteAdditionUpdate(newNote, notes.size());
            }

            case LIST_NOTES:
                return ui.formatNoteList(notes.getNotes());

            case DELETE_NOTE: {
                int pos = parser.parseIndex(input);
                Note deletedNote = notes.delete(pos);
                updateMemoryFile();
                return ui.formatNoteDeletionUpdate(deletedNote, notes.size());
            }

            default:
                // Every CommandType is handled explicitly above; reaching here means a new
                // value was added without updating this switch, which is a real bug -- fail
                // loudly instead of silently returning a blank response.
                throw new AssertionError("Unhandled CommandType: " + type);
        }
    }

    // Persist any changes to the task list or note list made during the session
    private void updateMemoryFile() {
        try {
            storage.save(tasks.getTasks());
            storage.saveNotes(notes.getNotes());
        } catch (IOException e) {
            // Previously this only printed to System.out, which the GUI never shows --
            // a failed save looked identical to a successful one. Throw instead so the
            // failure reaches the user as part of the response, in both CLI and GUI.
            throw new SaveFailedException(e.getMessage());
        }
    }

}
