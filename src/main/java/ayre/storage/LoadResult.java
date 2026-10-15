package ayre.storage;

import ayre.TaskList;

/**
 * A record to pass the load result of Storage::load() to Ayre.
 * Record is passed to the constructor in Ayre::Ayre().
 *
 * @param tasks The resultant TaskList from reading the save file.
 * @param loadMessage A printout of error messages compiled while reading the save file.
 */
public record LoadResult(TaskList tasks, String loadMessage) {}
