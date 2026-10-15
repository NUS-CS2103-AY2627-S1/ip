package bobo.ui;

import java.util.Arrays;
import java.util.List;

/**
 * Manages Bobo's dynamic personality state and phrase selection based on task list size.
 * Uses 5 size categories matching the 4 actions: Welcome, Add, Delete, and Empty/Clear.
 */
public class PersonalityManager {

    private static final String[] PHRASES = {
        /* Stage 1 (0–4 items): Extremely tiny, nervous, squeaky Bobo */
        "*squeak!* Eep! Hi there... I-I'm tiny Bobo... please don't step on me!",
        "*squeeeeak* A new task?! It's so big, but I'll try my absolute tiny best!",
        "*eep!* Task removed! Whew, my tiny shoulders feel a little lighter!",
        "*whimper* No tasks left... I'm just a little squeaky dust speck floating here...",

        /* Stage 2 (5–8 items): Small but more confident Bobo */
        "Hello hello! Bobo is growing a tiny bit bigger and feeling steady!",
        "Added to your list! Bobo's feet are firmly planted now!",
        "Task completed and removed! Bobo nods with growing confidence!",
        "List is empty! Bobo is standing tall on tip-toes, ready for action!",

        /* Stage 3 (9–12 items): Average-sized, energetic Bobo */
        "Hey there! Bobo here, bursting with energy and ready to crush to-dos!",
        "Task locked in! Bobo flexes with enthusiastic vigor!",
        "Task vanished! Bobo high-fives the air with boundless energy!",
        "No tasks in sight! Bobo warms up with energetic jumping jacks!",

        /* Stage 4 (13–16 items): Large, booming, confident Bobo */
        "**GREETINGS MORTAL!** BOBO HAS GROWN LARGE AND FULL OF POWER!",
        "**TASK ACKNOWLEDGED!** BOBO CARRIES THIS WEIGHT WITH IMMENSE EASE!",
        "**TASK CRUSHED!** BOBO'S BOOMING CONFIDENCE RECHOES THROUGH THE REALM!",
        "**LIST CLEARED!** BOBO STANDS TALL AS AN UNSTOPPABLE BEACON OF PRODUCTIVITY!",

        /* Stage 5 (17+ items): Giant Bobo with dramatic, powerful wording */
        "**BEHOLD THE MIGHTY TITAN BOBO! SHAKER OF WORLDS, COMMANDER OF TASKS!**",
        "**ANOTHER TASK SWALLOWED BY BOBO'S INFINITE MONUMENTAL PRODUCTIVITY!**",
        "**TASK ANNIHILATED! BOBO'S THUNDEROUS POWER SHATTERS MOUNTAINS OF TO-DOS!**",
        "**THE QUEUE LIES EMPTY BEFORE THE COLOSSAL TITAN BOBO! TREMBLE BEFORE MY SUPREMACY!**"
    };

    /**
     * Calculates the starting index for the 4-phrase action group based on item count.
     *
     * @param itemCount Total number of tasks currently in the list.
     * @return Starting index of the 4 phrases (0, 4, 8, 12, or 16).
     */
    public static int getStartIndex(int itemCount) {
        if (itemCount < 5) {
            return 0;
        } else if (itemCount < 9) {
            return 4;
        } else if (itemCount < 13) {
            return 8;
        } else if (itemCount < 17) {
            return 12;
        } else {
            return 16;
        }
    }

    /**
     * Retrieves the 4 action phrases (Welcome, Add, Delete, Empty) for the current task count.
     *
     * @param itemCount Total number of tasks currently in the list.
     * @return List of 4 personality phrases.
     */
    public static List<String> getPhrasesForCount(int itemCount) {
        int startIndex = getStartIndex(itemCount);
        return Arrays.asList(
            PHRASES[startIndex],
            PHRASES[startIndex + 1],
            PHRASES[startIndex + 2],
            PHRASES[startIndex + 3]
        );
    }

    /**
     * Gets the welcome/greeting phrase for the current task count.
     *
     * @param itemCount Total number of tasks.
     * @return Welcome greeting phrase string.
     */
    public static String getGreetingPhrase(int itemCount) {
        return PHRASES[getStartIndex(itemCount)];
    }

    /**
     * Gets the task addition response phrase for the current task count.
     *
     * @param itemCount Total number of tasks.
     * @return Task addition phrase string.
     */
    public static String getTaskAddedPhrase(int itemCount) {
        return PHRASES[getStartIndex(itemCount) + 1];
    }

    /**
     * Gets the task removal response phrase for the current task count.
     *
     * @param itemCount Total number of tasks.
     * @return Task removal phrase string.
     */
    public static String getTaskRemovedPhrase(int itemCount) {
        return PHRASES[getStartIndex(itemCount) + 2];
    }

    /**
     * Gets the empty list phrase for the current task count.
     *
     * @param itemCount Total number of tasks.
     * @return Empty list phrase string.
     */
    public static String getEmptyListPhrase(int itemCount) {
        return PHRASES[getStartIndex(itemCount) + 3];
    }
}
