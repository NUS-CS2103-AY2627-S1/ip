# deadline

Creates and adds a deadline to NyonBot with a specified date

## Synopsis

deadline \<name\> --by \<date\>

date must be entered in format dd/MM/yyyy HHmm

## Example usage

```
deadline sleep --by 01/01/1900 0000
```
Adds a deadline task called `sleep` which is done by `01/01/1900 0000`

Expected output:
```
Nyon! (I've added this task: 
[D][ ] sleep (by: 01 Jan 1900 0000)
There are 1 tasks in your list)
```

## Options
`--by`
Specifies the deadline date. Not optional

