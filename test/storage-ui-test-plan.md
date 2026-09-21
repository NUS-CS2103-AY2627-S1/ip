# Storage startup UI test

Run this plan separately from the normal command sessions. Create the disposable
`build/corrupt-ui-data.txt` fixture with exactly these contents, and launch
`topaz.Topaz` with `-Dtopaz.dataFile=build/corrupt-ui-data.txt`.
Do not pass `--clean-file`: the invalid file is the test input.

```text
T | 0 | keep this task
X | 0 | broken record
```

### Test case: Report a corrupt save file without overwriting it

Aim: Show the offending line and recovery advice instead of a stack trace or a partial task list.

Input:
```text
list
bye
```

Expected output:
```text
GRONK!
 Gronk hit a snag. Invalid save data at line 2: Unable to load a saved task. Repair this line or restore a backup; the file has not been changed.
```

After the session, verify that the fixture still contains both original lines.
