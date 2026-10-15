package sanyueqi.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

class TaskList {
    private final ArrayList<Task> taskList;

    public TaskList() {
        this.taskList = new ArrayList<>();
    }

    public void addTask(Task task) {
        this.taskList.add(task);
    }

    /**
     * Serializes each task into comma-separated values after the task list is successfully updated internally,
     * Then, the tasks are written to the master CSV.
     */
    public void writeTasks() {
        ArrayList<String> serialisedTasks = new ArrayList<>();
        for (Task task : this.taskList) {
            serialisedTasks.add(task.toCsv());
        }
        try {
            Files.write(Paths.get("logbook.csv"), serialisedTasks);
        } catch (IOException e) {
            System.out.println("Critical error: Writing tasks failed.");
        }
    }

    /**
     * Adds a task to the list if it is valid, writes it to the master CSV, and calls the print function.
     *
     * @param task The task to work with.
     */
    public String addAndWriteTask(Task task) {
        if (task != null) {
            addTask(task);
            writeTasks();
            return printNewTask(task);
        }
        return null;
    }

    /**
     * Prints a formatted string for a successfully added task.
     *
     * @param task The task to work with.
     * @return The string for testing purposes.
     */
    public String printNewTask(Task task) {
        if (task != null) {
            return String.format("Okay! I've added a new task:\n\t%s\nYou currently have %d tasks in the list.\n", task, taskList.size());
        } else {
            return null;
        }
    }

    public int getNumOfTasks() {
        return this.taskList.size();
    }

    /**
     * Marks a Task as either done or not done.
     *
     * @param parts The parts of a Task.
     * @param done Whether a Task should be marked as done or not done.
     */
    public String markTask(String[] parts, boolean done) {
        if (parts.length < 2) {
            return "Sorry, you need to specify the task number!";
        } else if (parts.length > 2) {
            return "Sorry, you've specified too many inputs!";
        }
        try {
            int num = Integer.parseInt(parts[1]);
            if (num < 1 || num > this.taskList.size()) {
                return String.format("Sorry, I can't %smark task %d. You have %d items in your list!\n",
                                     done ? "" : "un", num, taskList.size());
            }
            Task task = this.taskList.get(num - 1);
            if (task.isDone() != done) {
                task.markDone(done);
                writeTasks();
                String taskMessage = String.format("\n\t%s\n", task);
                return String.format(done ? "Great job on completing this task!%s"
                                          : "Okay, I've marked this task as not done yet:%s", taskMessage);
            } else {
                String taskMessage = String.format("\n\t%s\n", task);
                return String.format(done ? "You've already marked the following task as done!%s"
                                          : "This task is already currently marked as not done yet!%s", taskMessage);
            }
        } catch (NumberFormatException e) {
            return "Sorry, you've entered an invalid task number!";
        }
    }

    /**
     * Prints the task list.
     */
    public String printTasks() {
        StringBuilder s = new StringBuilder("Here are the tasks in your list:\n");
        int i = 1;
        for (Task task : this.taskList) {
            s.append(String.format("%d. %s\n", i, task));
            i += 1;
        }
        return s.toString();
    }

    /**
     * Finds and prints all Tasks whose descriptions contain a keyword as a substring.
     *
     * @param parts The parts of a keyword.
     */
    public String findTasks(String[] parts) {
        StringBuilder keyword = new StringBuilder();
        boolean isFindRemoved = false;
        for (String s : parts) {
            if (isFindRemoved) {
                keyword.append(s);
                keyword.append(" ");
            } else {
                isFindRemoved = true;
            }
        }

        if (keyword.isEmpty()) return "Sorry! Search keyword cannot be empty!";

        keyword.deleteCharAt(keyword.length() - 1);
        ArrayList<Task> results = new ArrayList<>();
        for (Task task : this.taskList) {
            if (task.getDesc().contains(keyword)) {
                results.add(task);
            }
        }

        if (!results.isEmpty()) {
            StringBuilder s = new StringBuilder("Here are the matching tasks in your list:\n");
            int i = 1;
            for (Task task : results) {
                s.append(String.format("%d. %s\n", i, task));
                i += 1;
            }
            return s.toString();

        } else {
            return "Sorry, I couldn't find any matching tasks!";
        }
    }

    /**
     * Deletes a Task.
     *
     * @param parts The parts of a Task.
     */
    public String deleteTask(String[] parts) {
        if (parts.length < 2) {
            return "Sorry, you need to specify the task number!";
        } else if (parts.length > 2) {
            return "Sorry, you've specified too many inputs!";
        }

        try {
            int num = Integer.parseInt(parts[1]);
            if (num < 1 || num > this.taskList.size()) {
                return String.format("Sorry, I can't delete task %d. You have %d items in your list!\n",
                                     num, taskList.size());
            }
            Task task = this.taskList.get(num - 1);
            this.taskList.remove(num - 1);
            writeTasks();
            return String.format("Okay, I've deleted this task:\n\t%s\nYou currently have %d tasks in the list.",
                                 task, this.taskList.size());

        } catch (NumberFormatException e) {
            return "Sorry, you've entered an invalid task number!";
        }
    }
}