# event

Creates and adds an event to NyonBot with a specified start date and end date

## Synopsis

event \<event name\> --from \<start date\> --to \<end date\>

date must be entered in format dd/MM/yyyy HHmm

`--from` must be entered before `--to`

## Example usage

```
event sleep --from 01/01/1900 0000 --to 01/01/1900 0800
```
Adds a event task called `sleep` which lasts from `01/01/1900 0000` to `01/01/1900 0800`

Expected output:
```
Nyon! (I've added this task: 
[E][ ] sleep (from: 01 Jan 1900 0000 to: 01 Jan 1900 0800)
There are 1 tasks in your list)
```

## Options
`--from` Specifies the start date in dd/MM/yyyy HHmm format. Not optional

`--to` Specifies the end date in dd/MM/yyyy HHmm format. Not optional

