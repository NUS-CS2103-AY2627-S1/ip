package bob.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import bob.BobException;
import bob.task.Deadline;
import bob.task.Event;
import bob.task.Task;
import bob.task.TaskDateTime;
import bob.task.Todo;

/**
 * Keeps the task list on the hard disk, so the tasks typed in one run of the
 * chatbot are still there in the next one.
 *
 * <p>The file is plain text with one task per line, its fields separated by a
 * vertical bar, for example:
 *
 * <pre>
 * T | 1 | read book
 * D | 0 | return book | 2026-06-06
 * E | 0 | project meeting | 2026-08-06 1400 | 2026-08-06 1600
 * </pre>
 *
 * <p>The first field says which kind of task it is, the second whether it has
 * been done, the third what it is, and any field after that holds a date that
 * kind of task carries. Dates are written the way the user types them at the
 * chatbot, which is the way {@link TaskDateTime} reads them back. An event's start
 * and end are kept as two fields rather than as one, so that reading them back
 * is a matter of taking two fields apart.
 *
 * <p>A vertical bar or a backslash inside what the user typed is written as
 * {@code \|} or {@code \\}. Without that, a task such as
 * {@code todo tidy up | then rest} would be saved as a line with one field too
 * many, and would be unreadable on the way back in. A backslash in front of any
 * other character is read as an ordinary backslash, so someone editing the file by
 * hand need not know about escaping to write, for example, a folder path.
 *
 * <p>This class owns the layout of the file. Writing is shared with the tasks
 * themselves: each task says what its fields are in {@link Task#toSaveFields()}
 * and this class joins them, so adding a new kind of task does not mean editing
 * the writing code here. Reading cannot be shared that way — a line has to be
 * recognized before there is a task to ask — so it is all done here.
 *
 * <p>Nothing in this class prints anything. Whatever the user needs to be told
 * is either returned in a {@link LoadResult} or thrown as a {@link BobException},
 * leaving {@link bob.ui.Ui Ui} as the only class that writes to the console.
 */
public class Storage {

    /**
     * Where the tasks are kept, relative to the directory the chatbot is run from.
     *
     * <p>The path is built from its parts rather than written out as one piece of
     * text, so that the separator between them is the one the operating system
     * running the chatbot uses. A path spelled {@code "data/bob.txt"} would carry
     * an assumption about that separator that this project has no reason to make.
     */
    public static final Path DEFAULT_FILE_PATH = Path.of("data", "bob.txt");

    /** Text written between the fields of a saved task. */
    private static final String FIELD_SEPARATOR = " | ";

    /** The character the fields of a saved task are separated by. */
    private static final char FIELD_SEPARATOR_CHARACTER = '|';

    /** The character marking the one after it as part of a field rather than as punctuation. */
    private static final char ESCAPE_CHARACTER = '\\';

    /**
     * Added to the save file's name to name the temporary file a save writes first, so
     * that {@code data/bob.txt} is saved by way of {@code data/bob.txt.tmp}.
     */
    private static final String TEMP_FILE_SUFFIX = ".tmp";

    /**
     * Added to the save file's name to name the copy kept of a damaged save file, so
     * that {@code data/bob.txt} is backed up as {@code data/bob.txt.bak}.
     */
    private static final String BACKUP_FILE_SUFFIX = ".bak";

    /**
     * The invisible character some editors, Notepad among them, put at the very start of a
     * file they save as UTF-8, to mark it as UTF-8.
     */
    private static final String BYTE_ORDER_MARK = "\uFEFF";

    /** Said after every failure to save, so the user knows what the failure has cost. */
    private static final String UNSAVED_CHANGE_NOTE =
            "\nThe change is in this session's list, but it won't survive quitting.";

    /** How many unreadable lines are reported one by one before the rest are just counted. */
    private static final int MAX_REPORTED_BAD_LINES = 5;

    /** Position of the field saying which kind of task a saved line holds. */
    private static final int FIELD_INDEX_TYPE = 0;

    /** Position of the field saying whether a saved task has been done. */
    private static final int FIELD_INDEX_DONE = 1;

    /** Position of the field holding what a saved task is. */
    private static final int FIELD_INDEX_DESCRIPTION = 2;

    /** Position of a saved {@link Deadline}'s due date. */
    private static final int FIELD_INDEX_BY = 3;

    /**
     * Position of a saved {@link Event}'s start.
     *
     * <p>The same position as {@link #FIELD_INDEX_BY}, since both are the first field
     * after the three every task has. It is named separately so that each reading
     * says which date it expects to find there.
     */
    private static final int FIELD_INDEX_FROM = 3;

    /** Position of a saved {@link Event}'s end. */
    private static final int FIELD_INDEX_TO = 4;

    /** Number of fields a saved {@link Todo} has: kind, status, description. */
    private static final int FIELD_COUNT_TODO = 3;

    /** Number of fields a saved {@link Deadline} has: a {@link Todo}'s three, plus the due date. */
    private static final int FIELD_COUNT_DEADLINE = 4;

    /** Number of fields a saved {@link Event} has: a {@link Todo}'s three, plus a start and an end. */
    private static final int FIELD_COUNT_EVENT = 5;

    /** The file the tasks are read from and written to. */
    private final Path filePath;

    /**
     * Whether saving over the file is refused, because {@link #load} could neither read
     * it nor copy it, so the file may be the only place the tasks in it are kept.
     *
     * <p>Not {@code final}: it is cleared again by a later load that reads the file, and
     * by a save that finds the file gone, since then there is nothing left to lose.
     */
    private boolean shouldKeepUnreadFile;

    /**
     * Whether the last task list given to {@link #save} is not on the disk, because that
     * save failed and none has worked since.
     *
     * <p>Remembered so that quitting can try the save once more, rather than dropping the
     * changes without a word. A command that changes nothing never saves, so without it
     * nothing would try again until the user happened to make another change.
     */
    private boolean hasUnsavedChanges;

    /**
     * Whether the user has already been told, on trying to quit, that the unsaved changes
     * still could not be saved.
     *
     * <p>Kept here beside {@link #hasUnsavedChanges} because it has to outlast the command
     * that gave the warning: the next attempt to quit reads it, to know that it may go
     * ahead without saving. Cleared by the next save that works.
     */
    private boolean isUnsavedChangesWarningGiven;

    /**
     * Creates storage backed by one file. The file does not have to exist yet:
     * it is created, along with any missing folders above it, the first time
     * the task list is saved.
     *
     * @param filePath where to keep the tasks, for example {@link #DEFAULT_FILE_PATH}.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Represents what came of reading the save file: the tasks that could be read,
     * and any messages for the user about parts of the file that could not be.
     *
     * <p>Two things have to come back from a load, and a record is the shortest
     * honest way to return both. It is a plain data carrier, so the compiler
     * writes its constructor, accessors, {@code equals} and {@code toString}.
     *
     * @param tasks    the tasks read from the file, in the order they were saved.
     * @param messages what the user should be told about the load, if anything.
     */
    public record LoadResult(List<Task> tasks, List<String> messages) {
    }

    /**
     * Returns the tasks saved in the file, together with anything the user should
     * be told about reading it.
     *
     * <p>This method does not throw. A chatbot that cannot read its save file can
     * still be used, so every problem is turned into a message and reported rather
     * than being allowed to stop the program starting:
     *
     * <ul>
     *   <li>no file yet — the ordinary first run — is not a problem at all, and is
     *       reported as an empty list with nothing to say;</li>
     *   <li>a folder where the file should be gives an empty list, and says so along
     *       with what to do about it, since no save can replace a folder;</li>
     *   <li>a file that cannot be read at all gives an empty list, and a warning
     *       that it will be overwritten or, if it cannot even be copied, that it
     *       will not be;</li>
     *   <li>a line that cannot be understood is skipped and reported, so one
     *       damaged line does not cost the user the tasks on all the others;</li>
     *   <li>lines holding the same task are all loaded, and reported together, so
     *       the user can decide which of them to delete.</li>
     * </ul>
     *
     * <p>In the two cases where something in the file is left out of the list, the
     * file is also copied to a backup beside it, such as {@code data/bob.txt.bak},
     * and the user is told where. The next change to the list overwrites the file, so
     * without the copy it would be the end of whatever was left out.
     */
    public LoadResult load() {
        // Set again below if this load, like an earlier one, cannot read the file.
        shouldKeepUnreadFile = false;
        if (!Files.exists(filePath)) {
            return new LoadResult(List.of(), List.of());
        }
        if (Files.isDirectory(filePath)) {
            // Checked before reading, which would fail as well, but with an error that
            // makes the folder sound like a damaged file.
            return new LoadResult(List.of(), List.of(
                    filePath + " is a folder, not a file, so your tasks can't be saved.",
                    "Move or rename that folder, then start Bob again."));
        }
        List<String> lines;
        try {
            lines = removeByteOrderMark(Files.readAllLines(filePath, StandardCharsets.UTF_8));
        } catch (IOException e) {
            return reportUnreadableFile(e);
        }
        return readTasks(lines);
    }

    /**
     * Returns an empty list, and what the user should be told, for a save file that is
     * there but cannot be read at all.
     *
     * <p>The file is copied to a backup first. If the copy is made, the file is
     * overwritten by the next change like any other, since the copy keeps what was in it.
     * If not — as when the operating system refuses to let the file be read, which stops
     * the copy for the same reason — the file is the only place those tasks are kept, so
     * saving over it is refused for the rest of the session. Without that, the next change
     * would replace them all: replacing the file needs leave to write to its folder, not
     * to read the file, so nothing else stops it.
     *
     * @param readError why the file could not be read.
     */
    private LoadResult reportUnreadableFile(IOException readError) {
        List<String> messages = new ArrayList<>();
        messages.add("I couldn't read " + filePath + " (" + describe(readError) + ").");
        Path backupFilePath = siblingPath(BACKUP_FILE_SUFFIX);
        try {
            Files.copy(filePath, backupFilePath, StandardCopyOption.REPLACE_EXISTING);
            messages.add("I'm starting with an empty list. That file will be overwritten"
                    + " the next time the list changes, but the copy stays as it is.");
            messages.add(describeBackup(backupFilePath));
        } catch (IOException copyError) {
            shouldKeepUnreadFile = true;
            messages.add("I'm starting with an empty list, and I won't save over that file,"
                    + " so nothing in it is lost.");
            messages.add(describeFailedBackup(backupFilePath, copyError));
            messages.add("Changes won't be saved until you fix that file and start Bob again,"
                    + " or move it somewhere else.");
        }
        return new LoadResult(List.of(), messages);
    }

    /**
     * Returns the lines of the save file with the byte order mark taken off the start of
     * the first line, if the file begins with one.
     *
     * <p>Some editors, Notepad among them, put this invisible character at the start of a
     * file they save as UTF-8. Java reads it as part of the first line, and {@code trim}
     * does not remove it, so a file the user had only opened and saved again would have
     * its first line reported as unreadable and left out. A save does not write it back.
     */
    private static List<String> removeByteOrderMark(List<String> lines) {
        if (lines.isEmpty() || !lines.get(0).startsWith(BYTE_ORDER_MARK)) {
            return lines;
        }
        List<String> cleanedLines = new ArrayList<>(lines);
        cleanedLines.set(0, lines.get(0).substring(BYTE_ORDER_MARK.length()));
        return cleanedLines;
    }

    /**
     * Turns the lines of the save file into tasks, collecting a message about
     * every line that could not be turned into one, and about lines that hold the
     * same task. If any line could not be read, the file is backed up as well.
     *
     * <p>Blank lines are passed over without comment: they are not damage, and a
     * file that a user has opened in an editor can easily end up with one.
     */
    private LoadResult readTasks(List<String> lines) {
        List<Task> loadedTasks = new ArrayList<>();
        // The line each loaded task came from, kept so that a report names the file's
        // own line even when blank or damaged lines have been passed over.
        List<Integer> loadedLineNumbers = new ArrayList<>();
        List<String> badLineReports = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            // The user counts lines from 1, the list counts from 0.
            int lineNumber = i + 1;
            try {
                loadedTasks.add(parseTask(line));
                loadedLineNumbers.add(lineNumber);
            } catch (BobException e) {
                badLineReports.add("Line " + lineNumber + " of " + filePath
                        + " isn't a task I can read: " + e.getMessage() + ".");
            }
        }
        List<String> messages = new ArrayList<>(summarizeBadLines(badLineReports));
        if (!badLineReports.isEmpty()) {
            messages.addAll(warnOfLeftOutLines(badLineReports.size()));
        }
        messages.addAll(reportSameTaskLines(loadedTasks, loadedLineNumbers));
        return new LoadResult(loadedTasks, messages);
    }

    /**
     * Returns the warning that the lines left out of the list will not survive the
     * next save, followed by what was done to keep them.
     *
     * <p>Said plainly, because the next command that changes the list rewrites the
     * whole file, and these lines are not in it to be rewritten. The file is copied
     * first, so the warning can usually point to a copy that still has them.
     *
     * @param badLineCount how many lines were left out, at least one.
     */
    private List<String> warnOfLeftOutLines(int badLineCount) {
        assert badLineCount >= 1 : "Only lines left out of the list need a warning, not " + badLineCount;
        boolean isSingleBadLine = badLineCount == 1;
        String warningIfCopied = isSingleBadLine
                ? "It'll be dropped from " + filePath + " the next time the list changes,"
                        + " but the copy still has it."
                : "They'll be dropped from " + filePath + " the next time the list changes,"
                        + " but the copy still has them.";
        String warningIfNotCopied = isSingleBadLine
                ? "It will be lost the next time the list changes — fix the file to keep it."
                : "They will be lost the next time the list changes — fix the file to keep them.";
        return backUpDamagedFile(warningIfCopied, warningIfNotCopied);
    }

    /**
     * Copies the save file to a backup beside it, such as {@code data/bob.txt.bak},
     * and returns what the user should be told: the warning that fits whether the copy
     * was made, followed by where the copy is or why there is none.
     *
     * <p>Only called when loading has left lines out of the list. The next change
     * to the list rewrites the save file from the tasks that were loaded, so without a
     * copy whatever was left out would be gone for good. The copy is made here, at
     * load, rather than just before that first save, so that the user is told where it
     * is at the same moment as they are told what was left out.
     *
     * <p>Only one copy is kept, under a fixed name, and a copy left by an earlier start
     * is replaced. Otherwise a file found damaged on every start would fill its folder
     * with copies; the price is that a copy from an earlier start, of a file that has
     * changed since, is lost.
     *
     * <p>Anything but an ordinary file is not copied. A folder where the save file should
     * be never gets this far, since {@link #load} reports it first, and copying one would
     * only make an empty folder.
     *
     * @param warningIfCopied    the warning to give when the copy is made.
     * @param warningIfNotCopied the warning to give when it is not, which has to be
     *                           starker, since then nothing else keeps the file.
     * @return the warning, then either where the copy is, or two lines saying it could
     *         not be made and what the user should do instead.
     */
    private List<String> backUpDamagedFile(String warningIfCopied, String warningIfNotCopied) {
        if (!Files.isRegularFile(filePath)) {
            return List.of(warningIfNotCopied);
        }
        Path backupFilePath = siblingPath(BACKUP_FILE_SUFFIX);
        try {
            Files.copy(filePath, backupFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return List.of(warningIfNotCopied, describeFailedBackup(backupFilePath, e),
                    "Copy " + filePath + " somewhere safe yourself before changing the list.");
        }
        return List.of(warningIfCopied, describeBackup(backupFilePath));
    }

    /** Returns the message telling the user where the copy of the save file was made. */
    private static String describeBackup(Path backupFilePath) {
        return "The file as it was is kept in " + backupFilePath + ", so nothing in it is lost.";
    }

    /** Returns the message telling the user that the copy of the save file could not be made, and why. */
    private static String describeFailedBackup(Path backupFilePath, IOException error) {
        return "I couldn't make a backup at " + backupFilePath + " (" + describe(error) + ").";
    }

    /**
     * Returns the path of a file in the save file's folder, named after the save file
     * with {@code suffix} added, such as {@code data/bob.txt.bak}.
     */
    private Path siblingPath(String suffix) {
        return filePath.resolveSibling(filePath.getFileName() + suffix);
    }

    /**
     * Returns a message for each group of lines in the save file that hold the same
     * task, in the sense of {@link Task#isSameTaskAs}.
     *
     * <p>Every line of a group is loaded, rather than all but the first being left out.
     * The lines may differ in ways the user cares about — one marked done and the other
     * not, or the description written in different capitals — so which to keep is
     * theirs to decide. Adding such a task at the chatbot is refused, so only a file
     * edited by hand can hold two.
     *
     * <p>Each task is compared with every task after it, which is plenty fast for a
     * list a person keeps by hand.
     *
     * @param tasks       the tasks read from the file, in the order they were saved.
     * @param lineNumbers the line each of those tasks was read from, counting from 1.
     * @return the messages to show, which are none at all when no two lines hold the same task.
     */
    private List<String> reportSameTaskLines(List<Task> tasks, List<Integer> lineNumbers) {
        List<String> messages = new ArrayList<>();
        // A task already named in a group must not start a second group of its own.
        boolean[] isAlreadyReported = new boolean[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            if (isAlreadyReported[i]) {
                continue;
            }
            // The tasks are loaded into the list in the order they were read, so a task's
            // position here is its position in the list.
            List<Integer> sameTaskIndexes = new ArrayList<>(List.of(i));
            for (int j = i + 1; j < tasks.size(); j++) {
                if (tasks.get(i).isSameTaskAs(tasks.get(j))) {
                    sameTaskIndexes.add(j);
                    isAlreadyReported[j] = true;
                }
            }
            if (sameTaskIndexes.size() > 1) {
                messages.add(describeSameTaskLines(sameTaskIndexes, lineNumbers));
            }
        }
        return messages;
    }

    /**
     * Returns the message about one group of lines holding the same task, for example
     * {@code Tasks 1 and 2 in your list (lines 2 and 5 of data/bob.txt) are the same task.},
     * followed by a note that all of them were kept.
     *
     * <p>The tasks are named first by their numbers in the list, since those are the
     * numbers {@code delete} takes. The file's line numbers follow for anyone fixing the
     * file by hand. Naming only the lines would send a user who types {@code delete} to
     * the wrong task, or to none, whenever a blank or unreadable line comes before them.
     *
     * @param taskIndexes the positions in the list of the tasks in the group, at least
     *                    two, counting from 0, in the order they appear in the file.
     * @param lineNumbers the line every loaded task was read from, counting from 1, in
     *                    the order of the list.
     */
    private String describeSameTaskLines(List<Integer> taskIndexes, List<Integer> lineNumbers) {
        int taskCount = taskIndexes.size();
        assert taskCount >= 2 : "A group of the same task has at least two lines, not " + taskCount;
        // The user counts tasks from 1, the list counts from 0.
        List<Integer> taskNumbers = taskIndexes.stream()
                .map(index -> index + 1)
                .toList();
        List<Integer> sameTaskLineNumbers = taskIndexes.stream()
                .map(lineNumbers::get)
                .toList();
        String keptNote = (taskCount == 2)
                ? "I've kept both — delete the one you don't need."
                : "I've kept all " + taskCount + " — delete the ones you don't need.";
        return "Tasks " + joinNumbers(taskNumbers) + " in your list"
                + " (lines " + joinNumbers(sameTaskLineNumbers) + " of " + filePath + ")"
                + " are the same task. " + keptNote;
    }

    /**
     * Returns numbers written out as a person would list them, such as {@code 1, 2 and 4}.
     *
     * @param numbers the numbers to write out, at least two.
     */
    private static String joinNumbers(List<Integer> numbers) {
        assert numbers.size() >= 2 : "Only two or more numbers are joined, not " + numbers.size();
        List<String> numberTexts = numbers.stream()
                .map(String::valueOf)
                .toList();
        int lastIndex = numberTexts.size() - 1;
        return String.join(", ", numberTexts.subList(0, lastIndex)) + " and " + numberTexts.get(lastIndex);
    }

    /**
     * Returns what the user should be told about the lines of the save file that
     * could not be read, given a report on each of them.
     *
     * <p>Kept apart from {@link #readTasks}, which walks the file, because this is
     * different work: deciding how many of the reports to quote, and how to word
     * what is said about the rest.
     *
     * @param badLineReports one report per unreadable line, in the order the lines
     *                       appear in the file.
     * @return the messages to show, which are none at all when every line was read.
     */
    private static List<String> summarizeBadLines(List<String> badLineReports) {
        int badLineCount = badLineReports.size();
        if (badLineCount == 0) {
            return List.of();
        }

        // Only the first few are quoted, so a thoroughly damaged file
        // does not bury the greeting under hundreds of lines.
        int quotedCount = Math.min(badLineCount, MAX_REPORTED_BAD_LINES);
        List<String> messages = new ArrayList<>(badLineReports.subList(0, quotedCount));
        int unquotedLineCount = badLineCount - quotedCount;
        if (unquotedLineCount > 0) {
            messages.add("...and " + unquotedLineCount
                    + (unquotedLineCount == 1 ? " more line" : " more lines")
                    + " I couldn't read.");
        }

        messages.add(badLineCount == 1
                ? "I've left that line out of your list."
                : "I've left those " + badLineCount + " lines out of your list.");
        return messages;
    }

    /**
     * Writes the whole task list to the file, replacing whatever was there before.
     *
     * <p>The whole list is rewritten on every change rather than the one changed
     * task being edited in place. That is more writing than is strictly needed,
     * but it keeps the file saying exactly what the list says.
     *
     * <p>The lines are not written into the save file itself. They go to a temporary
     * file beside it, which is then moved over it. Writing straight into the save file
     * empties it first, so a disk filling up, or the chatbot being stopped, partway
     * through would leave it half written and every task after that point gone. Moved
     * into place, the file holds the old list or the new one, never part of each. A
     * temporary file left by a save that failed is removed, and one left by a chatbot
     * that was stopped is simply overwritten by the next save.
     *
     * <p>What this does not guard against is a power cut just after a save, before the
     * operating system has put the new contents on the disk. Forcing them there before
     * the move, through a {@link java.nio.channels.FileChannel}, would close that gap at
     * the price of slower saves, which a task list kept by hand does not need.
     *
     * <p>The lines are built with a stream because each task turns into exactly
     * one line, independently of the others, which is what {@code map} describes.
     *
     * @param tasks the task list as it now stands.
     * @throws BobException if the file or the folder holding it cannot be written, or if
     *                      the file is one {@link #load} could neither read nor copy.
     */
    public void save(List<Task> tasks) throws BobException {
        // Set before anything can fail, and cleared only once the list is on the disk.
        hasUnsavedChanges = true;
        requireNoUnreadFileInTheWay();
        requireRoomToSave();
        List<String> lines = tasks.stream()
                .map(Storage::toSaveLine)
                .toList();
        Path tempFilePath = siblingPath(TEMP_FILE_SUFFIX);
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                // Does nothing if the folder is already there, so this is safe
                // to call on every save rather than only on the first one.
                Files.createDirectories(parentDirectory);
            }
            Files.write(tempFilePath, lines, StandardCharsets.UTF_8);
            moveIntoPlace(tempFilePath);
        } catch (IOException e) {
            deleteLeftover(tempFilePath);
            throw new BobException("I couldn't save your tasks to " + filePath
                    + " (" + describe(e) + ")." + UNSAVED_CHANGE_NOTE);
        }
        hasUnsavedChanges = false;
        isUnsavedChangesWarningGiven = false;
    }

    /**
     * Returns whether the last task list given to {@link #save} failed to reach the disk,
     * with no save having worked since.
     */
    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges;
    }

    /**
     * Returns whether the user has been told, on trying to quit, that the unsaved changes
     * could not be saved, with no save having worked since.
     */
    public boolean isUnsavedChangesWarningGiven() {
        return isUnsavedChangesWarningGiven;
    }

    /**
     * Records that the user has been told, on trying to quit, that the unsaved changes
     * could not be saved, so that the next attempt to quit may go ahead without them.
     */
    public void markUnsavedChangesWarningGiven() {
        isUnsavedChangesWarningGiven = true;
    }

    /**
     * Checks that saving would not overwrite a file that {@link #load} could neither read
     * nor copy, and so would not destroy tasks that were never loaded into the list.
     *
     * <p>Once the file is gone, saving is allowed again for the rest of the session: the user
     * has moved it somewhere else, so whatever is in it is safe. {@code notExists} is asked
     * rather than {@code exists}, because a folder that cannot be looked into makes
     * {@code exists} answer no without the file being gone.
     *
     * @throws BobException if saving would overwrite such a file.
     */
    private void requireNoUnreadFileInTheWay() throws BobException {
        if (!shouldKeepUnreadFile) {
            return;
        }
        if (Files.notExists(filePath)) {
            shouldKeepUnreadFile = false;
            return;
        }
        throw new BobException("I won't save over " + filePath
                + ", because I couldn't read the tasks already in it."
                + "\nFix that file and start Bob again, or move it somewhere else so I can start a new one."
                + UNSAVED_CHANGE_NOTE);
    }

    /**
     * Checks that nothing already on the disk stands where the save file, or a folder
     * above it, has to go.
     *
     * <p>Saving would fail either way, but with an error that names a path and leaves the
     * user to work out what is wrong with it. Looked for beforehand, the problem can be
     * named, along with what to do about it. Whatever is in the way is left alone, since it
     * may be something the user needs.
     *
     * @throws BobException if the save file's place is taken by a folder, or the place of a
     *                      folder above it by a file.
     */
    private void requireRoomToSave() throws BobException {
        if (Files.isDirectory(filePath)) {
            throw new BobException("I couldn't save your tasks to " + filePath
                    + " because it is a folder, not a file." + UNSAVED_CHANGE_NOTE);
        }
        // Only walked up as far as the first folder that exists, since everything above
        // a folder is a folder too.
        for (Path folder = filePath.getParent(); folder != null && !Files.isDirectory(folder);
                folder = folder.getParent()) {
            if (Files.exists(folder)) {
                throw new BobException("I couldn't save your tasks to " + filePath
                        + " because \"" + folder + "\" is a file, not a folder."
                        + "\nMove or rename that file so I can make the folder." + UNSAVED_CHANGE_NOTE);
            }
        }
    }

    /**
     * Moves a fully written temporary file over the save file.
     *
     * <p>An atomic move is asked for first, which swaps the file in one step, so there
     * is no moment at which the save file is missing or partly replaced. A file system
     * that cannot move a file that way is given an ordinary replacing move instead,
     * which is the best it offers.
     *
     * @param tempFilePath the temporary file holding the whole new list.
     * @throws IOException if the file cannot be moved into place.
     */
    private void moveIntoPlace(Path tempFilePath) throws IOException {
        try {
            Files.move(tempFilePath, filePath, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tempFilePath, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Removes the temporary file a failed save may have left beside the save file.
     *
     * <p>A failure to remove it is ignored. The save has already failed, which is what
     * the user is told, and a temporary file that could not be removed is overwritten
     * by the next save that works.
     */
    private static void deleteLeftover(Path tempFilePath) {
        try {
            Files.deleteIfExists(tempFilePath);
        } catch (IOException e) {
            // Deliberately ignored, for the reason given above.
        }
    }

    /** Returns one task written as a line of the save file. */
    private static String toSaveLine(Task task) {
        return task.toSaveFields().stream()
                .map(Storage::escape)
                .collect(Collectors.joining(FIELD_SEPARATOR));
    }

    /**
     * Returns the task written on one line of the save file.
     *
     * <p>Every way a line can be wrong is reported separately, because these
     * messages are read by someone looking at the file to repair it, and "line 4
     * is wrong" would leave them to find out how.
     *
     * @param line one line of the file, with surrounding spaces removed.
     * @throws BobException if the line is not a task this chatbot can read.
     */
    private static Task parseTask(String line) throws BobException {
        List<String> fields = splitFields(line);
        // splitFields adds a field after the last separator whether or not there is
        // anything in it, so even a line with nothing on it comes back as one field.
        assert !fields.isEmpty() : "A saved line always splits into at least one field";
        String typeIcon = fields.get(FIELD_INDEX_TYPE);
        return switch (typeIcon) {
            case Todo.TYPE_ICON -> parseTodo(fields);
            case Deadline.TYPE_ICON -> parseDeadline(fields);
            case Event.TYPE_ICON -> parseEvent(fields);
            default -> throw new BobException("\"" + typeIcon + "\" is not a kind of task I know"
                    + " (I know " + Todo.TYPE_ICON + ", " + Deadline.TYPE_ICON
                    + " and " + Event.TYPE_ICON + ")");
        };
    }

    /** Returns the {@link Todo} written as {@code T | <done> | <description>}. */
    private static Todo parseTodo(List<String> fields) throws BobException {
        requireFieldCount(fields, FIELD_COUNT_TODO, "todo");
        Todo todo = new Todo(requireNonEmpty(fields.get(FIELD_INDEX_DESCRIPTION), "description"));
        setDone(todo, fields.get(FIELD_INDEX_DONE));
        return todo;
    }

    /** Returns the {@link Deadline} written as {@code D | <done> | <description> | <by>}. */
    private static Deadline parseDeadline(List<String> fields) throws BobException {
        requireFieldCount(fields, FIELD_COUNT_DEADLINE, "deadline");
        Deadline deadline = new Deadline(
                requireNonEmpty(fields.get(FIELD_INDEX_DESCRIPTION), "description"),
                requireDate(fields.get(FIELD_INDEX_BY), "due date"));
        setDone(deadline, fields.get(FIELD_INDEX_DONE));
        return deadline;
    }

    /**
     * Returns the {@link Event} written as {@code E | <done> | <description> | <from> | <to>}.
     *
     * <p>The start and the end are checked as a pair by {@link Event#requireValidPeriod},
     * the rule an event typed at the chatbot meets, so a hand-edited file cannot load an
     * event the user could never have added. Its message is replaced by a shorter one,
     * for the reason given at {@link #requireDate}.
     */
    private static Event parseEvent(List<String> fields) throws BobException {
        requireFieldCount(fields, FIELD_COUNT_EVENT, "event");
        String description = requireNonEmpty(fields.get(FIELD_INDEX_DESCRIPTION), "description");
        TaskDateTime from = requireDate(fields.get(FIELD_INDEX_FROM), "start time");
        TaskDateTime to = requireDate(fields.get(FIELD_INDEX_TO), "end time");
        try {
            Event.requireValidPeriod(from, to);
        } catch (BobException e) {
            // The rule has already decided the pair is refused; comparing the two
            // again only picks which of its two reasons to give.
            throw new BobException(Event.endsBeforeStart(from, to)
                    ? "the event ends before it starts"
                    : "the event starts and ends at the same moment");
        }
        Event event = new Event(description, from, to);
        setDone(event, fields.get(FIELD_INDEX_DONE));
        return event;
    }

    /**
     * Marks a freshly read task as done if its saved status field says it was.
     *
     * @param task      the task just read from the line, still marked not done.
     * @param doneField the saved status field, which is the only thing it may be:
     *                  {@value Task#DONE_FLAG} or {@value Task#NOT_DONE_FLAG}.
     * @throws BobException if the field is anything else, since a status that
     *                      cannot be read is not safely assumed to mean "not done".
     */
    private static void setDone(Task task, String doneField) throws BobException {
        if (doneField.equals(Task.DONE_FLAG)) {
            task.markAsDone();
        } else if (!doneField.equals(Task.NOT_DONE_FLAG)) {
            throw new BobException("\"" + doneField + "\" doesn't say whether the task is done"
                    + " (it should be " + Task.DONE_FLAG + " or " + Task.NOT_DONE_FLAG + ")");
        }
    }

    /**
     * Checks that a saved line has exactly as many fields as its kind of task needs,
     * so that a field is never read from a position that holds something else.
     *
     * @throws BobException if the line has too few or too many fields.
     */
    private static void requireFieldCount(List<String> fields, int expectedCount, String taskKind)
            throws BobException {
        if (fields.size() != expectedCount) {
            throw new BobException("a saved " + taskKind + " has " + expectedCount
                    + " fields, but this line has " + fields.size());
        }
    }

    /**
     * Returns a field, having checked there is something in it.
     *
     * @throws BobException if the field is empty, which would load a task the user
     *                      could never have added in the first place.
     */
    private static String requireNonEmpty(String field, String fieldName) throws BobException {
        if (field.isEmpty()) {
            throw new BobException("the " + fieldName + " is empty");
        }
        return field;
    }

    /**
     * Returns the date written in a field, having checked that it is one.
     *
     * <p>{@link TaskDateTime#parse} already refuses text that is not a date, but its
     * complaint is written for someone typing a command and runs to three lines
     * of advice. It is replaced here by a shorter one, because these messages are
     * listed alongside every other complaint about the file and are read by
     * someone repairing it.
     *
     * @param field     the saved field that should hold a date.
     * @param fieldName what to call it in a message, for example {@code due date}.
     * @throws BobException if the field is empty or does not hold a date.
     */
    private static TaskDateTime requireDate(String field, String fieldName) throws BobException {
        requireNonEmpty(field, fieldName);
        try {
            return TaskDateTime.parse(field);
        } catch (BobException e) {
            throw new BobException("the " + fieldName + " \"" + field + "\" isn't a date"
                    + " (dates are saved as " + TaskDateTime.EXAMPLE_DATE + ", or "
                    + TaskDateTime.EXAMPLE_DATE_TIME + " with a time)");
        }
    }

    /**
     * Returns the text with the two characters that mean something in this file
     * format written as escape sequences, so that a task holding either of them
     * still saves as one readable line.
     *
     * <p>Backslashes are escaped first. The other way round, the backslash added
     * in front of a bar would itself be escaped a moment later, turning
     * {@code |} into {@code \\|} — an escaped backslash followed by a separator.
     */
    private static String escape(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Returns the text with each escape sequence replaced by the character it stands for.
     *
     * <p>Only {@code \\} and {@code \|} are escape sequences, since they are the only two
     * {@link #escape} writes. A backslash in front of any other character is kept as
     * text, so a line typed into the file by hand, such as
     * {@code T | 0 | copy C:\temp\notes}, loads as it reads rather than losing its
     * backslashes — a loss the next save would otherwise make permanent.
     */
    private static String unescape(String field) {
        StringBuilder text = new StringBuilder();
        boolean isEscaped = false;
        for (char character : field.toCharArray()) {
            if (isEscaped) {
                if (!isEscapable(character)) {
                    // The backslash escapes nothing, so it is part of the text.
                    text.append(ESCAPE_CHARACTER);
                }
                text.append(character);
                isEscaped = false;
            } else if (character == ESCAPE_CHARACTER) {
                isEscaped = true;
            } else {
                text.append(character);
            }
        }
        if (isEscaped) {
            // A backslash at the very end escapes nothing, so it is kept as written.
            text.append(ESCAPE_CHARACTER);
        }
        return text.toString();
    }

    /** Returns whether a backslash in front of the character makes an escape sequence. */
    private static boolean isEscapable(char character) {
        return character == ESCAPE_CHARACTER || character == FIELD_SEPARATOR_CHARACTER;
    }

    /**
     * Returns the fields of one saved line, split at the separators that are
     * separators — an escaped bar belongs to the field it sits in.
     *
     * <p>The line is walked a character at a time rather than being handed to
     * {@code String.split}, which has no way to know that the bar in
     * {@code tidy up \| then rest} is part of the text.
     *
     * <p>Surrounding spaces are trimmed from each field, so that a file written
     * with the separator padded as {@code " | "}, and one written or hand-edited
     * without the padding, both read the same way.
     */
    private static List<String> splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;
        for (char character : line.toCharArray()) {
            if (isEscaped) {
                // Kept escaped for now; unescaping the whole field comes after the split.
                field.append(ESCAPE_CHARACTER).append(character);
                isEscaped = false;
            } else if (character == ESCAPE_CHARACTER) {
                isEscaped = true;
            } else if (character == FIELD_SEPARATOR_CHARACTER) {
                fields.add(unescape(field.toString().trim()));
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (isEscaped) {
            // A backslash at the very end escapes nothing, so it is kept as written.
            field.append(ESCAPE_CHARACTER);
        }
        fields.add(unescape(field.toString().trim()));
        return fields;
    }

    /**
     * Returns a short explanation of a file error to show the user.
     *
     * <p>Some file errors carry no message of their own, so the name of the error
     * is shown instead of an empty pair of brackets.
     *
     * <p>The operating system refusing access is put in words. That error's own message
     * is only the path it refused, which says nothing about why, and which the message
     * this explanation goes into names already.
     */
    private static String describe(IOException error) {
        if (error instanceof AccessDeniedException) {
            return "permission denied";
        }
        String reason = error.getMessage();
        if (reason == null || reason.isBlank()) {
            return error.getClass().getSimpleName();
        }
        return reason;
    }
}
