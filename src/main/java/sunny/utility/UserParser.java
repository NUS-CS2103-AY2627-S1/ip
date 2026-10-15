package sunny.utility;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import sunny.SunnyVoice;
import sunny.storage.Storage;
import sunny.taskboard.Taskboard;
import sunny.ui.UI;
import sunnyexception.IncorrectDateFormatException;
import sunnyexception.SunnyException;
import sunnyexception.TaskEmptyDescException;
import sunnyexception.UnrecognisedTaskException;
import sunnyexception.InsufficientInfoException;
import sunnyexception.TaskOutOfBoundsException;
import sunnyexception.TooManyKeywordsException;
import task.Deadline;
import task.Event;
import task.Task;
import task.ToDo;

public class UserParser {
    private UserParser() {}
    private static class Holder {
        private static final UserParser INSTANCE = new UserParser();
    }
    /**
     * Returns the singleton instance of UserParser.
     *
     * @return the UserParser singleton instance
     */
    public static UserParser getInstance() {
        return Holder.INSTANCE;
    }

    private Storage storer = Storage.getInstance();
    private DateParser parser = DateParser.getInstance();
    private Taskboard taskboard = Taskboard.getInstance();
    private SunnyVoice sunnyVoice = SunnyVoice.getInstance();
    private String commandType = "greeting";

    /**
     * Parses the user's input and prints to the CLI the chatbot's response while
     * managing data required for the GUI.
     *
     * @param input the user's input.
     * @param ui the UI object some data is sent to.
     * @throws SunnyException indicates something wrong with the user's input.
     */
    public void parseUserInput(String input, UI ui) throws SunnyException {
        if (input.trim().isEmpty()) {
            throwUnrecognised(ui);
        }
        String[] parts = input.trim().split("\\s+");
        String command = parts[0].toLowerCase();
        Task task = null;
        switch (command) {
            case "bye":
                executeBye(ui);
                return;
            case "list":
                executeList(ui);
                return;
            case "mark":
                executeMark(ui, parts);
                return;
            case "unmark":
                executeUnmark(ui, parts);
                return;
            case "todo":
                task = executeTodo(input, ui, parts);
                break;
            case "deadline":
                task = executeDeadline(input, ui, parts);
                break;
            case "event":
                task = executeEvent(input, ui, parts);
                break;
            case "delete":
                executeDelete(ui, parts);
                return;
            case "find":
                executeFind(ui, parts);
                return;
            case "viewschedule":
                executeViewschedule(ui, parts);
                return;
            case "help":
                executeHelp(ui, parts);
                return;
            default:
                throwUnrecognised(ui);
        }
        assert task != null : "Task must exist before adding it to taskboard";
        writeAndSaveTask(ui, task);
    }

    private void writeAndSaveTask(UI ui, Task task) {
        try {
            storer.saveTasks(taskboard.getTasks()); //to check if access is denied before saving task to local taskboard.
            taskboard.addTask(task);
            storer.saveTasks(taskboard.getTasks());
            ui.replyTask(task);
        } catch (AccessDeniedException e) {
            ui.replyError(e.getMessage());
        } catch (IOException e) {
            ui.replyError(e.getMessage());
        }
    }

    private void saveTask(UI ui, Task task) {
        try {
            storer.saveTasks(taskboard.getTasks());
            ui.replyTask(task);
        } catch (AccessDeniedException e) {
            ui.replyError(e.getMessage());
        } catch (IOException e) {
            ui.replyError(e.getMessage());
        }
    }

    private void executeHelp(UI ui, String[] parts) throws UnrecognisedTaskException {
        if (parts.length > 1) {
            throwUnrecognised(ui);
        }
        commandType = "find";
        ui.replyHelp();
    }

    private void executeViewschedule(UI ui, String[] parts) throws InsufficientInfoException, TooManyKeywordsException, IncorrectDateFormatException {
        checkAndThrowIfInsufficientParts(parts, ui);
        checkIfTooManyKeywords(ui, parts);

        Optional<LocalDateTime> keyDate = parser.parse(parts[1]);
        checkIfDateIsIncorrectFormat(ui, keyDate);

        ArrayList<Task> eventsInKeyDate = new ArrayList<>();
        ArrayList<Task> deadlinesInKeyDate = new ArrayList<>();
        for (Task task : taskboard.getTasks()) {
            addTaskIfWithinDateRequirements(task, keyDate, deadlinesInKeyDate, eventsInKeyDate);
        }
        commandType = "find";
        ui.replyViewSchedule(eventsInKeyDate, deadlinesInKeyDate);
    }

    private static void addTaskIfWithinDateRequirements(Task task, Optional<LocalDateTime> keyDate, ArrayList<Task> deadlinesInKeyDate, ArrayList<Task> eventsInKeyDate) {
        if (task instanceof Deadline && ((Deadline) task).getBy() != null) {
            boolean isKeyDateBeforeDeadline = !keyDate.get().isAfter(((Deadline) task).getBy());
            if (isKeyDateBeforeDeadline) {
                deadlinesInKeyDate.add(task);
            }
        }
        else if (task instanceof Event && (((Event) task).getStart() != null && ((Event) task).getEnd() != null)) {
            boolean isKeyDateAfterStart = !keyDate.get().isBefore(((Event) task).getStart());
            boolean isKeyDateBeforeEnd = !keyDate.get().isAfter(((Event) task).getEnd());
            if (isKeyDateBeforeEnd && isKeyDateAfterStart) {
                eventsInKeyDate.add(task);
            }
        }
    }

    private void checkIfDateIsIncorrectFormat(UI ui, Optional<LocalDateTime> keyDate) throws IncorrectDateFormatException {
        if (keyDate.isEmpty()) {
            ui.setLastResponse(sunnyVoice.getException("incorrectDateFormat"));
            commandType = "error";
            throw new IncorrectDateFormatException(sunnyVoice.getException("incorrectDateFormat"));
        }
    }

    private void executeFind(UI ui, String[] parts) throws InsufficientInfoException, TooManyKeywordsException {
        checkAndThrowIfInsufficientParts(parts, ui);
        checkIfTooManyKeywords(ui, parts);

        String keyword = parts[1];
        ArrayList<Task> foundTasks = new ArrayList<>();
        for (Task task : taskboard.getTasks()) {
            if (task.getDesc().contains(keyword)) {
                foundTasks.add(task);
            }
        }

        ui.replyFind(foundTasks);
        commandType = "find";
    }

    private void executeDelete(UI ui, String[] parts) throws InsufficientInfoException, TaskOutOfBoundsException {
        checkAndThrowIfInsufficientParts(parts, ui);

        int index = getInputFromParts(parts, ui);
        checkAndThrowMissingTask(ui, index);

        Task task = taskboard.getTask(index);
        taskboard.removeTask(index);
        saveTask(ui, task);

        ui.replyDelete(task, index);
        commandType = "delete";
    }

    private Task executeEvent(String input, UI ui, String[] parts) throws TaskEmptyDescException, InsufficientInfoException, IncorrectDateFormatException {
        checkIfDescEmpty(parts, ui, "event");
        int firstSlash = input.indexOf("/from");
        int secondSlash = input.indexOf("/to");
        if (firstSlash == -1 || secondSlash == -1 || firstSlash > secondSlash) {
            throwInsufficient(ui);
        }

        EventDetails details = new EventDetails(input, firstSlash, secondSlash, ui);
        Task event = createEventForExecuteEventMethod(ui, details.startOpt, details.endOpt, details.desc, details.endString, details.startString);

        commandType = "task";
        return event;
    }

    private class EventDetails {//data class so attributes can be public
        public String desc;
        public String startString;
        public String endString;
        public Optional<LocalDateTime> startOpt;
        public Optional<LocalDateTime> endOpt;

        public EventDetails(String input, int firstSlash, int secondSlash, UI ui) throws TaskEmptyDescException, IncorrectDateFormatException {
            this.desc = input.substring(6, firstSlash);
            checkIfDeadlineOrEventDescriptionEmpty(desc, ui, "event");

            this.startString = input.substring(firstSlash + 5, secondSlash).trim();
            checkIfDateIsEmpty(startString, ui);

            this.endString = input.substring(secondSlash + 3).trim();
            checkIfDateIsEmpty(endString, ui);

            this.startOpt = parser.parse(startString);
            this.endOpt = parser.parse(endString);
        }
    }

    private Task createEventForExecuteEventMethod(UI ui, Optional<LocalDateTime> startOpt, Optional<LocalDateTime> endOpt, String desc, String endString, String startString) throws IncorrectDateFormatException {
        if (startOpt.isPresent() && endOpt.isPresent()) {
            if (startOpt.get().isAfter(endOpt.get())) {
                throwInvalidDates(ui);
            }
            if (startOpt.get().isEqual(endOpt.get())) {
                throwEqualDates(ui);
            }
            LocalDateTime startActual = startOpt.get();
            LocalDateTime endActual = endOpt.get();
            return new Event(desc, startActual, endActual, "", "");
        } else if (startOpt.isPresent()) {
            LocalDateTime startActual = startOpt.get();
            return new Event(desc, startActual, null, "", endString);
        } else if (endOpt.isPresent()) {
            LocalDateTime endActual = endOpt.get();
            return new Event(desc, null, endActual, startString, "");
        } else {
            if (startString.equals(endString)) {
                throwEqualDates(ui);
            }
            return new Event(desc, null, null, startString, endString);
        }
    }

    private Task executeDeadline(String input, UI ui, String[] parts) throws TaskEmptyDescException, InsufficientInfoException, IncorrectDateFormatException {
        checkIfDescEmpty(parts, ui, "deadline");
        int slashIndex = input.indexOf("/by");
        if (slashIndex == -1) {
            throwInsufficient(ui);
        }

        DeadlineDetails details = new DeadlineDetails(input, slashIndex, ui);
        Task deadline = createDeadlineForExecuteDeadlineMethod(details.dateOpt, details.description, details.dateString);

        commandType = "task";
        return deadline;
    }

    private class DeadlineDetails {
        public String description;
        public String dateString;
        public Optional<LocalDateTime> dateOpt;

        public DeadlineDetails(String input, int slashIndex, UI ui) throws TaskEmptyDescException, IncorrectDateFormatException {
            this.description = String.join(" ", input.substring(9, slashIndex)).trim();
            checkIfDeadlineOrEventDescriptionEmpty(description, ui, "deadline");

            this.dateString = input.substring(slashIndex + 3).trim();
            checkIfDateIsEmpty(dateString, ui);

            this.dateOpt = parser.parse(dateString);
        }
    }

    private static Task createDeadlineForExecuteDeadlineMethod(Optional<LocalDateTime> dateOpt, String description, String dateString) {
        if (dateOpt.isPresent()) {
            LocalDateTime actualDate = dateOpt.get();
            return new Deadline(description, actualDate, "");
        } else {
            return new Deadline(description, null, dateString);
        }
    }

    private Task executeTodo(String input, UI ui, String[] parts) throws TaskEmptyDescException {
        checkIfDescEmpty(parts, ui, "todo");
        Task todo = new ToDo(String.join(" ", input.substring(4)));
        commandType = "task";
        return todo;
    }

    private void executeUnmark(UI ui, String[] parts) throws InsufficientInfoException, TaskOutOfBoundsException {
        checkAndThrowIfInsufficientParts(parts, ui);

        int index = getInputFromParts(parts, ui);
        checkAndThrowMissingTask(ui, index);

        Task task = taskboard.getTask(index);
        if (!task.isDone()) {
            ui.replyAlreadyDone("unmarked", index);
            commandType = "alreadyDone";
        } else {
            task.unmark();
            saveTask(ui, task);
            ui.replyUnmark(index);
            commandType = "unmark";
        }
    }

    private void executeMark(UI ui, String[] parts) throws InsufficientInfoException, TaskOutOfBoundsException {
        Task task;
        checkAndThrowIfInsufficientParts(parts, ui);

        int index = getInputFromParts(parts, ui);
        checkAndThrowMissingTask(ui, index);

        task = taskboard.getTask(index);
        if (task.isDone()) {
            ui.replyAlreadyDone("marked", index);
            commandType = "alreadyDone";
        } else {
            task.mark();
            saveTask(ui, task);
            ui.replyMark(index);
            commandType = "mark";
        }
    }

    private void executeList(UI ui) {
        ui.replyList();
        commandType = "list";
    }

    private void executeBye(UI ui) {
        ui.replyGoodbye();
        commandType = "goodbye";
    }

    /**
     * Returns the type of command the user's input gave.
     *
     * @return the command type from the user's input.
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * Sets the type of command the user's input gave.
     *
     * @param text the command type of the user's input to be set.
     */
    public void setCommandType(String text){
        commandType = text;
    }

    private void checkIfTooManyKeywords(UI ui, String[] parts) throws TooManyKeywordsException {
        if (parts.length > 2) {
            ui.setLastResponse(sunnyVoice.getException("tooManyKeywords"));
            commandType = "error";
            throw new TooManyKeywordsException(sunnyVoice.getException("tooManyKeywords"));
        }
    }

    private void throwInvalidDates(UI ui) throws IncorrectDateFormatException{
        ui.setLastResponse(sunnyVoice.getException("startAfterEnd"));
        commandType = "error";
        throw new IncorrectDateFormatException(sunnyVoice.getException("startAfterEnd"));
    }

    private void throwUnrecognised(UI ui) throws UnrecognisedTaskException {
        ui.setLastResponse(sunnyVoice.getException("unrecognised"));
        commandType = "error";
        throw new UnrecognisedTaskException(sunnyVoice.getException("unrecognised"));
    }

    private void checkAndThrowIfInsufficientParts(String[] parts, UI ui) throws InsufficientInfoException {
        if (parts.length == 1){
            throwInsufficient(ui);
        }
    }

    private void throwInsufficient(UI ui) throws InsufficientInfoException{
        ui.setLastResponse(sunnyVoice.getException("insufficient"));
        commandType = "error";
        throw new InsufficientInfoException(sunnyVoice.getException("insufficient"));
    }

    private void checkAndThrowMissingTask(UI ui, int i) throws TaskOutOfBoundsException{
        if (!isIndexValid(i)) {
            ui.setLastResponse(sunnyVoice.getException("missingTask"));
            commandType = "error";
            throw new TaskOutOfBoundsException(sunnyVoice.getException("missingTask"));
        }
    }

    private int getInputFromParts(String[] parts, UI ui) throws NumberFormatException {
        try{
            return Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException e) {
            ui.setLastResponse(sunnyVoice.getException("notInteger"));
            commandType = "error";
            throw new NumberFormatException(sunnyVoice.getException("notInteger"));
        }
    }

    private boolean isIndexValid(int index) {
        return index >= 0 && index < taskboard.getTaskCount() && taskboard.getTask(index) != null;
    }

    private void checkIfDescEmpty(String[] parts, UI ui, String task) throws TaskEmptyDescException {
        if (parts.length == 1){
            throwDescEmpty(ui, task);
        }
    }

    private void checkIfDeadlineOrEventDescriptionEmpty(String desc, UI ui, String task) throws TaskEmptyDescException {
        if (desc.isEmpty()) {
            throwDescEmpty(ui, task);
        }
    }

    private void throwDescEmpty(UI ui, String task) throws TaskEmptyDescException {
        List<String> remarks = sunnyVoice.getDescEmptyRemarks();
        ui.setLastResponse(remarks.get(0) + task + remarks.get(1));
        commandType = "error";
        throw new TaskEmptyDescException(sunnyVoice.getListException("descEmpty"), task);
    }

    private void checkIfDateIsEmpty(String date, UI ui) throws IncorrectDateFormatException{
        if (date.isEmpty()) {
            ui.setLastResponse(sunnyVoice.getException("dateEmpty"));
            commandType = "error";
            throw new IncorrectDateFormatException(sunnyVoice.getException("dateEmpty"));
        }
    }

    private void throwEqualDates(UI ui) throws IncorrectDateFormatException{
        ui.setLastResponse(sunnyVoice.getException("equalDates"));
        commandType = "error";
        throw new IncorrectDateFormatException(sunnyVoice.getException("equalDates"));
    }
}