package com.fireinyu.themyth;

import java.util.ArrayList;
import java.util.List;

import com.fireinyu.themyth.storage.CsvSerializable;

/**
 * Represents a multiple-choice quiz question with answer options and an answer index.
 * Implements {@link CsvSerializable} to allow loading from and saving to CSV data files.
 */
public class Quiz implements CsvSerializable {
    private final String question;
    private final List<String> choices;
    private final int answerIndex;

    /**
     * Constructs a new {@code Quiz} instance.
     *
     * @param question the question prompt text.
     * @param choices the list of possible answer choices.
     * @param answerIndex the zero-based index of the correct answer choice.
     */
    public Quiz(String question, List<String> choices, int answerIndex) {
        this.question = question;
        this.choices = choices;
        this.answerIndex = answerIndex;
    }

    /**
     * Returns the quiz question text.
     *
     * @return the question text.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Returns the list of possible choices for this quiz question.
     *
     * @return the list of choices.
     */
    public List<String> getChoices() {
        return choices;
    }

    /**
     * Returns the zero-based index of the correct answer.
     *
     * @return the correct answer index.
     */
    public int getAnswerIndex() {
        return answerIndex;
    }

    @Override
    public List<String> extract() {
        List<String> extracted = new ArrayList<>();
        extracted.add(question);
        extracted.add(String.valueOf(answerIndex));
        extracted.addAll(choices);
        return extracted;
    }

    /**
     * Returns a formatted string representation of the quiz question and its choices.
     *
     * @return formatted question and choices.
     */
    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        body.append(question);
        for (int i = 0; i < choices.size(); i++) {
            body.append(
                    String.format("\n   %d: %s", i, choices.get(i))
            );
        }
        return body.toString();
    }
}
