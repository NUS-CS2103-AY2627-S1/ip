package echo;

import java.util.ArrayDeque;

/**
 * Runs the Echo command-line task manager.
 *
 * <p>This class reads commands from the user, updates the task list, saves changes to storage,
 * and displays feedback through the user interface. The session ends when the user enters the
 * {@code bye} command.</p>
 */
public class Echo {
    private final TodoList todoList = new TodoList(this);
    protected Storage store = new Storage("testdata.txt", todoList);
    protected final Ui ui = new Ui();

    private final int numSavedStates = 2;
    protected ArrayDeque<Runnable> previousActions = new ArrayDeque<Runnable>(numSavedStates);

    /**
     * Parses and interprets user input, then handles the primary logic of what the user input does.
     *
     * @param input the input entered by the user
     * @return Echo's text response to input commands in the form of a String
     */
    protected String getResponse(String input) {
        assert input != null;

        String[] commandArgs = Parser.convertInputToArgs(input);
        String command = commandArgs[0];

        int todoListSize = todoList.getSize();

        if (previousActions.size() > numSavedStates) {
            previousActions.removeLast();
        }

        try {
            CommandWord cmdword = CommandWord.fromString(command);
            switch (cmdword) {
                case BYE -> {
                    return Ui.getFarewell();
                }
                case LIST -> {
                    return ui.getListString(this.todoList);
                }
                case MARK -> {
                    int taskNum = Parser.getTaskNum(todoListSize, commandArgs);
                    this.todoList.markList(taskNum);
                    this.store.writeData(this.todoList.getList());
                    return ui.getMarkString(todoList.getTask(taskNum - 1), true);
                }
                case UNMARK -> {
                    int taskNum = Parser.getTaskNum(todoListSize, commandArgs);
                    this.todoList.unmarkList(taskNum);
                    this.store.writeData(this.todoList.getList());
                    return ui.getMarkString(todoList.getTask(taskNum - 1), false);
                }
                case TODO -> {
                    String[] taskArgs = Parser.getTaskArgs(input, command);
                    String desc = String.join("/ ", taskArgs);
                    Todo newTask = new Todo(desc);
                    this.todoList.addToList(newTask);
                    this.store.writeData(this.todoList.getList());
                    return ui.getAddedTaskString(newTask, cmdword.toString());
                }
                case DEADLINE -> {
                    String[] taskArgs = Parser.getTaskArgs(input, command);
                    Deadline newTask = new Deadline(taskArgs);
                    assert taskArgs.length > 1;
                    this.todoList.addToList(newTask);
                    this.store.writeData(this.todoList.getList());
                    return ui.getAddedTaskString(newTask, cmdword.toString());
                }
                case EVENT -> {
                    String[] taskArgs = Parser.getTaskArgs(input, command);
                    Task newTask = new Event(taskArgs);
                    assert taskArgs.length > 1;
                    this.todoList.addToList(newTask);
                    this.store.writeData(this.todoList.getList());
                    return ui.getAddedTaskString(newTask, cmdword.toString());
                }
                case DELETE -> {
                    int taskNum = Parser.getTaskNum(todoListSize, commandArgs);
                    Task deletedTask = this.todoList.deleteTask(taskNum);
                    int listSize = this.todoList.getSize();
                    assert this.todoList.getSize() < todoListSize;
                    this.store.writeData(this.todoList.getList());
                    return ui.getDeleteTask(deletedTask, listSize);
                }
                case FIND -> {
                    String keyword = Parser.getKeyword(input, command);
                    TodoList searchList = this.todoList.findSearchList(keyword);
                    assert searchList.getSize() <= this.todoList.getSize();
                    return ui.getSearchListString(searchList);
                }
                case UNDO -> {
                    if (previousActions.isEmpty()) {
                        throw new InvalidCommandException();
                    }
                    previousActions.pop().run();
                    this.store.writeData(this.todoList.getList());
                    return "THE PREVIOUS ACTION PERFORMED HAS BEEN REVERSED.";
                }
                default -> {
                    throw new InvalidCommandException();
                }
            }
        } catch (InvalidCommandException e) {
            return InvalidCommandException.getInvalidCommandMessage(command);
        }
    }
}

