package duke.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import duke.exception.DukeException;
import duke.place.Place;
import duke.place.PlaceList;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.TaskList;
import duke.task.ToDo;

/**
 * Handles loading tasks and places from files and saving them.
 */
public class Storage {
    private final String filePath;
    private final String placeFilePath;
    private PlaceList placeList = new PlaceList();

    /**
     * Constructs a Storage handler configured for the specified task file path.
     *
     * @param filePath The path to the file used for loading and saving tasks.
     */
    public Storage(String filePath) {
        this(filePath, resolvePlaceFilePath(filePath));
    }

    /**
     * Constructs a Storage handler configured for both task and place file paths.
     *
     * @param filePath      The path to the file used for loading and saving tasks.
     * @param placeFilePath The path to the file used for loading and saving places.
     */
    public Storage(String filePath, String placeFilePath) {
        assert filePath != null && !filePath.trim().isEmpty() : "File path must not be null or empty";
        assert placeFilePath != null && !placeFilePath.trim().isEmpty() : "Place file path must not be null or empty";
        this.filePath = filePath;
        this.placeFilePath = placeFilePath;
    }

    private static String resolvePlaceFilePath(String filePath) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent == null) {
            return "places.txt";
        }
        return new File(parent, "places.txt").getPath();
    }

    /**
     * Loads tasks from the file specified by filePath.
     *
     * @return An ArrayList of tasks loaded from the file.
     * @throws DukeException If there is an issue accessing or reading the file.
     */
    public ArrayList<Task> load() throws DukeException {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        File file = new File(filePath);
        if (file.exists() && file.isDirectory()) {
            throw new DukeException("The specified task data path is a directory, not a file: " + filePath);
        }
        if (file.exists() && !file.canRead()) {
            throw new DukeException("Cannot read task data file (permission denied): " + filePath);
        }
        if (!file.exists()) {
            return loadedTasks;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                Task task = parseTask(line);
                if (task != null) {
                    loadedTasks.add(task);
                }
            }
        } catch (FileNotFoundException e) {
            throw new DukeException("Could not find data file at: " + filePath);
        } catch (Exception e) {
            throw new DukeException("Error loading tasks from file: " + e.getMessage());
        }
        return loadedTasks;
    }

    private Task parseTask(String line) {
        try {
            String[] parts = line.split("\\s*\\|\\s*");
            if (parts.length < 3) {
                return null;
            }
            String type = parts[0];
            boolean isDone = parts[1].equals("1");
            String name = parts[2];

            Task task = null;
            if (type.equals("T")) {
                task = new ToDo(name);
            } else if (type.equals("D") && parts.length >= 4) {
                task = new Deadline(name, parts[3]);
            } else if (type.equals("E") && parts.length >= 5) {
                task = new Event(name, parts[3], parts[4]);
            }

            if (task != null && isDone) {
                task.markAsDone();
            }
            return task;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Saves the tasks in the given TaskList to the file specified by filePath.
     *
     * @param taskList The TaskList containing tasks to save.
     * @throws DukeException If an I/O error occurs while saving.
     */
    public void save(TaskList taskList) throws DukeException {
        assert taskList != null : "TaskList to save must not be null";
        save(taskList.getTasks());
    }

    /**
     * Saves the given list of tasks to the file specified by filePath.
     *
     * @param tasks The list of tasks to save.
     * @throws DukeException If an I/O error occurs while saving.
     */
    public void save(ArrayList<Task> tasks) throws DukeException {
        assert tasks != null : "Tasks list to save must not be null";
        File file = new File(filePath);
        if (file.exists() && file.isDirectory()) {
            throw new DukeException("Cannot save tasks to a directory path: " + filePath);
        }
        if (file.exists() && !file.canWrite()) {
            throw new DukeException("Cannot write to task data file (permission denied): " + filePath);
        }
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                throw new DukeException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
            List<String> lines = tasks.stream()
                    .map(Task::toFileFormat)
                    .collect(Collectors.toList());
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            throw new DukeException("An error occurred while saving tasks.");
        }
    }

    /**
     * Returns the PlaceList currently managed by this storage.
     *
     * @return The PlaceList.
     */
    public PlaceList getPlaceList() {
        return placeList;
    }

    /**
     * Sets the PlaceList managed by this storage.
     *
     * @param placeList The PlaceList to associate with storage.
     */
    public void setPlaceList(PlaceList placeList) {
        assert placeList != null : "PlaceList must not be null";
        this.placeList = placeList;
    }

    /**
     * Loads places from the configured place file path.
     *
     * @return The loaded PlaceList.
     * @throws DukeException If the file cannot be accessed or parsed.
     */
    public PlaceList loadPlaces() throws DukeException {
        ArrayList<Place> loadedPlaces = new ArrayList<>();
        File file = new File(placeFilePath);
        if (file.exists() && file.isDirectory()) {
            throw new DukeException("The specified place data path is a directory, not a file: " + placeFilePath);
        }
        if (file.exists() && !file.canRead()) {
            throw new DukeException("Cannot read place data file (permission denied): " + placeFilePath);
        }
        if (!file.exists()) {
            this.placeList = new PlaceList(loadedPlaces);
            return this.placeList;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\s*\\|\\s*", 2);
                String name = parts[0].trim();
                String details = parts.length > 1 ? parts[1].trim() : "";
                if (!name.isEmpty()) {
                    loadedPlaces.add(new Place(name, details));
                }
            }
        } catch (FileNotFoundException e) {
            throw new DukeException("Could not find places data file at: " + placeFilePath);
        } catch (Exception e) {
            throw new DukeException("Error loading places from file: " + e.getMessage());
        }
        this.placeList = new PlaceList(loadedPlaces);
        return this.placeList;
    }

    /**
     * Saves the current PlaceList to the configured place file path.
     *
     * @throws DukeException If an I/O error occurs while saving.
     */
    public void savePlaces() throws DukeException {
        savePlaces(this.placeList);
    }

    /**
     * Saves the specified PlaceList to the configured place file path.
     *
     * @param places The PlaceList to save.
     * @throws DukeException If an I/O error occurs while saving.
     */
    public void savePlaces(PlaceList places) throws DukeException {
        assert places != null : "PlaceList to save must not be null";
        File file = new File(placeFilePath);
        if (file.exists() && file.isDirectory()) {
            throw new DukeException("Cannot save places to a directory path: " + placeFilePath);
        }
        if (file.exists() && !file.canWrite()) {
            throw new DukeException("Cannot write to place data file (permission denied): " + placeFilePath);
        }
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                throw new DukeException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
            List<String> lines = places.getPlaces().stream()
                    .map(Place::toFileFormat)
                    .collect(Collectors.toList());
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            throw new DukeException("An error occurred while saving places.");
        }
    }
}
