package walle.storage;

import java.util.ArrayList;

import walle.note.Note;

/**
 * The result of loading notes from the notes file: the notes that parsed
 * successfully, and how many lines had to be skipped because their content
 * wasn't as expected.
 *
 * @param notes        the notes read from the notes file.
 * @param skippedLines the number of lines that couldn't be parsed and were skipped.
 */
public record NoteLoadResult(ArrayList<Note> notes, int skippedLines) {
}
