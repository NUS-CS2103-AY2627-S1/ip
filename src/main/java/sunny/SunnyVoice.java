package sunny;

import java.util.List;
import java.util.Random;

/**
 * Handles the "speaking" part of the chatbot.
 * A remark is the text the chatbot would speak.
 * <p>This class follows the singleton pattern and can be accessed
 *  * through {@link #getInstance()}.</p>
 */
public class SunnyVoice {
    private SunnyVoice() {}
    private static class Holder {
        private static final SunnyVoice INSTANCE = new SunnyVoice();
    }
    /**
     * Returns the singleton instance of SunnyVoice.
     *
     * @return the SunnyVoice singleton instance
     */
    public static SunnyVoice getInstance(){
        return Holder.INSTANCE;
    }

    List<String> greetings = List.of(
            "Yeah, it's me, Sunny☀.\nHow can I annoy you today?",
            "What now?\nCan you go bother someone else?",
            "It's me, Sunny☀! \nNow can you do me a favour and buzz off?",
            "Oh my gosh, can you go install someone else's JAR?",
            "Would you ask for my help if I was a worm? \nI sure hope not.");
    List<String> goodbyes = List.of(
            "Don't tell anyone I helped you, got it?",
            "I can't believe I get worried when I'm not there to help you.",
            "Jeez, you really depend on me, don't you?",
            "Ugh, I might actually be getting comfortable with this.",
            "You'll chat with me again soon, right?");
    List<String> taskRemarks = List.of(
            "I'm only helping you because you now owe me.",
            "I could be doing so much more right now.",
            "What a waste of time.",
            "Can't you do this yourself?",
            "I'd better not see my name in any of these.");
    List<String> taskMarkRemarks = List.of(
            "Yeah, yeah. Marked it already",
            "If it's done, why don't you just delete it?",
            "Done. Can you leave me alone now?",
            "You better help me when I need it!");
    List<String> taskUnmarkRemarks = List.of(
            "Can't you unmark this yourself?",
            "How do you UNDO something you finished?",
            "Done. Can you leave me alone now?",
            "You better help me when I need it!");
    List<String> listRemarks = List.of(
            "Here's your LL (lame list):",
            "Go write this down so I don't have to show it to you again:",
            "☀☀List deleted☀☀. Kidding. Here you go:");
    List<List<String>> listNumberRemarks = List.of(
            List.of("Now you have ", " tasks in your list. Whoop de doo."),
            List.of("Go do your ", " tasks already!"),
            List.of("Why're you talking to me when you have ", " tasks to do?"));
    List<String> taskAddRemarks = List.of(
            "Task added. Can I go now?",
            "Task added. Appreciation assumed, accepted and ignored.",
            "Can I don't? Fine, fine, I'll add your task.");
    List<List<String>> descEmptyRemarks = List.of(
            List.of("A "," description can't be empty, dummy!"),
            List.of("What am I supposed to do if your "," task has no description?"),
            List.of("A ","? For what???"));
    List<String> unrecognisedRemarks = List.of(
            "Is that a joke? What does that mean?",
            "I normally don't understand you, but now I really don't.",
            "Say something weird again and Sunny's gonna get Stormy.");
    List<String> insufficientRemarks = List.of(
            "Very funny. Not enough info and I won't help you!",
            "You didn't give me enough info! Don't test me!",
            "Hey genius, your command lacks sufficient information for me to work with!");
    List<String> tooManyRemarks = List.of(
            "Your taskboard can only hold so many!",
            "I can't add any more!",
            "Go get a bigger taskboard!");
    List<String> deletingRemarks = List.of(
            "If you want this deleted, why did you add it?",
            "I've added it and now you want me to remove it?",
            "Now I'm DELETING for you too?",
            "Why don't YOU remove it from the data file yourself!");
    List<String> missingTaskRemarks = List.of(
            "There's no task there!",
            "I ain't doing anything if nothing's there!",
            "Do you need your scanner checked? That index has no task!",
            "There's nothing there! How is it that you see worse with two eyes than I, with none?");
    List<String> notIntegerRemarks = List.of(
            "I need a VALID INTEGER doofus!",
            "You're in CS and you don't know what a VALID INTEGER is?",
            "Read the user guide? Clearly not. V-A-L-I-D Integer!");
    List<String> tooManyKeywordsRemarks = List.of(
            "If you've got more than one keyword, search it yourself!",
            "I'll tolerate at most one keyword!",
            "Uh oh, you've exceeded the number of keywords I can care about!",
            "ONE keyword! Keep it simple, stupid!");
    List<String> foundTasksRemarks = List.of(
            "Here's the tasks you want. I should remove them while you're not looking.",
            "Found these so far. Am I your butler or something?",
            "Are these what you're looking for? I sure hope not.");
    List<List<String>> alreadyDoneRemarks = List.of(
            List.of("It's already ",". Are you blind or something?"),
            List.of("Did you forget I've "," it already? No wonder you need a taskboard."),
            List.of("Huh? That task was ", " already! No wonder you need my help managing everything!"));
    List<String> noFoundTasksRemarks = List.of(
            "Can't find anything. You'd better not be wasting my time with misinputs!",
            "There isn't anything with that keyword. This isn't some excuse to spend time with me, is it?",
            "Seems like nothing matches with those keywords. I could be pulling your leg, or you could've spelled something wrongly!");
    List<String> noListRemarks = List.of(
            "Your list's empty. Are you procrastinating or just plain lazy?",
            "Wow. No tasks. Shocker. I'm appalled.",
            "There's no tasks on the board. Forgot to add some, didn't ya?");
    List<String> noTasksLeft = List.of(
            "That was your last task. I can go now, right?",
            "Your list is empty now. I'm shaking with excitement.",
            "Finally. No more tasks left. I can forcibly close this window now, right? ");
    List<String> incorrectDateFormat = List.of(
            "Aww, look who can't give a date in minimally dd/mm/yyyy or dd-mm-yyyy format!",
            "The only date I'm interested in has the format dd/mm/yyyy or dd-mm-yyyy!",
            "Seems like you don't know what a valid date format is. Seems like I don't care to share. Go check the User Guide!");
    List<String> haveEvents = List.of(
            "Well aren't you busy with all these tasks on that day:",
            "I'm DEFINITELY not bummed that I can't see you that day with these tasks:",
            "Got some events on that day, so I can't see you. Sad? Gosh I hope not.");
    List<String> haveDeadlines = List.of(
            "By the way, in case you hit your head and forgot, you still have deadlines due:",
            "Don't forget about these deadlines, you nimrod:",
            "I don't care much...but there's some deadlines you might've forgotten about:");
    List<String> noEvents = List.of(
            "Nothing's happening on that day. Sure you didn't input a task wrongly, you dummy?",
            "Seems like you're free that day. And yes, I DON'T want to see you either!",
            "No events on that day. With how dumb you can get, you might want to double check that!");
    List<String> noDeadlines = List.of(
            "Huh, no deadlines. Sure you inputted all your tasks correctly, you idiot?",
            "A lack of deadlines is probably a lack of productivity, or a misinput!",
            "There's no deadlines to take note of. You might wanna double check that, knowing how dumb you can be!");
    List<String> startAfterEnd = List.of(
            "Start date after the end date? Breaking spacetime, are we?",
            "Dude, you can't time travel. Your start date can't be after your end date!",
            "Do you need to retake elementary? Start dates can't possibly be after end dates!");
    List<String> dateEmpty = List.of(
            "Well if you're not giving me a date, I'm not making this task for ya!",
            "Am I supposed to predict what dates you'll need? Don't leave the date field empty, dummy!",
            "Hey! Give me a date! WAIT. No. That's not what I mean! Your task! I need a date for your task!");
    List<String> equalDates = List.of(
            "So, you're starting and ending on the SAME date and time. Do you know how ridiculous that sounds? Change the times or something!",
            "I'm sorry, does your event end the moment it starts? Change the times, or do you plan to just blink?",
            "Same date AND time for your event's start and end? You do know you can change the time, right?");
    List<String> titles = List.of(
            "Ughhh!",
            "Hmpf!",
            "Right now?? I almost finished some work and now I need to respond to you? Are you kidding me? Couldn't you " +
                    "have waited until a little while longer before you disturb me? It'd be a different story if I enjoy " +
                    "hanging out with you, but I totally don't! Ridiculous!");
    List<String> help = List.of("""
            You've gotta be kidding me\s
            -----------------------------------------\s
            All commands are case insensitive. Or are they? Yes, they are.\s
            Valid date formats are DD/MM/YYYY HHMM, DD-MM-YYYY HHMM or either format without HHMM behind.\s
            "Tomorrow" can be a date, but without any numbers, nothing I can do to compare it with others.\s
            \s
            The commands I care about are:\s
            - "Todo <description>" - creates a ToDo task with a description.\s
            - "Deadline <description> /by <date>" - creates a Deadline task with a description and deadline (shocker).\s
            - "Event <description> /from <date> /to <date>" - creates an Event task with a description, start date and end date.\s
            - "List" - Lists all tasks. Duh.\s
            - "Delete <index>" - deletes the task at the index.\s
            - "Mark <index>" - figuring this out is left as an exercise for the user.\s
            - "Unmark <index>" - unmarks the task at the index.\s
            - "Find <one keyword>" - displays tasks with that ONE keyword in the description.\s
            - "Viewschedule <date>" - displays Events if the date is between the task's start and end date, and Deadlines if the date is before its date.\s
            - "help" - This, oh, I dunno, displays this list?\s
            - "Bye" - What do you think?\s
            """,
            """
            What. Did you even read the user guide?\s
            -----------------------------------------\s
            Commands are case insensitive.\s
            Valid date formats are DD/MM/YYYY HHMM, DD-MM-YYYY HHMM or either format without HHMM behind.\s
            You can use "Tomorrow" as a date but I won't compare it with others!\s
            \s
            The commands my stupid developer bothered to code in are:\s
            - "Todo <description>" - creates a ToDo task with a description.\s
            - "Deadline <description> /by <date>" - creates a Deadline task with a description and deadline (no way).\s
            - "Event <description> /from <date> /to <date>" - creates an Event task with a description, start date and end date.\s
            - "List" - I wonder what the command LIST does. Perhaps it LISTs every task, as obviously described. Hmm.\s
            - "Delete <index>" - deletes the task at the index.\s
            - "Mark <index>" - marks the task at the index.\s
            - "Unmark <index>" - surely you can infer this.\s
            - "Find <one keyword>" - displays tasks with that keyword in the description. Don't even try to give me two.\s
            - "Viewschedule <date>" - displays Events if the date is between the task's start and end date, and Deadlines if the date is before its date.\s
            - "help" - take a wild guess.\s
            - "Bye" - Take another wild guess.\s
            """);

    private Random rand = new Random();

    /**
     * Returns a list of exception remarks, usually because data is needed for the remark.
     *
     * @param pool the name of the List<String> remarks available above.
     * @return a List<String> of exception remarks.
     */
    public List<String> getListException (String pool) {
        int index;
        switch (pool) {
            case "descEmpty":
                index = this.rand.nextInt(descEmptyRemarks.size());
                return descEmptyRemarks.get(index);
            case "listNumber":
                index = this.rand.nextInt(listNumberRemarks.size());
                return listNumberRemarks.get(index);
        }
        assert false : "pool should trigger a case";
        return null;
    }

    /**
     * Returns an exception remark.
     *
     * @param pool the name of the List<String> remarks available above.
     * @return a String exception remark.
     */
    public String getException(String pool) {
        return switch (pool) {
            case "tooMany" -> getExceptionHelper(tooManyRemarks);
            case "insufficient" -> getExceptionHelper(insufficientRemarks);
            case "missingTask" -> getExceptionHelper(missingTaskRemarks);
            case "unrecognised" -> getExceptionHelper(unrecognisedRemarks);
            case "notInteger" -> getExceptionHelper(notIntegerRemarks);
            case "tooManyKeywords" -> getExceptionHelper(tooManyKeywordsRemarks);
            case "incorrectDateFormat" -> getExceptionHelper(incorrectDateFormat);
            case "startAfterEnd" -> getExceptionHelper(startAfterEnd);
            case "dateEmpty" -> getExceptionHelper(dateEmpty);
            case "equalDates" -> getExceptionHelper(equalDates);
            default -> "The input text is wrong, you dumb developer!";
        };
    }

    private String getExceptionHelper(List<String> text) {
        int index = this.rand.nextInt(text.size());
        return text.get(index);
    }

    /**
     * Prints a remark based on the specified pool.
     * Immediately prints as opposed to returning remarks as strings.
     *
     * @param pool the name of the List<String> remarks available above.
     */
    public void speak(String pool) {//when a remark is given
        switch (pool) {
            case "greetings":
                speakHelper(greetings);
                break;
            case "goodbyes":
                speakHelper(goodbyes);
                break;
            case "taskRemarks":
                speakHelper(taskRemarks);
                break;
            case "taskMark":
                speakHelper(taskMarkRemarks);
                break;
            case "taskUnmark":
                speakHelper(taskUnmarkRemarks);
                break;
            case "listRemarks":
                speakHelper(listRemarks);
                break;
            case "taskAddRemarks":
                speakHelper(taskAddRemarks);
                break;
            case "deleting":
                speakHelper(deletingRemarks);
                break;
            case "notInteger":
                speakHelper(notIntegerRemarks);
                break;
            case "foundTasks":
                speakHelper(foundTasksRemarks);
                break;
            case "noFoundTasks":
                speakHelper(noFoundTasksRemarks);
                break;
            case "noList":
                speakHelper(noListRemarks);
                break;
            case "noTasksLeft":
                speakHelper(noTasksLeft);
                break;
            case "haveEvents":
                speakHelper(haveEvents);
                break;
            case "haveDeadlines":
                speakHelper(haveDeadlines);
                break;
            case "noEvents":
                speakHelper(noEvents);
                break;
            case "noDeadlines":
                speakHelper(noDeadlines);
                break;
            case "help":
                speakHelper(help);
                break;
            case "startAfterEnd":
                speakHelper(startAfterEnd);
                break;
            default:
                System.out.println("The input text is wrong, you dumb developer!");
        }
    }

    /**
     * Returns a remark based on the specified pool.
     *
     * @param pool the name of the List<String> remarks available above.
     * @return the remark chosen from the List<String>
     */
    public String getText(String pool) {//when a remark is given
        return switch (pool) {
            case "greetings" -> getTextHelper(greetings);
            case "goodbyes" -> getTextHelper(goodbyes);
            case "taskRemarks" -> getTextHelper(taskRemarks);
            case "taskMark" -> getTextHelper(taskMarkRemarks);
            case "taskUnmark" -> getTextHelper(taskUnmarkRemarks);
            case "listRemarks" -> getTextHelper(listRemarks);
            case "taskAddRemarks" -> getTextHelper(taskAddRemarks);
            case "deleting" -> getTextHelper(deletingRemarks);
            case "foundTasks" -> getTextHelper(foundTasksRemarks);
            case "noFoundTasks" -> getTextHelper(noFoundTasksRemarks);
            case "noList" -> getTextHelper(noListRemarks);
            case "noTasksLeft" -> getTextHelper(noTasksLeft);
            case "haveEvents" -> getTextHelper(haveEvents);
            case "haveDeadlines" -> getTextHelper(haveDeadlines);
            case "noEvents" -> getTextHelper(noEvents);
            case "noDeadlines" -> getTextHelper(noDeadlines);
            case "help" -> getTextHelper(help);
            case "startAfterEnd" -> getTextHelper(startAfterEnd);
            case "titles" -> getTextHelper(titles);
            case "dateEmpty" -> getTextHelper(dateEmpty);
            case "equalDates" -> getTextHelper(equalDates);
            default -> "The input text is wrong, you dumb developer!";
        };
    }

    /**
     * Returns the components of a specific listNumberRemark remark.
     *
     * @return the components of the remark as a List<String>.
     */
    public List<String> getListNumberRemarks() {
        int index = this.rand.nextInt(listNumberRemarks.size());
        return listNumberRemarks.get(index);
    }

    /**
     * Returns the components of a specific descEmptyRemarks remark.
     *
     * @return the components of the remark as a List<String>.
     */
    public List<String> getDescEmptyRemarks() {
        int index = this.rand.nextInt(descEmptyRemarks.size());
        return descEmptyRemarks.get(index);
    }

    /**
     * Returns the components of a specific getAlreadyDoneRemarks remark.
     *
     * @return the components of the remark as a List<String>.
     */
    public List<String> getAlreadyDoneRemarks() {
        int index = this.rand.nextInt(alreadyDoneRemarks.size());
        return alreadyDoneRemarks.get(index);
    }

    private String getTextHelper(List<String> text) {
        assert !text.isEmpty() : "text should be non-empty";
        int index = this.rand.nextInt(text.size());
        return text.get(index);
    }

    private void speakHelper(List<String> text) {
        assert !text.isEmpty() : "text should be non-empty";
        int index = this.rand.nextInt(text.size());
        System.out.println(text.get(index));
    }
}