/**
 * Reads and writes the task list to a file on disk.
 * <p>
 * Tasks are stored one per line in a text format that each task encodes for itself. A
 * missing file means a first run rather than an error, and a line that cannot be decoded
 * is skipped rather than aborting the load, so one damaged line cannot cost the user the
 * rest of the list.
 */
package seedu.goat.storage;
