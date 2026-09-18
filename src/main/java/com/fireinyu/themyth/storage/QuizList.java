package com.fireinyu.themyth.storage;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

import com.fireinyu.themyth.Quiz;
import com.fireinyu.themyth.exceptions.CorruptedTaskFileException;

/**
 * List of {@link Quiz} questions that can be loaded from and synced with a CSV file on disk or in resources.
 *
 * @see Quiz
 * @see CsvBackedList
 */
public class QuizList extends CsvBackedList<Quiz> {

    private final Random random = new Random(LocalDateTime.now().getNano());

    /**
     * Initializes an empty {@code QuizList}.
     */
    public QuizList() {
        super();
    }

    @Override
    protected Quiz parse(String... item) throws CorruptedTaskFileException {
        return new Quiz(item[0], Arrays.asList(item).subList(2, item.length), Integer.parseInt(item[1]));
    }

    /**
     * Selects and returns a random quiz question from the list.
     *
     * @return a randomly chosen {@link Quiz} question.
     */
    public Quiz getRandom() {
        return get(random.nextInt(0, size()));
    }

}
