/**
 * Turns the text the user typed into commands and tasks.
 * <p>
 * Parsing here is deliberately ignorant of the task list: it reports what the user
 * appears to have asked for and whether the request is well formed on its own terms.
 * Whether a request makes sense against the current list, such as whether task 7 exists,
 * is checked by the caller, which is what lets this package be tested with nothing but
 * strings.
 */
package seedu.goat.parser;
