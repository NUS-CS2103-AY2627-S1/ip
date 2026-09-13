package odysseus;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * A console personal assistant that records a traveler's tasks.
 */
public class Odysseus {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String INTRO_ART = String.join(System.lineSeparator(),
            " ___    ____  __   __  ____   ____  _____  _   _  ____",
            " / _ \\  |  _ \\ \\ \\ / / / ___| / ___|| ____|| | | |/ ___|",
            "| | | | | | | | \\ V /  \\___ \\ \\___ \\|  _|  | | | |\\___ \\",
            "| |_| | | |_| |  | |    ___) | ___) | |___ | |_| | ___) |",
            " \\___/  |____/   |_|   |____/ |____/|_____| \\___/ |____/");
    private static final Path DEFAULT_STORAGE_PATH = Path.of("data", "odysseus.txt");
    private final Storage storage;
    private final LearningStorage learningStorage;
    private final Parser parser;
    private final TaskList tasks;
    private final LearningDeck learningDeck;
    private final String loadingMessage;
    private LearningCard activeCard;

    /**
     * Creates Odysseus using the standard voyage-log location.
     */
    public Odysseus() {
        this(DEFAULT_STORAGE_PATH);
    }

    /**
     * Creates Odysseus with the supplied voyage-log location.
     *
     * @param storagePath location of the persistent voyage log.
     */
    public Odysseus(Path storagePath) {
        storage = new Storage(storagePath);
        learningStorage = new LearningStorage(storagePath.resolveSibling("odysseus-learning.txt"));
        parser = new Parser();
        TaskList loadedTasks;
        LearningDeck loadedLearningDeck;
        String recoveredLoadingMessage = "";
        try {
            loadedTasks = storage.load();
        } catch (OdysseusException exception) {
            loadedTasks = new TaskList();
            recoveredLoadingMessage = "I could not load the ship's log. Starting with an empty log.";
        }
        tasks = loadedTasks;
        try {
            loadedLearningDeck = learningStorage.load();
        } catch (OdysseusException exception) {
            loadedLearningDeck = new LearningDeck();
            recoveredLoadingMessage += (recoveredLoadingMessage.isEmpty() ? "" : System.lineSeparator())
                    + "I could not load Athena's Archive. Starting with an empty archive.";
        }
        learningDeck = loadedLearningDeck;
        loadingMessage = recoveredLoadingMessage;
    }

    /**
     * Starts Odysseus and processes commands until the traveler says goodbye.
     */
    public static void main(String[] args) {
        run(new Scanner(System.in), System.out, DEFAULT_STORAGE_PATH);
    }

    /**
     * Runs an Odysseus conversation using the supplied input, output, and storage path.
     */
    static void run(Scanner scanner, PrintStream output, Path storagePath) {
        output.println(INTRO_ART);
        output.println("Ahoy, traveler! I am Odysseus, long tested by sea and fate.");
        output.println("What course shall we chart together?");
        output.println(DIVIDER);

        Odysseus odysseus = new Odysseus(storagePath);
        if (!odysseus.loadingMessage.isEmpty()) {
            output.println(odysseus.loadingMessage);
        }
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            output.println(DIVIDER);
            if (command.equals("bye")) {
                break;
            }
            output.println(odysseus.getResponse(command));
            output.println(DIVIDER);
        }

        output.println("Farewell, traveler. May Athena guide your voyage until we meet again.");
        output.println(DIVIDER);
    }

    /**
     * Processes one traveler command and returns Odysseus's response.
     *
     * @param command command to process.
     * @return a user-facing response in Odysseus's voice.
     */
    public String getResponse(String command) {
        return getConversationResponse(command).message();
    }

    /**
     * Processes one traveler command and describes how its reply should be presented.
     *
     * @param command command to process.
     * @return a user-facing response and whether it corrects a command error.
     */
    public ConversationResponse getConversationResponse(String command) {
        try {
            if (command.equals("bye")) {
                return standardResponse("Farewell, traveler. May Athena guide your voyage until we meet again.");
            }
            if (command.equals("list")) {
                return standardResponse(taskListResponse());
            }
            if (command.equals("cards") || command.startsWith("cards ")) {
                return standardResponse(cardListResponse(parser.parseOptionalTopic(command, "cards")));
            }
            if (command.equals("review") || command.startsWith("review ")) {
                activeCard = learningDeck.nextCard(parser.parseOptionalTopic(command, "review"));
                return standardResponse("Athena offers a card from " + activeCard.getTopic() + ":"
                        + System.lineSeparator() + activeCard.getPrompt() + System.lineSeparator()
                        + "answer <your response> | reveal");
            }
            if (command.equals("answer") || command.startsWith("answer ")) {
                return standardResponse(answerActiveCard(parser.parseAnswer(command)));
            }
            if (command.equals("reveal")) {
                return standardResponse(revealActiveCard());
            }
            if (command.equals("mastered")) {
                return standardResponse(markActiveCardMastered());
            }
            if (command.equals("again")) {
                return standardResponse(markActiveCardForReview());
            }
            if (command.equals("forget") || command.startsWith("forget ")) {
                LearningCard card = learningDeck.deleteCard(parser.parseCardNumber(command));
                if (card == activeCard) {
                    activeCard = null;
                }
                return standardResponse(saveLearningWarning() + "The sea has carried this card from Athena's Archive:"
                        + System.lineSeparator() + "  " + card.getPrompt());
            }
            if (command.equals("learn") || command.startsWith("learn ")) {
                LearningCard card = parser.parseLearningCard(command);
                learningDeck.addCard(card);
                return standardResponse(saveLearningWarning() + "Athena has added this card to her archive:"
                        + System.lineSeparator() + "  [" + card.getTopic() + "] " + card.getPrompt());
            }
            if (command.equals("find") || command.startsWith("find ")) {
                return standardResponse(matchingTasksResponse(parser.parseFindKeyword(command)));
            }
            if (TaskAction.MARK.matches(command)) {
                Task task = tasks.getTask(parser.parseTaskNumber(command, TaskAction.MARK));
                task.markAsDone();
                return standardResponse(saveWarning() + "Well sailed! I've marked this task as done:"
                        + System.lineSeparator() + "  " + task);
            }
            if (TaskAction.UNMARK.matches(command)) {
                Task task = tasks.getTask(parser.parseTaskNumber(command, TaskAction.UNMARK));
                task.markAsNotDone();
                return standardResponse(saveWarning() + "This task awaits its hour again:"
                        + System.lineSeparator() + "  " + task);
            }
            if (TaskAction.DELETE.matches(command)) {
                Task task = tasks.deleteTask(parser.parseTaskNumber(command, TaskAction.DELETE));
                return standardResponse(saveWarning() + "The waves have carried this task from our log:"
                        + System.lineSeparator() + "  " + task
                        + System.lineSeparator() + taskCountResponse());
            }
            Task task = parser.parseTask(command);
            tasks.addTask(task);
            return standardResponse(saveWarning() + "Well charted. I've added this task:"
                    + System.lineSeparator() + "  " + task
                    + System.lineSeparator() + taskCountResponse());
        } catch (OdysseusException exception) {
            return new ConversationResponse(exception.getMessage(), true);
        }
    }

    /**
     * Marks an ordinary command outcome for the presentation adapter.
     */
    private ConversationResponse standardResponse(String message) {
        return new ConversationResponse(message, false);
    }

    /**
     * Saves tasks through storage and reports a recoverable saving error.
     */
    private String saveWarning() {
        try {
            storage.save(tasks);
            return "";
        } catch (OdysseusException exception) {
            return "I could not save the ship's log." + System.lineSeparator();
        }
    }

    /**
     * Saves learning cards and reports a recoverable saving error.
     */
    private String saveLearningWarning() {
        try {
            learningStorage.save(learningDeck);
            return "";
        } catch (OdysseusException exception) {
            return "I could not save Athena's Archive." + System.lineSeparator();
        }
    }

    /**
     * Evaluates the traveler's answer for the active learning card.
     */
    private String answerActiveCard(String response) throws OdysseusException {
        LearningCard card = requireActiveCard();
        activeCard = null;
        if (card.answerMatches(response)) {
            card.markMastered();
            return saveLearningWarning() + "Well recalled. The answer is " + card.getAnswer() + "."
                    + System.lineSeparator() + "Its mastery now stands at " + card.getMastery() + ".";
        }
        card.markForReview();
        return saveLearningWarning() + "Not this time—the answer is " + card.getAnswer() + "."
                + System.lineSeparator() + "The card remains close at hand for another voyage.";
    }

    /**
     * Reveals the active answer while letting the traveler assess their recall.
     */
    private String revealActiveCard() throws OdysseusException {
        LearningCard card = requireActiveCard();
        return "Athena's answer: " + card.getAnswer() + System.lineSeparator()
                + "Use mastered if you recalled it, or again if it needs another voyage.";
    }

    /**
     * Records a successful self-assessment for the active learning card.
     */
    private String markActiveCardMastered() throws OdysseusException {
        LearningCard card = requireActiveCard();
        card.markMastered();
        activeCard = null;
        return saveLearningWarning() + "Well recalled. Its mastery now stands at " + card.getMastery() + ".";
    }

    /**
     * Records that the active learning card should be presented again soon.
     */
    private String markActiveCardForReview() throws OdysseusException {
        LearningCard card = requireActiveCard();
        card.markForReview();
        activeCard = null;
        return saveLearningWarning() + "The card remains close at hand for another voyage.";
    }

    /**
     * Returns the active card or explains how to begin a study turn.
     */
    private LearningCard requireActiveCard() throws OdysseusException {
        if (activeCard == null) {
            throw new OdysseusException("Choose a card with review before answering, revealing, or assessing it.");
        }
        return activeCard;
    }

    /**
     * Returns the task-count sentence for the current voyage log.
     */
    private String taskCountResponse() {
        int taskCount = tasks.getTaskCount();
        return "Our voyage now holds " + taskCount + " task" + (taskCount == 1 ? "." : "s.");
    }

    /**
     * Returns a formatted rendering of learning cards in an optional topic.
     */
    private String cardListResponse(String topic) throws OdysseusException {
        var cardNumbers = learningDeck.findCardNumbers(topic);
        if (cardNumbers.isEmpty()) {
            if (topic == null) {
                return "Athena's Archive is empty. Try: learn nostos /answer homecoming.";
            }
            return "Athena has no cards on " + topic + ".";
        }
        StringBuilder response = new StringBuilder("Here are Athena's learning cards:");
        for (int cardNumber : cardNumbers) {
            LearningCard card = learningDeck.getCard(cardNumber);
            response.append(System.lineSeparator()).append(cardNumber).append(". [")
                    .append(card.getTopic()).append("] ").append(card.getPrompt())
                    .append(" (mastery: ").append(card.getMastery()).append(")");
        }
        return response.toString();
    }

    /**
     * Returns a formatted rendering of every task in the voyage log.
     */
    private String taskListResponse() throws OdysseusException {
        if (tasks.getTaskCount() == 0) {
            return "My ship's log is clear, traveler.";
        }
        StringBuilder response = new StringBuilder("Here are the tasks on our voyage, traveler:");
        for (int number = 1; number <= tasks.getTaskCount(); number++) {
            response.append(System.lineSeparator()).append(number).append(". ").append(tasks.getTask(number));
        }
        return response.toString();
    }

    /**
     * Returns a formatted rendering of every task matching a keyword.
     */
    private String matchingTasksResponse(String keyword) throws OdysseusException {
        StringBuilder response = new StringBuilder("Here are the matching tasks in your list:");
        for (int taskNumber : tasks.findTaskNumbers(keyword)) {
            response.append(System.lineSeparator()).append(taskNumber).append(". ").append(tasks.getTask(taskNumber));
        }
        return response.toString();
    }
}
