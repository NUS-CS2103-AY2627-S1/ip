# UI Test Plan

Record every console UI test here. The runner compares the complete standard output exactly, except for differences in line endings and one final newline.

## Program command

```text
java -Dstdout.encoding=UTF-8 src/main/java/Oreo.java
```

## Test case format

For each test, add a heading named `## Test case: <name>`, followed by an aim in the form `- **Aim:** <purpose>`. Add `### Inputs` and `### Expected output` headings beneath it, each followed by a `text` code block. Replace all placeholder text before running the plan.

## Planned test cases

Add completed test cases below. Each test case must provide an aim, inputs, and complete expected output before it can be run.

## Test case: Create and list every task type

- **Aim:** Verify that to-dos, deadlines, and events are stored polymorphically and displayed with their type-specific details.

### Inputs

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
 ██████╗ ██████╗ ███████╗ ██████╗
██╔═══██╗██╔══██╗██╔════╝██╔═══██╗
██║   ██║██████╔╝█████╗  ██║   ██║
██║   ██║██╔══██╗██╔══╝  ██║   ██║
╚██████╔╝██║  ██║███████╗╚██████╔╝
 ╚═════╝ ╚═╝  ╚═╝╚══════╝ ╚═════╝

     ◉ ◉ ◉  O R E O  ◉ ◉ ◉
     ─────────────────────
____________________________________________________________
Hello! I'm OREO, your personal cookie-themed chatbot: crisp, creamy, and ready to help.
What can I do for you? Let's make today a little sweeter.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
 2.[D][ ] return book (by: Sunday)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
