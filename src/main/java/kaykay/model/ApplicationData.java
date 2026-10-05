package kaykay.model;

/**
 * Holds the collections of information managed by Kaykay.
 */
public final class ApplicationData {
    /** Tasks managed by the application. */
    private final TaskList tasks;

    /** Places managed by the application. */
    private final PlaceList places;

    /** Creates application data with an empty task list. */
    public ApplicationData() {
        this(new TaskList(), new PlaceList());
    }

    /**
     * Creates application data containing the supplied tasks.
     *
     * @param tasks tasks managed by the application.
     */
    public ApplicationData(TaskList tasks) {
        this(tasks, new PlaceList());
    }

    /**
     * Creates application data containing the supplied tasks and places.
     *
     * @param tasks tasks managed by the application.
     * @param places places managed by the application.
     */
    public ApplicationData(TaskList tasks, PlaceList places) {
        this.tasks = tasks;
        this.places = places;
    }

    /**
     * Returns the tasks managed by the application.
     *
     * @return the application's task list.
     */
    public TaskList getTasks() {
        return tasks;
    }

    /**
     * Returns the places managed by the application.
     *
     * @return the application's place list.
     */
    public PlaceList getPlaces() {
        return places;
    }
}
