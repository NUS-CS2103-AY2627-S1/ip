/**
 * The kinds of task the chatbot tracks.
 * <p>
 * {@link sallman.task.Task} holds what every task has, a description and
 * whether it is done, and each subclass adds what makes it different: nothing
 * for a todo, a due date for a deadline, a span for an event. Each also knows
 * how to show itself and how to write itself to the data file.
 * <p>
 * This package depends on nothing else in the app, so a task can be
 * understood without reading the rest.
 */
package sallman.task;
