package ff15.ai;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ff15.FF15Exception;

/**
 * Knows what to tell the AI about FF15, so it can answer questions about FF15's
 * features and turn a request in plain words into a command FF15 understands.
 *
 * <p>An LLM knows nothing about FF15 on its own. Each prompt therefore carries a
 * "system prompt": instructions sent ahead of the user's words, listing FF15's
 * commands and saying what shape the answer must take. {@link AiHelper} does the
 * sending; this class decides what is sent and tidies up what comes back.
 */
public class AiAssistant {
    /**
     * What the AI is told about FF15's commands. It must be kept in step with
     * {@link ff15.Parser}; a command missing here is one the AI will never
     * suggest or know to mention.
     */
    static final String COMMAND_GUIDE = """
            FF15 is a task and contact manager. Its commands are:
            todo <description> - adds a task with no date.
            deadline <description> /by <date> - adds a task due by a date.
            event <description> /from <date> /to <date> - adds a task that runs between two dates.
            list - shows every task, numbered from 1.
            mark <task number> - marks a task as done.
            unmark <task number> - marks a task as not done.
            delete <task number> - removes a task.
            find <keyword> - shows the tasks whose description contains the keyword.
            on <yyyy-MM-dd | yyyy-MM | yyyy> - shows the tasks on that day, month, or year.
            contact add <name> [/phone <phone>] [/email <email>] - adds a contact.
            contact list - shows every contact, numbered from 1.
            contact delete <contact number> - removes a contact.
            contact find <keyword> - shows the contacts whose name contains the keyword.
            bye - exits FF15.
            @ai <question> - asks the AI a question about FF15's features.
            @do <request> - asks the AI to turn a request in plain words into one of the commands above.
            A <date> is yyyy-MM-dd, optionally followed by a 24-hour time with no colon, \
            e.g. 2026-12-02 or 2026-12-02 1800.
            Commands are lowercase. Descriptions and names cannot contain the | character.
            """;

    /** What the AI is told to reply when no command can do what was asked. */
    static final String NO_COMMAND = "NONE";

    private static final String QUESTION_INSTRUCTIONS = "You are helping a user of FF15, a chatbot app. "
            + "Answer the user's question about FF15's features, based only on the commands below. "
            + "If FF15 cannot do what they ask about, say so. "
            + "Keep your answer to at most two short sentences, in plain text with no markdown.\n\n";

    private static final String COMMAND_INSTRUCTIONS = "You turn a user's request into one FF15 command. "
            + "Reply with the command only, on one line, exactly as the user would type it: "
            + "no explanation, no quotes, no markdown. "
            + "Work out relative dates such as \"tomorrow\" or \"Friday\" from the calendar given below. "
            + "If no single command below can do what they ask, reply with exactly " + NO_COMMAND + ".\n\n";

    private final AiHelper aiHelper;

    /** Where today's date comes from; a test can fix it so the prompt does not change from day to day. */
    private final Supplier<LocalDate> today;

    /**
     * Creates an assistant that sends its prompts through {@code aiHelper}.
     *
     * @param aiHelper what sends the prompts to the model.
     */
    public AiAssistant(AiHelper aiHelper) {
        this(aiHelper, LocalDate::now);
    }

    /**
     * Creates an assistant that sends its prompts through {@code aiHelper} and
     * takes today's date from {@code today}.
     *
     * @param aiHelper what sends the prompts to the model.
     * @param today where today's date comes from.
     */
    AiAssistant(AiHelper aiHelper, Supplier<LocalDate> today) {
        this.aiHelper = aiHelper;
        this.today = today;
    }

    /**
     * Returns the AI's answer to a question about FF15's features.
     *
     * @throws FF15Exception if the AI could not be reached, or said nothing.
     */
    public String answerQuestion(String question) throws FF15Exception {
        String answer = aiHelper.getAiResponse(QUESTION_INSTRUCTIONS + COMMAND_GUIDE, question).strip();
        if (answer.isEmpty()) {
            throw new FF15Exception("The AI went quiet on me. Try asking another way.");
        }
        return answer;
    }

    /**
     * Returns the FF15 command the AI thinks will carry out {@code request}. The
     * command is only suggested, never run: the AI can misunderstand, so the user
     * gets to check it first. Whether it is a command FF15 can actually run is
     * left to the caller, which has the parser to find out with.
     *
     * @throws FF15Exception if the AI could not be reached, or found no command that fits.
     */
    public String suggestCommand(String request) throws FF15Exception {
        String systemPrompt = COMMAND_INSTRUCTIONS + describeWeekAhead(today.get()) + "\n\n" + COMMAND_GUIDE;
        String command = cleanCommand(aiHelper.getAiResponse(systemPrompt, request));
        if (command.isEmpty() || command.equalsIgnoreCase(NO_COMMAND)) {
            throw new FF15Exception("I can't do that. Not with the commands I've got. Not even for you.");
        }
        return command;
    }

    /**
     * Returns today's date and the dates of the seven days after it, each with its
     * day of the week, e.g. "Today is Tuesday 2026-09-29. The next seven days are:
     * Wednesday 2026-09-30, ...". An LLM predicts text rather than calculating, and
     * is unreliable at working out which date a weekday falls on; given this list,
     * "by Friday" becomes a lookup instead of a calculation it can get wrong.
     */
    static String describeWeekAhead(LocalDate today) {
        String nextSevenDays = IntStream.rangeClosed(1, 7)
                .mapToObj(today::plusDays)
                .map(AiAssistant::describeDay)
                .collect(Collectors.joining(", "));
        return "Today is " + describeDay(today) + ". The next seven days are: " + nextSevenDays + ".";
    }

    /** Returns {@code date} with its day of the week, e.g. {@code Tuesday 2026-09-29}. */
    private static String describeDay(LocalDate date) {
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + date;
    }

    /**
     * Returns the command in the AI's reply without the wrapping it sometimes adds
     * despite being told not to: a code fence, a matching pair of backticks or
     * quotes around the whole command, and any lines after the first. Models do
     * not always follow instructions to the letter, so their output is tidied
     * before use rather than trusted as is.
     */
    static String cleanCommand(String reply) {
        String withoutFences = reply.strip().replaceAll("^```[a-z]*\\s*", "").replaceAll("\\s*```$", "");
        String command = withoutFences.strip().lines().findFirst().orElse("").strip();
        // Only a matching pair is unwrapped, so the quotes in todo read "Dune" survive.
        boolean isWrapped = command.length() >= 2
                && "`\"'".indexOf(command.charAt(0)) != -1
                && command.charAt(0) == command.charAt(command.length() - 1);
        if (isWrapped) {
            command = command.substring(1, command.length() - 1).strip();
        }
        return command;
    }
}
