package ubis;

import java.nio.file.Path;

/**
 * Exercises default and relative storage paths inside a temporary child-process directory.
 * ConsoleTest verifies the output and files without touching the developer's saved tasks.
 */
public class StorageProcess {
    /**
     * Writes and reloads a task through the convenience storage entry points.
     *
     * @param args Unused command-line arguments.
     */
    public static void main(String[] args) {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo().initialise("default task"));
        System.out.println(Storage.save(tasks));
        System.out.print(Storage.load());
        System.out.println(Storage.loadWithReport().getWarning());
        System.out.println(new Ubis(tasks).getResponse("list"));
        System.out.println(Storage.save(tasks, Path.of("bare.txt")));
        System.out.print(Storage.load(Path.of("bare.txt")));
    }
}
