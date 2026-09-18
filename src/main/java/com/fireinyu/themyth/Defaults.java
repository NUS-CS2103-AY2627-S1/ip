package com.fireinyu.themyth;

import java.time.LocalDateTime;

import com.fireinyu.themyth.chatmodes.ChatMode;
import com.fireinyu.themyth.chatmodes.QuizMode;
import com.fireinyu.themyth.tasks.TaskOrder;
import com.fireinyu.themyth.util.MythDateTime;

/**
 * Default values for the app.
 * Uses LocalDateTime internally.
 *
 * @see MythDateTime
 * @see LocalDateTime
 */
public class Defaults {
    /** Default path to the task file. */
    public static final String TASKFILE = "resources/tasks.csv";
    /** Default path to the quiz questions file. */
    public static final String QUIZFILE = "data/questions.csv";
    /** Maximum line width used by the command-line interface. */
    public static final int LINEWIDTH = 100;
    /** Chat mode used when the application starts. */
    public static final ChatMode STARTMODE = new QuizMode();
    /** Prompt displayed for user input. */
    public static final String USERPROMPT = ">>> ";
    /** Prompt used for normal responses. */
    public static final String BOTPROMPT = "The Myth declares, honey 💅✨: ";
    /** Prompt used for recoverable errors. */
    public static final String TWEAKPROMPT = "The Myth is clutching pearls 🙄💅: ";
    /** Prompt used for fatal errors. */
    public static final String DEATHPROMPT =
            "The Myth dramatically faints into the abyss! The rhinestone tombstone reads 🪦✨: ";
    /** Format used to serialize date-time values. */
    public static final String DATE_INPUTFORMAT = "yyyy-MM-dd-HH-mm-ss";
    /** Default ordering applied to tasks. */
    public static final TaskOrder TASK_ORDER = TaskOrder.MODIFIED;
}
