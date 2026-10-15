package sunnyexception;

import java.util.List;

/**
 * Indicates that a task description was not provided by the user.
 * This class extends {@link SunnyException}.
 */
public class TaskEmptyDescException extends SunnyException{
    /**
     * Constructs the exception with the specified message components and type of task involved.
     *
     * @param message the message components to construct the message describing the exception.
     * @param type the type of task that is involved.
     */
    public TaskEmptyDescException (List<String> message, String type){
        String temp = message.get(0) + type + message.get(1);
        super(temp);
    }
}