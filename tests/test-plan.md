# Place-record test plan

## Automated tests

- Verify `/help` lists task and place commands.
- Add valid places with and without optional visit dates and notes.
- Verify ratings, prices, invalid dates, empty values, duplicate flags, unknown flags, pipes, and line breaks are rejected without saving a record.
- Verify case-insensitive name-substring searches retain permanent list indices.
- Verify duplicate names are allowed.
- Verify each `editplace` field can be updated and optional fields can be cleared with `none`.
- Verify a place survives a save/load round trip, and that malformed records are skipped individually.
- Verify a missing `places.txt` file is created and loads as empty.
- Verify deletion requires matching confirmation and an unmatched confirmation changes nothing.
- Verify existing task parser, storage, and command tests continue to pass.

## Manual tests

1. Start the application with an existing `data/thomas.txt`; confirm existing tasks are still displayed and usable.
2. Add, list, find, edit, and delete several places through the GUI.
3. Request a deletion, run another command, then confirm that deletion; verify it is rejected.
4. Inspect `data/places.txt` after adding and editing records; verify it remains readable and task data is untouched.
