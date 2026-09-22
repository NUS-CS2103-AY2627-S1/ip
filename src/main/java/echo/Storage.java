package echo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;

import java.util.Scanner;

/**
 * Handles the reading and writing of data to and from storage, so tasks are saved between sessions.
 */
public class Storage {
    private File listFile;
    private TodoList todoList;

    public Storage(String filePath, TodoList todoList) {
        this.listFile = new File(filePath);
        this.todoList = todoList;
    }

   /**
     * <p>Reads the stored tasks from the storage text file if it exists
     * and puts them in the TodoList in Echo. If the storage file does not exist,
     * it creates the storage text file.</p>
     */
    public void readData() {
        try {
            Scanner fileReader = new Scanner(listFile);
            while (fileReader.hasNext()) {
                String nextLine = fileReader.nextLine();

                assert nextLine != null;

                String[] storedTaskArgs = nextLine.split(" // ");
                String input = String.join(" /", storedTaskArgs);

                String taskType = storedTaskArgs[0];
                
		assert taskType.length() == 1;

                String markedString = storedTaskArgs[1];

                boolean marked = Boolean.parseBoolean(markedString);

                Task currentTask = null;

                if (taskType.equals("T")) {
                    String[] taskArgs = Parser.getStoredTaskArgs(input, taskType);
                    currentTask = new Todo(taskArgs[0]);
                    todoList.addToListFromStorage(currentTask);
                } else if (taskType.equals("D")) {
                    String[] taskArgs = Parser.getStoredTaskArgs(input, taskType);
                    currentTask = new Deadline(taskArgs);
                    todoList.addToListFromStorage(currentTask);
                } else if (taskType.equals("E")) {
                    String[] taskArgs = Parser.getStoredTaskArgs(input, taskType);
                    currentTask = new Event(taskArgs);
                    todoList.addToListFromStorage(currentTask);
                } else {
                    assert false;
                }

                if (marked && currentTask != null) {
                    currentTask.mark();
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found");
            try {
                listFile.createNewFile();
                System.out.println("File created: " + listFile.getName());
            } catch (IOException ioException) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Writes all the tasks in the taskList to the storage file.
     *
     * @param taskList the list of tasks to be added to the storage file.
     */
    protected void writeData(ArrayList<Task> taskList) {
        try {
            assert taskList != null;

            FileWriter fw = new FileWriter(this.listFile);

            for (Task task : taskList) {
                fw.write(task.toStorageFormat() + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
