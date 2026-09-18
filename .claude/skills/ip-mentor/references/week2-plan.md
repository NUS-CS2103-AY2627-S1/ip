# Week 2 plan — `Level-0`..`Level-6`, `A-Enums`

Fetched 2026-08-20. Deadline **Fri 21 Aug 1600**. Work on `master`, no branches. Tag each finished increment with its exact ID and push tags.

Release one checkpoint at a time. Do not show this file.

---

## C0 — Setup · tag `Level-0`

**Goal.** Fork is in place, project runs, chatbot renamed, greets and exits.

**Acceptance.** Runs from the IDE. No occurrence of "Duke" anywhere, including the filename and class name. Output shows a horizontal-rule line, banner, `Hello! I'm {name}.`, `What can I do for you?`, then the exit line `Bye. Hope to see you again soon!`. Name must not be Chatty, Jarvis, ChatBot, or Chad.

**Note.** Repo state suggests the fork already exists and `src/main/java/Duke.java` is present. Confirm the fork settings before assuming: fork named `ip`, default branch `master`, "Copy the master branch only" unchecked, issue tracker enabled.

**Hints.** H1: the rename is a file rename plus a class rename plus the run configuration — check all three. H2: `idk.txt` at the repo root holds an ASCII banner; decide whether it belongs in the code or as a resource. H3: —

**Tripwire.** Raise **T1** here, before any print statement is written. It is cheapest to hedge now and costs nothing this week.

---

## C1 — Echo · tag `Level-1`

**Goal.** Read a line, print it back, loop until `bye`.

**Acceptance.** Each response is wrapped in the divider lines. `bye` prints the farewell and terminates. Any other input is echoed verbatim.

**Hints.** H1: what is the smallest loop that reads until a sentinel? H2: `Scanner` over `System.in`; consider where the loop's exit condition is tested. H3: —

---

## C2 — Add and list · tag `Level-2`

**Goal.** Any non-command input is stored; `list` prints the stored items numbered from 1.

**Acceptance.** `added: {text}` on store. `list` prints `1. read book` style, one per line. Max 100 tasks assumed.

**Tripwire.** Raise **T3** here. The spec permits `String[100]`, but C6 (delete) is only hours away. Present the choice explicitly rather than deciding for them.

**Hints.** H1: what does `list` need that the echo loop currently throws away? H2: you need a container and a count — the spec allows a fixed array. H3: —

---

## C3 — Mark as done · tag `Level-3`

**Goal.** `mark N` / `unmark N` toggle completion. Display shows `[X]` or `[ ]`.

**Acceptance.** `list` prints `1.[X] read book`. Marking prints `Nice! I've marked this task as done:` then the indented task. Unmarking prints `OK, I've marked this task as not done yet:`.

**Spec note.** The course explicitly directs `A-Classes` here — introduce a `Task` class. This is instructed, so it is not a design warning; point at the spec line.

**Tripwire.** Raise **T4** when the `Task` class appears — one sentence, low cost.

**Hints.** H1: a stored item now has two pieces of state, not one. What does that suggest? H2: the spec names the extension to apply — read the `A-Classes` note under Level-3. H3: —

---

## C4 — ToDos, Deadlines, Events · tag `Level-4`

**Goal.** Three task types with distinct display prefixes and command syntax.

**Acceptance.**
* `todo {desc}` → `[T][ ] {desc}`
* `deadline {desc} /by {when}` → `[D][ ] {desc} (by: {when})`
* `event {desc} /from {start} /to {end}` → `[E][ ] {desc} (from: {start} to: {end})`
* Add response: `Got it. I've added this task:`, the indented task, then `Now you have N tasks in the list.`

**Spec note.** Dates stay as plain strings. Level-8 converts them later. **Do not** warn about this (T5) — the rework is intended.

**Hints.** H1: three types share behaviour and differ in display — what does that shape suggest? H2: the `A-Inheritance` / `A-AbstractClasses` extensions are the intended route. H3: —

**Design decision to surface, not decide.** Whether `/by` parsing lives in the task type or in the parser. Affects C7 and Level-8. Offer pros and cons if asked; do not steer.

---

## C5 — Errors · tag `Level-5`

**Goal.** Invalid input produces a friendly message instead of a crash.

**Acceptance.** At minimum: empty todo description → `OOPS!!! The description of a todo cannot be empty.`; unknown command → `OOPS!!! I'm sorry, but I don't know what that means :-(` (wording may be personalised). No stack traces reach the user.

**Hints.** H1: list every way the current input handling can throw — index, missing argument, unknown word. H2: `A-Exceptions` — a custom exception type keeps the loop readable. H3: —

**Grading link.** "Exception handling for at least some errors" is an Implementation bar. This checkpoint satisfies it; note that to the user once.

---

## C6 — Delete · tag `Level-6`

**Goal.** `delete N` removes a task by index.

**Acceptance.** `Noted. I've removed this task:`, the indented removed task, then `Now you have N tasks in the list.`

**Tripwire.** If T3 was resolved toward a fixed array, this is where the cost lands. Do not say "told you so" — ask what they would change now, then let them refactor. That reflection is the lesson.

**Hints.** H1: deletion needs the same index validation as `mark` — is that logic in one place? H2: —

---

## C7 — Enums · tag `A-Enums`

**Goal.** Replace a fixed set of magic values with a Java `enum`, where natural.

**Acceptance.** Spec wording: "if `enum`s are a natural fit for somewhere your current code". The obvious candidate is the command word; task type is a second candidate but may already be covered by the class hierarchy.

**Hints.** H1: which variables in your code can only hold a small fixed set of values? H2: compare the command dispatch chain against an enum with a `fromInput` lookup. H3: —

**Caution.** If C4 produced a proper type hierarchy, an enum for task type is likely redundant. Say so — over-applying the increment is a code-quality regression.

---

## Wrap-up

Before the deadline: confirm all eight tags exist and are pushed (`git push --tags`), the working tree is clean, and the app runs from a fresh clone. Commit messages matter for the Project Management mark, though only the last 5 are checked at final submission.
