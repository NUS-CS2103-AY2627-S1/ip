package student.project.bob.personality;

import student.project.bob.model.Task;

/**
 * Stores Bob's display details and personality-specific GUI responses.
 */
public final class PersonalityProfile {
    /**
     * The default personality used by the application.
     */
    public static final PersonalityProfile DEFAULT = new PersonalityProfile(
            "Bob", "/images/Bob_the_builder.jpg", "/images/Wendy.png", "/images/btb_background.jpeg");

    private final String name;
    private final String botImagePath;
    private final String userImagePath;
    private final String backgroundImagePath;

    /**
     * Creates a personality profile.
     *
     * @param name display name of the chatbot
     * @param botImagePath classpath to the chatbot image
     * @param userImagePath classpath to the user image
     * @param backgroundImagePath classpath to the GUI background image
     */
    private PersonalityProfile(String name, String botImagePath, String userImagePath, String backgroundImagePath) {
        this.name = name;
        this.botImagePath = botImagePath;
        this.userImagePath = userImagePath;
        this.backgroundImagePath = backgroundImagePath;
    }

    /**
     * Returns Bob's display name.
     *
     * @return chatbot display name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the chatbot image resource path.
     *
     * @return chatbot image resource path
     */
    public String getBotImagePath() {
        return botImagePath;
    }

    /**
     * Returns the user image resource path.
     *
     * @return user image resource path
     */
    public String getUserImagePath() {
        return userImagePath;
    }

    /**
     * Returns the GUI background image resource path.
     *
     * @return background image resource path
     */
    public String getBackgroundImagePath() {
        return backgroundImagePath;
    }

    /**
     * Returns Bob's welcome message.
     *
     * @return welcome message
     */
    public String getWelcomeMessage() {
        return "Hello! I'm " + name + ". What can I build for you today?";
    }

    /**
     * Formats the response for adding a task.
     *
     * @param task task that was added
     * @param taskCount number of tasks after the addition
     * @return formatted add-task response
     */
    public String formatAddedTask(Task task, int taskCount) {
        return "Can we build it? I've added this task:\n\n"
                + task
                + "\nNow you have "
                + taskCount
                + " tasks in the list.";
    }

    /**
     * Formats the response for marking a task as done.
     *
     * @param task task that was marked
     * @return formatted mark response
     */
    public String formatMarkedTask(Task task) {
        return "Nice work! I've marked this task as complete:\n  [X] " + task.getDescription();
    }

    /**
     * Formats the response for marking a task as not done.
     *
     * @param task task that was unmarked
     * @return formatted unmark response
     */
    public String formatUnmarkedTask(Task task) {
        return "All right, I'll leave this task unfinished:\n  [ ] " + task.getDescription();
    }

    /**
     * Formats the response for deleting a task.
     *
     * @param task task that was deleted
     * @param taskCount number of tasks after the deletion
     * @return formatted delete response
     */
    public String formatDeletedTask(Task task, int taskCount) {
        return "I've cleared this task from the worksite:\n    "
                + task
                + "\nNow you have "
                + taskCount
                + " tasks in the list.";
    }
}
