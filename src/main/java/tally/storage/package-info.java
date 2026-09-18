/**
 * Keeping the tally on disk between runs.
 *
 * <p>{@link tally.storage.Storage} owns the layout of the data file: one line
 * per task, its fields separated by " | ". A line it cannot read is skipped and
 * named, so one damaged line does not cost the user every other task, and a copy
 * of the file is kept aside before anything writes over it.
 */
package tally.storage;
