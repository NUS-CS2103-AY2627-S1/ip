package sunny.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import sunny.SunnyVoice;
import sunny.taskboard.Taskboard;
import sunny.utility.UserParser;
import sunnyexception.IncorrectDateFormatException;
import sunnyexception.SunnyException;
import sunnyexception.TaskEmptyDescException;
import sunnyexception.UnrecognisedTaskException;
import sunnyexception.InsufficientInfoException;
import sunnyexception.TaskOutOfBoundsException;

import sunnyexception.TooManyKeywordsException;
import sunnyexception.TooManyTasksException;
import task.Task;

/**
 * Handles the interactions with the user.
 * <p>This class follows the singleton pattern and can be accessed
 * through {@link #getInstance()}.</p>
 */
public class UI {
    private UI() {}
    private static class Holder {
        private static final UI INSTANCE = new UI();
    }
    /**
     * Returns the singleton instance of UI.
     *
     * @return the UI singleton instance
     */
    public static UI getInstance() {
        return Holder.INSTANCE;
    }

    private Taskboard taskboard = Taskboard.getInstance();
    private SunnyVoice sunnyVoice = SunnyVoice.getInstance();
    private UserParser userParser = UserParser.getInstance();
    boolean isRunning = true;
    private String lastResponse;

    private static void printBar() {
        System.out.println("____________________________________________________________");
    }

    /**
     * Prints a goodbye remark and sets isRunning to false, usually to exit the program
     * by stopping run().
     */
    public void replyGoodbye() {
        lastResponse = sunnyVoice.getText("goodbyes");
        printBar();
        sunnyVoice.speak("goodbyes");
        printBar();
        isRunning = false;
    }

    /**
     * Prints a listRemark and the tasks in the taskboard.
     * The numbering is the order the tasks were added in.
     * If there are none, it just prints the remark.
     */
    public void replyList() {
        printBar();
        if (taskboard.getTaskCount() == 0) {
            sunnyVoice.speak("noList");
            lastResponse = sunnyVoice.getText("noList");
        } else {
            sunnyVoice.speak("listRemarks");
            String response = "";
            int i = 0;
            while (i < taskboard.getTaskCount()) {
                System.out.println(i + 1 + "." + taskboard.getTask(i));
                response += i + 1 + "." + taskboard.getTask(i) + "\n";
                i += 1;
            }
            lastResponse = sunnyVoice.getText("listRemarks") + "\n" + response;
        }
        printBar();
    }

    /**
     * Prints a taskMark remark and the task selected by the index.
     *
     * @param index the index that selects the task to be marked.
     */
    public void replyMark(int index) {
        lastResponse = sunnyVoice.getText("taskMark") + "\n" + (index + 1) + "." + taskboard.getTask(index);
        printBar();
        sunnyVoice.speak("taskMark");
        System.out.println(index + "." + taskboard.getTask(index));
        printBar();
    }

    /**
     * Prints a taskUnmark remark and the task selected by the index.
     *
     * @param index the index that selects the task to be unmarked.
     */
    public void replyUnmark(int index) {
        lastResponse = sunnyVoice.getText("taskUnmark") + "\n" + (index + 1) + "." + taskboard.getTask(index);
        printBar();
        sunnyVoice.speak("taskUnmark");
        System.out.println(index + "." + taskboard.getTask(index));
        printBar();
    }

    /**
     * Prints an alreadyDone remark and the task selected by the index.
     *
     * @param index the index that selects the task.
     */
    public void replyAlreadyDone(String task, int index) {
        List<String> remarks = sunnyVoice.getAlreadyDoneRemarks();
        lastResponse = remarks.get(0) + task + remarks.get(1) + "\n" + (index + 1) + "." + taskboard.getTask(index);
        printBar();
        sunnyVoice.speak(remarks.get(0) + task + remarks.get(1));
        System.out.println(index + "." + taskboard.getTask(index));
        printBar();
    }

    /**
     * Prints a deleting remark, the task selected by the index, a listNumberRemark and
     * the number of tasks in the taskboard.
     *
     * @param temp the task to be deleted.
     */
    public void replyDelete(Task temp, int i) {
        printBar();
        sunnyVoice.speak("deleting");
        System.out.println("    " + (i + 1) + ". " + temp);
        lastResponse = sunnyVoice.getText("deleting") + "\n" + "    " + (i + 1) + ". " + temp + "\n";

        if (taskboard.getTaskCount() == 0) {
            lastResponse += sunnyVoice.getText("noTasksLeft");
            sunnyVoice.speak("noTasksLeft");
        } else {
            List<String> remarks = sunnyVoice.getListNumberRemarks();
            lastResponse += remarks.get(0) + taskboard.getTaskCount() + remarks.get(1);
            System.out.println(remarks.get(0) + taskboard.getTaskCount() + remarks.get(1));
        }
        printBar();
    }

    /**
     * Prints a taskAddRemark, the task to be added, a listNumberRemark, the number of tasks
     * in the taskboard, and a taskRemark.
     *
     * @param temp the task to be added to the taskboard.
     */
    public void replyTask(Task temp) {
        List<String> remarks = sunnyVoice.getListNumberRemarks();
        lastResponse = sunnyVoice.getText("taskAddRemarks") + "\n" + "    " + temp + "\n" + remarks.get(0) + taskboard.getTaskCount() + remarks.get(1);
        printBar();
        sunnyVoice.speak("taskAddRemarks");
        System.out.println("    " + temp);
        System.out.println(remarks.get(0) + taskboard.getTaskCount() + remarks.get(1));
        sunnyVoice.speak("taskRemarks");
        printBar();
    }

    /**
     * Prints the tasks with a specific keyword entered.
     *
     * @param foundTask the tasks that contain the keyword.
     */
    public void replyFind(ArrayList<Task> foundTask) {
        printBar();
        String response = "";
        int i = 0;
        while (i < foundTask.size()) {
            System.out.println(i + 1 + "." + foundTask.get(i));
            response += i + 1 + "." + foundTask.get(i) + "\n";
            i += 1;
        }
        if (response.isEmpty()) {
            sunnyVoice.speak("noFoundTasks");
            lastResponse = sunnyVoice.getText("noFoundTasks");
        } else {
            sunnyVoice.speak("foundTasks");
            lastResponse = sunnyVoice.getText("foundTasks") + "\n" + response;
        }
        printBar();
    }

    /**
     * Prints a list of Events and Deadlines, or a remark if either list is empty.
     *
     * @param events the list of Events to be printed.
     * @param deadlines the list of Deadlines to be printed.
     */
    public void replyViewSchedule(ArrayList<Task> events, ArrayList<Task> deadlines) {
        String response = "";
        if (events.isEmpty() && deadlines.isEmpty()) {
            replyNoEventsOrDeadlines();
        } else if (events.isEmpty()) {
            replyNoEventsHaveDeadlines(deadlines, response);
        } else if (deadlines.isEmpty()) {
            replyHaveEventsNoDeadlines(events, response);
        } else {
            replyHaveEventsHaveDeadlines(events, deadlines, response);
        }
    }

    private void replyHaveEventsHaveDeadlines(ArrayList<Task> events, ArrayList<Task> deadlines, String response) {
        int i = 1;
        int j = 1;
        printBar();
        response += sunnyVoice.getText("haveEvents") + "\n\n";
        for (Task event : events) {
            System.out.println(i + "." + event);
            response += i + "." + event + "\n";
            i += 1;
        }
        response += "\n" + sunnyVoice.getText("haveDeadlines") + "\n\n";
        for (Task deadline : deadlines) {
            System.out.println(j + "." + deadline);
            response += j + "." + deadline + "\n";
            j += 1;
        }
        printBar();
        lastResponse = response;
    }

    private void replyHaveEventsNoDeadlines(ArrayList<Task> events, String response) {
        int i = 1;
        printBar();
        response += sunnyVoice.getText("haveEvents") + "\n\n";
        for (Task event : events) {
            System.out.println(i + "." + event);
            response += i + "." + event + "\n";
            i += 1;
        }
        sunnyVoice.speak("noDeadlines");
        printBar();
        lastResponse = response + "\n\n" + sunnyVoice.getText("noDeadlines");
    }

    private void replyNoEventsHaveDeadlines(ArrayList<Task> deadlines, String response) {
        int i = 1;
        printBar();
        sunnyVoice.speak("noEvents");
        response += sunnyVoice.getText("haveDeadlines") + "\n\n";
        for (Task deadline : deadlines) {
            System.out.println(i + "." + deadline);
            response += i + "." + deadline + "\n";
            i += 1;
        }
        printBar();
        lastResponse = sunnyVoice.getText("noEvents") + "\n\n" + response;
    }

    private void replyNoEventsOrDeadlines() {
        printBar();
        sunnyVoice.speak("noEvents");
        sunnyVoice.speak("noDeadlines");
        printBar();
        lastResponse = sunnyVoice.getText("noEvents") + "\n\n" + sunnyVoice.getText("noDeadlines");
    }

    /**
     * Replies with a list of the available commands.
     */
    public void replyHelp() {
        printBar();
        sunnyVoice.speak("help");
        lastResponse = sunnyVoice.getText("help");
        printBar();
    }

    /**
     * Replies with a specific error description.
     *
     * @param e the message from the error.
     */
    public void replyError(String e) {
        printBar();
        System.out.println(e);
        lastResponse = e;
        printBar();
    }

    /**
     * Continuously reads user input and sends it to UserParser for processing until the UI is stopped.
     * Handles exceptions resulting from invalid input.
     *
     * @param scanner a scanner inputted by a different class.
     */
    public void run(Scanner scanner) {
        while (isRunning) {
            try{
                if (taskboard.getTaskCount() == 101){
                    throw new TooManyTasksException(sunnyVoice.getException("tooMany"));
                }
                String input = scanner.nextLine();

                userParser.parseUserInput(input, this);
            }
            catch (UnrecognisedTaskException | TaskEmptyDescException | InsufficientInfoException |
                   TooManyTasksException | TaskOutOfBoundsException | TooManyKeywordsException |
                   IncorrectDateFormatException e) {
                printBar();
                System.out.println(e.getMessage());
                printBar();
                lastResponse = e.getMessage();
            } catch (NumberFormatException e) { //if something other than an integer was used, or the integer is too large/small
                printBar();
                sunnyVoice.speak("notInteger");
                printBar();
                lastResponse = sunnyVoice.getText("notInteger");
            } catch (SunnyException e) { //no catch for out of bounds to see if code was the issue rather than user
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Resets the class by setting isRunning to true, thereby allowing run() to run.
     */
    public void reset() {
        isRunning = true;
    }

    /**
     * Sets the lastResponse attribute.
     *
     * @param text the text to set the lastResponse to.
     */
    public void setLastResponse(String text) {
        lastResponse = text;
    }

    /**
     * Returns the LastResponse attribute as a String.
     *
     * @return the String representation of the lastResponse.
     */
    public String getLastResponse() {
        return lastResponse;
    }

}
