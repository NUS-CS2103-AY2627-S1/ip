# delete

Deletes a task from NyonBot

## Synopsis

delete \<taskname or number\>

## Example usage

```
delete sleep
```
deletes the first occurence of `sleep` (exact match) from the nyonbot list

Expected output:
```
Nyon! (I've removed [T][ ] sleep from your list)
```

---

```
delete 1
```
deletes the task at index 1, as specified by [list](https://github.com/nje14/ip/tree/master/docs/LIST.md)

Expected output:
Similar to above

## Aliases

`del`, `rm`
