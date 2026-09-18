package anby;

import java.util.Random;

import anby.task.Task;

/**
 * Displays messages to the user.
 */
public class Ui {
    private static final String BANNER = "    ___          __         \n"
            + "   /   |  ____  / /_  __  __\n"
            + "  / /| | / __ \\/ __ \\/ / / /\n"
            + " / ___ |/ / / / /_/ / /_/ / \n"
            + "/_/  |_/_/ /_/_.___/\\__, /  \n"
            + "                   /____/\n";
    private static final String INTRO = "Hey, I'm Anby\n"
            + "What do you need me for? I accept payment only in burgers\n\nFor free life advice type burger";
    private static final String END = "Alright see you. Don't forget my burgers okay";
    private static final String[] QUOTES = {
        "War does not determine who is right - only who is left.",
        "When you do crazy things, expect crazy results",
        "I dream of a better tomorrow, where chickens can cross the road and not be quested about their motives",
        "No I didn't trip, the floor looked like it needed a hug",
        "Better late than never, but never late is better",
        "I was standing in the park wondering why frisbees got bigger as they get closer. Then it hit me.",
        "When tempted to fight fire with fire, remember that the fire department generally uses water.",
        "Some cause happiness wherever they go; others whenever they go.",
        "Never underestimate the power of stupid people in large groups",
        "A successful man is one who makes more money than his wife can spend. "
                + "A successful woman is one who can find such a man.",
        "Behind every great man is a woman rolling her eyes.",
        "Perfection is not attainable, but if we chase perfection we can catch excellence.",
        "An idea isn't responsible for the people who believe in it.",
        "I would like to die on Mars. Just not on impact.",
        "If women ran the world we wouldn't have wars, just intense negotiations every 28 days. [Robin Williams]",
        "Between two evils, I always pick the one I never tried before.",
        "If two wrongs don't make a right, try three.",
        "Man cannot live by bread alone; he must have peanut butter.",
        "A pessimist is a person who has had to listen to too many optimists.",
        "All men are equal before fish.",
        "I've never been married, but I tell people I'm divorced so they won't think something is wrong with me.",
        "O Lord, help me to be pure, but not yet.",
        "Any kid will run any errand for you, if you ask at bedtime.",
        "We owe to the Middle Ages the two worst inventions of humanity - romantic love and gunpowder.",
        "Guilt: the gift that keeps on giving.",
        "The point of war is not to die for your country, but to make the noob on the other side die for his",
        "Always borrow money from a pessimist. He won't expect it back.",
        "Friendship is like peeing on yourself: everyone can see it, but only you get the warm feeling that it brings.",
        "Dogs have masters. Cats have staff.",
        "Knowledge is knowing a tomato is a fruit; wisdom is not putting it in a fruit salad.",
        "Why do people say 'no offense' right before they're about to offend you?",
        "By all means, marry. If you get a good wife, you'll become happy; "
                + "if you get a bad one, you'll become a philosopher.",
        "I asked God for a bike, but I know God doesn't work that way. So I stole a bike and asked for forgiveness.",
        "The best way to lie is to tell the truth . . . carefully edited truth.",
        "Do not argue with an idiot. He will drag you down to his level and beat you with experience.",
        "The only mystery in life is why the kamikaze pilots wore helmets.",
        "Going to church doesn't make you a Christian any more than standing in a garage makes you a car.",
        "A bargain is something you don't need at a price you can't resist.",
        "If you steal from one author, it's plagiarism; if you steal from many, it's research.",
        "If you think nobody cares if you're alive, try missing a couple of car payments.",
        "How is it one careless match can start a forest fire, but it takes a whole box to start a campfire?",
        "God gave us our relatives; thank God we can choose our friends.",
        "Children: You spend the first 2 years of their life teaching them to walk and talk. "
                + "Then you spend the next 16 telling them to sit down and shut-up.",
        "Nothing sucks more than that moment during an argument when you realize you're wrong.",
        "By the time a man realizes that his father was right, he has a son who thinks he's wrong.",
        "We've all heard that a million monkeys banging on a million typewriters will eventually "
                + "reproduce the entire works of Shakespeare. Now, thanks to the Internet, we know this is not true.",
        "Women who seek to be equal with men lack ambition.",
        "When you go into court you are putting your fate into the hands of twelve people "
                + "who weren't smart enough to get out of jury duty.",
        "Those people who think they know everything are a great annoyance to those of us who do.",
        "By working faithfully eight hours a day you may eventually get to be boss and work twelve hours a day.",
        "When tempted to fight fire with fire, remember that the Fire Department usually uses water.",
        "America is a country where half the money is spent buying food, "
                + "and the other half is spent trying to lose weight.",
        "A bank is a place that will lend you money, if you can prove that you don't need it.",
        "The best time to give advice to your children is while they're still young enough "
                + "to believe you know what you're talking about.",
        "Tell a man there are 300 billion stars in the universe and he'll believe you. "
                + "Tell him a bench has wet paint on it and he'll have to touch it to be sure.",
        "The human brain is a wonderful thing. It starts working the moment you are born, "
                + "and never stops until you stand up to speak in public.",
        "At every party, there are two kinds of people: those who want to go home and those who don't. "
                + "The trouble is, they are usually married to each other.",
        "You love flowers, but you cut them. You love animals, but you eat them. "
                + "You tell me you love me, so now I'm scared!",
        "I don't need a hair stylist, my pillow gives me a new hairstyle every morning.",
        "Don't worry if plan A fails, there are 25 more letters in the alphabet.",
        "Studying means 10% reading and 90% complaining to your friends that you have to study.",
        "If you want your wife to listen to you, then talk to another woman; she will be all ears.",
        "You never realize how boring your life is until someone asks what you like to do for fun.",
        "In the morning you beg to sleep more, in the afternoon you are dying to sleep, "
                + "and at night you refuse to sleep.",
        "When I said that I cleaned my room, I just meant I made a path from the doorway to my bed.",
        "Life isn't measured by the number of breaths you take, "
                + "but by the number of moments that take your breath away.",
        "The great pleasure in life is doing what people say you cannot do.",
        "If we were on a sinking ship, and there was only one life vest... I would miss you so much.",
        "All my life I thought air was free, until I bought a bag of chips.",
        "Long time ago I used to have a life, until someone told me to create a Facebook account.",
        "Never take life seriously. Nobody gets out alive anyway."
    };

    /**
     * Creates a UI helper.
     */
    public Ui() {
    }

    /**
     * Shows the greeting message when Anby starts.
     */
    public void showGreeting() {
        System.out.println(getGreeting());
    }

    /**
     * Shows the goodbye message when Anby exits.
     */
    public void showGoodbye() {
        System.out.println(getGoodbye());
    }

    /**
     * Shows all tasks in the task list.
     *
     * @param tasks task list to show
     */
    public void showList(TaskList tasks) {
        System.out.println(getList(tasks));
    }

    /**
     * Returns the greeting message.
     *
     * @return greeting message
     */
    public String getGreeting() {
        return BANNER + INTRO;
    }

    /**
     * Returns the intro message without the CLI banner.
     *
     * @return intro message
     */
    public String getIntro() {
        return INTRO;
    }

    /**
     * Returns the goodbye message.
     *
     * @return goodbye message
     */
    public String getGoodbye() {
        return END;
    }

    /**
     * Returns all tasks in the task list as a message.
     *
     * @param tasks task list to show
     * @return task list message
     */
    public String getList(TaskList tasks) {
        String message = formatTaskList(
                tasks,
                "lol you have no tasks!\n",
                "finish these and then reward me with burgers:\n");

        if (tasks.isEmpty()) {
            return message;
        }

        return message + "\n" + getTaskCountReaction(tasks.size()) + "\n";
    }

    /**
     * Shows that a task has been marked as done.
     *
     * @param task task that was marked
     */
    public void showMarked(Task task) {
        System.out.println(getMarked(task));
    }

    /**
     * Shows that a task has been marked as not done.
     *
     * @param task task that was unmarked
     */
    public void showUnmarked(Task task) {
        System.out.println(getUnmarked(task));
    }

    /**
     * Shows that a task has been deleted.
     *
     * @param task task that was deleted
     */
    public void showDeleted(Task task) {
        System.out.println(getDeleted(task));
    }

    /**
     * Shows that a task has been added.
     *
     * @param task task that was added
     * @param taskCount number of tasks after adding the task
     */
    public void showAdded(Task task, int taskCount) {
        System.out.println(getAdded(task, taskCount));
    }

    /**
     * Shows all tasks in the task list that matches with the searched keyword.
     *
     * @param matchingTasks list of tasks that match the searched keyword.
     */
    public void showFindResults(TaskList matchingTasks) {
        System.out.println(getFindResults(matchingTasks));
    }

    /**
     * Returns a message for a marked task.
     *
     * @param task task that was marked
     * @return marked task message
     */
    public String getMarked(Task task) {
        return "ooo you're done with this! that'll be one burger please:\n" + task;
    }

    /**
     * Returns a message for an unmarked task.
     *
     * @param task task that was unmarked
     * @return unmarked task message
     */
    public String getUnmarked(Task task) {
        return "hey why didn't you do this already?\n" + task;
    }

    /**
     * Returns a message for a deleted task.
     *
     * @param task task that was deleted
     * @return deleted task message
     */
    public String getDeleted(Task task) {
        return "okay i've taken away this task for you:\n" + task;
    }

    /**
     * Returns a message for an added task.
     *
     * @param task task that was added
     * @param taskCount number of tasks after adding the task
     * @return added task message
     */
    public String getAdded(Task task, int taskCount) {
        return "okay, this is a new task: \n" + task
                + "\nyou've got " + taskCount + " task(s) waiting for you..."
                + "\n\n" + getTaskCountReaction(taskCount);
    }

    /**
     * Returns all matching tasks as a message.
     *
     * @param matchingTasks list of tasks that match the searched keyword.
     * @return find results message
     */
    public String getFindResults(TaskList matchingTasks) {
        return formatTaskList(
                matchingTasks,
                "i dug up all the nearest burger chains and found nothing oopsie\n try a smaller keyword maybe",
                "heres what i found:\n");
    }

    /**
     * Returns a random quote.
     *
     * @param random random generator used to pick a quote
     * @return random quote
     */
    public String getRandomQuote(Random random) {
        return QUOTES[random.nextInt(QUOTES.length)];
    }

    private String formatTaskList(TaskList tasks, String emptyMessage, String headerMessage) {
        StringBuilder message = new StringBuilder();

        if (tasks.isEmpty()) {
            message.append(emptyMessage);
        } else {
            message.append(headerMessage);
        }

        for (int i = 0; i < tasks.size(); i++) {
            message.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
        }

        return message.toString();
    }

    private String getTaskCountReaction(int taskCount) {
        if (taskCount == 1) {
            return "just one item, light snack.";
        } else if (taskCount <= 3) {
            return "manageable combo meal, chef.";
        } else if (taskCount <= 5) {
            return "okay now the tray is getting heavy.";
        } else {
            return "bro this is a full family feast.";
        }
    }

    /**
     * Shows an error message.
     *
     * @param message error message to show
     */
    public void showError(String message) {
        System.out.println(getError(message));
    }

    /**
     * Returns an error message.
     *
     * @param message error message to show
     * @return error message
     */
    public String getError(String message) {
        return message;
    }
}
