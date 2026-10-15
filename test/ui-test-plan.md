# UI Test Plan — Atlas

Run with: `test/ui-test.sh` (from the repo root).

Each case has these files under `test/cases/`:

- `<name>.in` — commands fed to Atlas. Most cases end with `bye`; a case may
  deliberately omit it, since the case then proves end of input exits cleanly
- `<name>.expected` — lines that must appear in the output (substring match,
  mirroring the course grading scripts; tolerant of bubble formatting).
  Indexed list lines (e.g. `1.[T][ ] task`, `1.[C] Bob`) only occur in listing
  output (`list`, `find`, `client list`, `client find`), so they prove stored
  state rather than acknowledgement text.
- `<name>.in2` (optional) — a second session run in the same working
  directory after the first, used to test persistence across restarts.
  Expected substrings cover both runs' combined output.
- `<name>.setup/` (optional) — files copied into the working directory before
  the first run, keeping their layout, for cases that need a starting state no
  Atlas command can produce (e.g. a data file holding a corrupt record).

Each case runs in its own temporary working directory, so Level-7 data files
are isolated and every case starts with a fresh task list.

## Level-8 note

Deadline `/by` values are dates since Level-8: input `yyyy-mm-dd`, output
`MMM dd yyyy`. Non-date values are rejected with the Fates message. The data
file stores the ISO date (e.g. `D | 0 | x | 2019-10-15`); old files with
non-date `by` values are skipped as corrupted on load.

## A-Varargs note

Since A-Varargs, `find` takes any number of whitespace-separated keywords
(e.g. `find book paper`). A task matches when its description contains ANY
of the keywords (case-sensitive OR), in insertion order. Bare `find` still
shows the missing-keyword message.

## D-Clients note

The `client` command manages client records alongside tasks:

- `client add <name> [/phone <number>] [/email <address>]` — name required,
  the two markers optional and accepted in either order
- `client list` — every client, numbered from 1
- `client find <keyword> [<keyword> ...]` — a client matches when its NAME,
  phone or email contains the keyword (case-sensitive OR), in insertion order
- `client delete <number>` — removes one client

Clients and tasks are numbered independently, so deleting a client never
renumbers tasks and vice versa. `list` shows tasks only and `client list`
shows clients only.

Both entity types live in the same data file, distinguished by the first
field: `T`, `D` and `E` are tasks, `C` is a client. A client record is written
as `C | name | phone | email`, with empty phone and email fields when they are
not known; on load, 2 to 4 fields are accepted so a hand-written file without
the optional fields still loads. Records are written tasks first, then
clients. Delimiters inside any field are escaped as `\|` and `\\`, exactly as
for tasks. Unknown record types and malformed client records are skipped as
corrupted lines, keeping the rest of the file.

Unit tests cover the client list operations, the display form, the matching
rule, and the storage round trip, including hand-written records that hold
only a name, or a name and a phone.

## C-Priority note

The `priority` command attaches a level to a task:

- `priority <number> <high|medium|low>` — attaches that level
- `priority <number> none` — removes the level

The task number is validated first, then the level word, so `priority abc high`
reports the pantheon message while `priority 1 urgent` reports the Fates
message. The level word must be the only text after the number.

A ranked task shows a bracketed tag straight after the status icon, and only
when a level is set, so unranked tasks keep their previous display form:
`1.[T][ ][HIGH] buy milk`, `2.[D][X][LOW] submit report (by: Oct 15 2019)`,
`3.[E][ ][MEDIUM] project meeting (from: 2pm to: 4pm)`. Both `list` and `find`
show the tag, since both print the task's display form.

A task record gains a trailing priority field only when the task has a level
(`T | 0 | buy milk | high`, `D | 0 | submit report | 2019-10-15 | low`,
`E | 0 | project meeting | 2pm | 4pm | medium`). On load, 3 or 4 fields are
accepted for `T`, 4 or 5 for `D`, and 5 or 6 for `E`; an empty field means no
level, and an unrecognised word makes the line corrupted. Files written before
this increment load unchanged, and client records are untouched.

Unit tests cover the level words, the display form with and without a level, the
command and level parsing including every rejection message, and the storage
round trip for all three task types. `priority-errors` cannot prove from its
output alone that the rejected commands changed nothing, since the harness
cannot assert that a line is absent; the verification battery checks the data
file after that case instead. `priority-persistence` ends its first session with
a priority command, so a level that is only held in memory and never written to
disk fails that case; the battery separately checks a session whose last command
is a clear, because a cleared level can only be proven absent from the data file.
Whitespace around the number is insignificant, and a tab separates the number
from the level exactly as a space does, matching how the other commands treat
whitespace. `AtlasSessionTest` covers what the harness cannot: that ranking and
clearing each reach the data file, that a bad task number is reported before a
bad level, and that an upper-case level changes nothing.

Intentional behaviour changes introduced with these increments: the unknown-word
reply now lists `priority` and `client` among the commands. Two cases produce that reply.
`unknown-command.expected` asserts the full line, so it was updated in the
same commit. `uppercase-bye.expected` asserts only the opening words of the
message, so it needed no change; it now also asserts the `list` reply that
follows, which is what proves the session continues after an unknown word.

## Cases

| Case | Aim | Inputs | Expected |
|---|---|---|---|
| `spec-scenario` | Full week-2 flow: add all 3 task types, list, mark, exit | `todo borrow book`, `deadline return book /by 2019-10-15`, `event project meeting /from Mon 2pm /to 4pm`, `deadline do homework /by 2019-12-02`, `list`, `mark 1`, `list`, `bye` | Indexed list snapshots before and after mark; goodbye on `bye` |
| `empty-list` | `list` with no tasks | `list`, `bye` | `Your list is empty.` |
| `mark-unmark-all-types` | mark AND unmark on every task type | `todo a`, `deadline b /by 2019-12-02`, `event c /from 1pm /to 2pm`, `mark 1`, `mark 2`, `mark 3`, `list`, `unmark 1`, `unmark 2`, `unmark 3`, `list`, `bye` | All three `[X]` after mark; all three `[ ]` restored after unmark |
| `special-chars` | punctuation and multi-word fields parse | `deadline fix bug :-) /by 2019-10-15`, `event team dinner /from 7pm at marina /to 9pm at home`, `list`, `bye` | Indexed list lines with `(by: Oct 15 2019)` and `(from: 7pm at marina to: 9pm at home)` |
| `unknown-command` | bare text is rejected, not silently added | `plain task`, `bye` | Full Oracle message with command list, including `priority` and `client` |
| `uppercase-bye` | only exact `bye` exits | `BYE`, `list`, `bye` | `BYE` rejected as unknown; the `list` that follows still runs (`Your list is empty.`) |
| `empty-todo` | empty todo description rejected | `todo`, `todo `, `bye` | `Name your labour, mortal: todo <desc>` |
| `missing-by` | deadline without ` /by ` rejected | `deadline do work`, `deadline`, `bye` | Full Fates message with syntax hint |
| `missing-from` | event without ` /from ` rejected | `event party /to 10pm`, `event`, `bye` | Full Icarus-from message with syntax hint |
| `missing-to` | event without ` /to ` rejected | `event party /from 8pm`, `bye` | Full Icarus-to message with syntax hint |
| `bad-mark` | mark/unmark with non-number, zero, out-of-range, or no index | `mark abc`, `mark 0`, `mark 99`, `mark`, `unmark`, `unmark 99`, `bye` | Full pantheon messages for both commands; bare-command prompts |
| `empty-desc` | task command with marker but no description | `deadline /by Mon`, `event /from 2pm /to 3pm`, `todo`, `bye` | All three full `Name your labour` messages |
| `empty-values` | whitespace-only descriptions and empty date fields rejected | `todo  `, `deadline task /by `, `event party /from  /to end`, `event party /from start /to `, `list`, `bye` | All four full error messages; list still empty |
| `delete-case` | delete removes the task and renumbers | `todo a`, `deadline b /by 2019-12-02`, `event c /from 1pm /to 2pm`, `delete 2`, `list`, `delete 1`, `list`, `bye` | Removed task shown `[D][ ] b (by: Dec 02 2019)`; count drops; list renumbers without gaps |
| `delete-bad` | delete with non-number, zero, out-of-range, or no index | `delete abc`, `delete 0`, `delete 99`, `delete`, `bye` | Full pantheon message; bare-command prompt |
| `delete-only` | deleting the last task empties the list cleanly | `todo only`, `delete 1`, `list`, `bye` | Removed shown; `Now you have 0 tasks in the list.`; `Your list is empty.` |
| `find-case` | Level-9: find tasks by a case-sensitive description keyword | add two tasks containing `book`, add an unrelated task, `find book`, `find missing`, bare `find` | Matching tasks are listed in insertion order; no-match and missing-keyword messages are shown |
| `find-multi-keyword` | A-Varargs: find accepts several whitespace-separated keywords; any keyword matches (OR) | `todo read book`, `deadline return book /by 2019-10-15`, `todo visit museum`, `find book paper`, `find missing`, `find museum`, bare `find`, `bye` | Both `book` rows listed in order for `book paper`; no-match message; `museum` row listed alone; missing-keyword message |
| `state-recovery` | errors leave the list untouched; mark/delete after errors work | `todo keep`, `deadline due /by 2019-12-02`, `event meet /from 10am /to 11am`, `deadline broken`, `mark 99`, `delete 99`, `list`, `mark 2`, `delete 1`, `list`, `unmark 1`, `list`, `bye` | Errors do not add tasks; indexed snapshots prove keep removed, due stays marked through the delete, then unmarks |
| `large-list` | dynamic sizing and storage correctness beyond 100 tasks | 101 `todo task N` commands, `list`, `mark 101`, `delete 1`, `list`, `bye` | Indexed lines for tasks 1/100/101; 101st markable; after delete: renumbered, 101st still marked, count 100 |
| `level7-persistence` | Level-7: save on change, load on restart | run 1: add todo/deadline/event, `mark 2`, `list`, `bye`; run 2: `list`, `unmark 2`, `list`, `delete 1`, `list`, `bye` | Run 2 lists run 1's tasks loaded from disk with mark intact; unmark and delete persist and renumber |
| `level8-dates` | Level-8: deadline dates parse (yyyy-mm-dd) and print formatted (MMM dd yyyy); non-dates rejected | `deadline submit /by 2019-10-15`, `deadline newyear /by 2020-01-05`, `list`, `deadline bad /by 15/10/2019`, `list`, `bye` | Formatted lines `(by: Oct 15 2019)` and `(by: Jan 05 2020)`; full rejection message for `15/10/2019`; the two valid deadlines still listed |
| `client-add-list` | D-Clients: add clients with and without optional fields, then list them | `client add Bob`, `client add Mary Jane /phone 91234567`, `client add Chen Wei /email wei@example.com`, `client add Dara /phone 8123 4567 /email dara@example.com`, `client list`, `bye` | Echo of each added client; counts in singular and plural; indexed client lines showing phone and email only when known, and both together for a client that has both |
| `client-errors` | D-Clients: every malformed client command is rejected and adds nothing | `client`, `client foo`, `client add`, `client add /phone 91234567`, `client add Bob /phone`, `client add Bob /email`, `client add B /phone /email x@y.com` (two markers sharing one space), `client add A /email /phone 2`, `client delete`, `client delete abc`, `client delete 0`, `client delete 99`, `client find`, `client list`, `bye` | Full usage, missing-name, empty-value, missing-number, no-such-client and missing-keyword messages; client list still empty |
| `client-delete-find` | D-Clients: find matches name/phone/email; delete renumbers clients and leaves tasks untouched | `todo buy milk`, three `client add` commands, `client find x.com`, `client delete 2`, `client list`, `list`, `client delete abc`, `client find zzz`, `bye` | Matching client listed; removed client shown; clients renumbered; task still `1.[T][ ] buy milk`; invalid delete rejected; no-match message shown |
| `client-persistence` | D-Clients: clients and tasks share one file and survive a restart, including escaped delimiters | run 1: two `client add` commands (one with `|` and `\` in the fields), `todo buy milk`, `bye`; run 2: `client list`, `list`, `client delete 1`, `client list`, `bye` | Run 2 loads both entity types from disk; escaped characters round-trip; deleting a client keeps the task and renumbers the rest |

| `priority-set-list` | C-Priority: rank all three task types, list, find, clear one level, re-rank another | `todo buy milk`, `deadline submit report /by 2019-10-15`, `event project meeting /from 2pm /to 4pm`, `priority 1 high`, `priority 2 low`, `priority 3 medium`, `list`, `find milk`, `priority 3 none`, `list`, `priority 2 high`, `list`, `bye` | Ranked echoes with `[HIGH]`, `[LOW]` and `[MEDIUM]`; indexed list and find rows carrying the tags; the cleared row back to `3.[E][ ]`; the re-ranked row `2.[D][ ][HIGH]` |
| `priority-errors` | C-Priority: every malformed priority command is rejected and changes nothing | bare `priority`, `priority 0 high`, `priority abc high`, `todo only task`, `priority 1`, `priority 1 ` (trailing space), `priority 1 urgent`, `priority 1 high extra`, `priority 1 none`, `priority high 1`, `priority 2 low`, `list`, `bye` | Full missing-number, pantheon, missing-level and Fates messages; the cleared-rank reply for `none`; `1.[T][ ] only task` still unranked after every rejection |
| `priority-persistence` | C-Priority: levels survive a restart with tasks and clients in one file, including a level set as the last command of a session | run 1: add todo/deadline/event, `priority 1 high`, `priority 3 low`, `client add Bob /phone 91234567`, `priority 2 medium`, `bye`; run 2: `list`, `client list`, `priority 1 none`, `list`, `bye` | Run 2 lists run 1's levels loaded from disk (`[HIGH]`, `[MEDIUM]`, `[LOW]`) and the client alongside them, so a level kept only in memory fails the case; then the cleared task is back to `1.[T][ ] buy milk` |
| `event-marker-overlap` | A malformed event whose markers share one space is reported, not fatal | `event ritual /from /to 4pm`, `event /from /to 4pm`, `list`, `bye` | Full Icarus-from message for the missing start; full `Name your labour` message for the missing description; the list is still empty |
| `event-inverted-range` | A dated event whose end falls before its start is rejected on both sides of the comparison, in every date and clock form Atlas reads | `event x /from 2026-12-01 /to 2026-11-01`, `event y /from 2026-12-01 1400 /to 2026-11-01 1400`, `event z /from 2026-12-01 1400 /to 2026-12-01 1000`, `event w /from 2026-12-01 1400 /to 2026-12-01 1400`, `event v /from 18/9/2026 1000am /to 17/9/2026 1000am`, `event u /from 1-12-2026 1400 /to 1-12-2026 1300`, `event q /from 18/9/2026 1000am /to 18/9/2026 1000am`, `event r /from 18/9/2026 1000am /to 18/9/2026 0900am`, `list`, `bye` | The full ends-before-it-begins message for every earlier end date, earlier hour and earlier same-day hour; the begins-and-ends-together message for the equal hours; the list still empty |
| `event-dated-order-ok` | Every range Atlas should still accept after the ordered-range rule is accepted | `event same-day later hour /from 2026-12-01 1400 /to 2026-12-01 1600`, `event day-first same day /from 18/9/2026 1000am /to 18/9/2026 1200pm`, `event past midnight /from 2026-12-01 2300 /to 2026-12-02 0100`, `event same date no hours /from 2026-12-01 /to 2026-12-01`, `event clock only /from 2pm /to 1pm`, `event free text /from 7pm at marina /to later that night`, `list`, `bye` | All six listed in order with their values unchanged, which proves the new rule rejects none of a forwards date range, a day-first same-day range, a cross-midnight range, a date pair without hours, a clock-only past-midnight pair or free text |
| `event-compact-meridiem` | A compact 12-hour clock beside a date is read, so an earlier start hour is accepted | `event earlier /from 18/9/2026 1000am /to 18/9/2026 1230pm`, `list`, `bye` | The event listed with the values as typed, which proves `1000am` reads as 10:00 and `1230pm` as 12:30 |
| `event-impossible-date` | A date-shaped value that names no real day is rejected, and the calendar including leap years is honoured | `event badday /from 32/9/2026 /to 1/10/2026`, `event badfeb /from 31/2/2026 /to 1/3/2026`, `event badiso /from 2026-02-30 /to 2026-03-01`, `event badmonth /from 13/13/2026 /to 1/1/2027`, `event leap /from 29/2/2024 /to 1/3/2024`, `event noleap /from 29/2/2026 /to 1/3/2026`, `list`, `bye` | The full no-such-date message for each impossible date, naming it; only the leap-day event stored, listed as `1.[E][ ] leap (from: 29/2/2024 to: 1/3/2024)` |
| `duplicate-markers` | A marker repeated in one command is rejected, not swallowed into a value | `todo seed`, `event a /from 1pm /from 2pm /to 3pm`, `event b /from 1pm /to 2pm /to 3pm`, `deadline d /by 2026-09-01 /by 2026-09-02`, `client add Bob /phone 1 /phone 2`, `client add Ann /email a@b.com /email c@d.com`, `list`, `client list`, `bye` | The five full duplicate messages; only the seeded task exists; the client list is still empty |
| `whitespace-slack` | Leading whitespace is insignificant, and `bye` with surrounding spaces exits | ` todo padded` (indented), `  mark 1`, `  list`, ` bye` | `1.[T][X] padded`, which proves the indented commands ran and the indented `bye` exited |
| `no-bye-exit` | End of input exits cleanly with the goodbye and exit code 0 | `todo survive`, `list` (no `bye` line at all) | `1.[T][ ] survive` and the goodbye, with the session exiting 0 instead of crashing |
| `corrupt-data-file` | A record Atlas cannot parse is reported, left out, and the file copied aside before the overwrite | run 1, started from `corrupt-data-file.setup/` holding one good record and one unparsable line: `list`, `todo survivor`, `bye`; run 2: `list`, `bye` | The summary warning, `Line 2: too few fields`, the `.corrupted-` copy name in the warning, the good record kept, the new task added, and both tasks present in run 2, which proves the save after the backup worked |

## A-MoreErrorHandling note

Atlas now survives and explains every malformed command and unreadable data file
it can meet in normal use:

- The ` /from ` and ` /to ` markers can share one space, as in
  `event x /from /to y`. Ending the start value at that shared space ran the
  slice backwards and killed the process with `StringIndexOutOfBoundsException`;
  the value now ends inside the marker at the earliest, so the missing start is
  reported as the ordinary Icarus message.
- A marker repeated in one command is rejected instead of being absorbed into
  the previous value, which used to store `2026-09-01 /by 2026-09-02` as a date
  field. This applies to `/from`, `/to`, `/by`, `/phone` and `/email`.
- An event whose start and end name the same moment is rejected. Both values
  must be plain clock times (`2pm`, `10am`, `2:30pm`, `1000am`, `14:00`, `0900`) for the check to
  apply, because free text such as `7pm at marina` cannot be ordered. A later
  end is accepted when no date is given, since an event may run past midnight.
- A dated event whose end falls before its start is rejected: two values that
  both start with a date, as `2026-12-01`, `1/12/2026` or `1-12-2026`, are
  ordered by date, and two on the same date by the hour each one carries.
  `2026-12-01` to `2026-11-01` and `18/9/2026 1000am` to `17/9/2026 1000am` are
  no longer stored as ranges that run backwards, while a forwards range, a
  cross-midnight pair written with the next day's date, a same-date pair without
  hours, and free text are all still accepted.
- A value shaped like a date that names no real day is rejected instead of being
  kept as free text, so `31/2/2026`, `32/9/2026`, `13/13/2026` and `2026-02-30`
  fail while `29/2/2024` passes, because the calendar, leap years included,
  decides.
- End of input (Ctrl+D, or a script that runs out of lines) exits with the
  goodbye instead of throwing `NoSuchElementException`. `bye ` with a trailing
  space and an indented command both work now, because the command is matched
  after its leading whitespace is dropped.
- Errors are reported through `Output.speakError`, which the console `Ui` draws
  in a bubble bordered with `!` marks, so a problem is told apart from an
  ordinary reply at a glance. The error text itself is byte-identical to before.
- A data file that cannot be read, or that holds records Atlas cannot parse, is
  reported through that same sink rather than printed to the console, where the
  GUI never showed it. The original file is copied to
  `<file>.corrupted-<timestamp>` before the next save overwrites it, and the
  save is abandoned if that copy cannot be written.

The only behaviour change among the earlier cases is cosmetic: every case whose
output contains an error message now shows those bubbles with `!` borders. The
message lines are unchanged, and the data files written by all previous cases
are byte-identical.

## Harness limitation

The harness only checks that each expected line is present; it cannot check
that an unwanted line is absent. A case therefore cannot, by itself, prove
that a rejected command left the stored state unchanged. Where that proof
matters, the case has to add a follow-up command whose reply differs when the
state is wrong (for example deleting the row that should not exist and
expecting the not-found message).

The harness also cannot read the data file or the directory around it, so the
copy that `A-MoreErrorHandling` makes of an unreadable file is proven by
`StorageTest` rather than by a case; a case can only assert that the warning
names the copy. The other proofs the harness cannot express live in
`AtlasSessionTest` and `StorageTest`: that a rejected command leaves the file
untouched, that errors arrive through `Output.speakError` rather than
`Output.speak`, and that a failed backup abandons the save instead of
destroying the file.

## Maintenance rule

After every code update: re-run `test/ui-test.sh`. If behaviour changed
intentionally, update this plan and the case files in the same commit as the
code change. If a case fails, fix the code first — the tests are the contract.
