# UI Test Plan

## Program

- Working directory: .
- Compile verity.command: `./gradlew classes`
- Run verity.command: `java -cp build/classes/java/main verity.Verity`

Use Java 25. Before Test Case 1, remove any existing `data/verity.txt` and
`data/clients.txt`. Run all cases in order because later cases use data
created by earlier cases.

## Test Case 1: Create and list a client

- Aim: Verify client creation, generated IDs, full-detail listing, and empty-field display.

### Inputs

```text
client add /name Alice Tan /phone +65 9123 4567 /email alice@example.com /company Acme /preferred email
client list
bye
```

### Expected output

```text
____________________________________________________________
V   V  EEEEE  RRRR   IIIII  TTTTT  Y   Y
V   V  E      R   R    I      T     Y Y
 V V   EEEE   RRRR     I      T      Y
  V    E      R R      I      T      Y
  V    EEEEE  R  RR  IIIII    T      Y

Hello! I'm Verity.
I speak only the truth.
What can I do for you?
____________________________________________________________

____________________________________________________________
    Client added:
    ID: C001
    Name: Alice Tan
    Phone: +65 9123 4567
    Email: alice@example.com
    Address: -
    Company: Acme
    Notes: -
    Preferred contact: email
____________________________________________________________

____________________________________________________________
    Clients:

    C001
      Name: Alice Tan
      Phone: +65 9123 4567
      Email: alice@example.com
      Address: -
      Company: Acme
      Notes: -
      Preferred contact: email

    Total: 1 client.
____________________________________________________________

____________________________________________________________
    Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 2: Edit, find, and assign clients

- Aim: Verify partial edits, explicit clearing, name search, multiline notes, and multiple task clients.

### Inputs

```text
client edit C001 /phone +65 9876 5432 /notes Called at 9am\nRequested a quotation
client edit C001 /clear company
client find ali
client add /name Bob Lee /email bob@example.com
todo Prepare invoice /client C001 /client C002
client view C001
bye
```

### Expected output

```text
____________________________________________________________
V   V  EEEEE  RRRR   IIIII  TTTTT  Y   Y
V   V  E      R   R    I      T     Y Y
 V V   EEEE   RRRR     I      T      Y
  V    E      R R      I      T      Y
  V    EEEEE  R  RR  IIIII    T      Y

Hello! I'm Verity.
I speak only the truth.
What can I do for you?
____________________________________________________________

____________________________________________________________
    Client updated:
    ID: C001
    Name: Alice Tan
    Phone: +65 9876 5432
    Email: alice@example.com
    Address: -
    Company: Acme
    Notes: Called at 9am
           Requested a quotation
    Preferred contact: email
____________________________________________________________

____________________________________________________________
    Client updated:
    ID: C001
    Name: Alice Tan
    Phone: +65 9876 5432
    Email: alice@example.com
    Address: -
    Company: -
    Notes: Called at 9am
           Requested a quotation
    Preferred contact: email
____________________________________________________________

____________________________________________________________
    Matching clients:

    C001
      Name: Alice Tan
      Phone: +65 9876 5432
      Email: alice@example.com
      Address: -
      Company: -
      Notes: Called at 9am
             Requested a quotation
      Preferred contact: email

    Total: 1 client.
____________________________________________________________

____________________________________________________________
    Client added:
    ID: C002
    Name: Bob Lee
    Phone: -
    Email: bob@example.com
    Address: -
    Company: -
    Notes: -
    Preferred contact: -
____________________________________________________________

____________________________________________________________
     Got it. I've added this task:
       [T][ ] Prepare invoice
     Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________
    Client details:
    ID: C001
    Name: Alice Tan
    Phone: +65 9876 5432
    Email: alice@example.com
    Address: -
    Company: -
    Notes: Called at 9am
           Requested a quotation
    Preferred contact: email

    Assigned tasks:
      1.[T][ ] Prepare invoice
____________________________________________________________

____________________________________________________________
    Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 3: Dissociate and delete a client

- Aim: Verify final-client protection, explicit deletion confirmation, task retention, and former-client notes.

### Inputs

```text
client dissociate C001 /task 1
client dissociate C002 /task 1
client delete C002
client list
client delete C002 confirm
list
bye
```

### Expected output

```text
____________________________________________________________
V   V  EEEEE  RRRR   IIIII  TTTTT  Y   Y
V   V  E      R   R    I      T     Y Y
 V V   EEEE   RRRR     I      T      Y
  V    E      R R      I      T      Y
  V    EEEEE  R  RR  IIIII    T      Y

Hello! I'm Verity.
I speak only the truth.
What can I do for you?
____________________________________________________________

____________________________________________________________
    Client C001 was dissociated from task 1.
____________________________________________________________

____________________________________________________________
     Speak your truth. Client C002 is the last client associated with task 1.
____________________________________________________________

____________________________________________________________
    Warning: Deleting client C002 (Bob Lee) is permanent.
    The client is currently assigned to 1 task.
    Those tasks will be kept and annotated.

    Type `client delete C002 confirm` to continue.
____________________________________________________________

____________________________________________________________
    Clients:

    C001
      Name: Alice Tan
      Phone: +65 9876 5432
      Email: alice@example.com
      Address: -
      Company: -
      Notes: Called at 9am
             Requested a quotation
      Preferred contact: email

    C002
      Name: Bob Lee
      Phone: -
      Email: bob@example.com
      Address: -
      Company: -
      Notes: -
      Preferred contact: -

    Total: 2 clients.
____________________________________________________________

____________________________________________________________
    Client deleted:
      C002 Bob Lee

    Removed the client from 1 task.
    The affected tasks were kept and annotated.
____________________________________________________________

____________________________________________________________

    Here are the tasks in your list:

    1.[T][ ] Prepare invoice
        Note: Former client Bob Lee (C002) was deleted.
____________________________________________________________

____________________________________________________________
    Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 4: Reject duplicates and keep task assignment optional

- Aim: Verify duplicate contact rejection and backward-compatible unassigned task commands.

### Inputs

```text
client add /name Carol Lim /email ALICE@example.com
client add /name Carol Lim /phone +65-9876-5432
todo Read book
deadline Submit report /by 2026-09-30
event Meeting /from 2026-09-20 /to 2026-09-20
list
bye
```

### Expected output

```text
____________________________________________________________
V   V  EEEEE  RRRR   IIIII  TTTTT  Y   Y
V   V  E      R   R    I      T     Y Y
 V V   EEEE   RRRR     I      T      Y
  V    E      R R      I      T      Y
  V    EEEEE  R  RR  IIIII    T      Y

Hello! I'm Verity.
I speak only the truth.
What can I do for you?
____________________________________________________________

____________________________________________________________
     Speak your truth. A client with that email address already exists.
____________________________________________________________

____________________________________________________________
     Speak your truth. A client with that phone number already exists.
____________________________________________________________

____________________________________________________________
     Got it. I've added this task:
       [T][ ] Read book
     Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________
     Got it. I've added this task:
       [D][ ] Submit report (by: Sep 30 2026)
     Now you have 3 tasks in the list.
____________________________________________________________

____________________________________________________________
     Got it. I've added this task:
       [E][ ] Meeting (from: Sep 20 2026 to: Sep 20 2026)
     Now you have 4 tasks in the list.
____________________________________________________________

____________________________________________________________

    Here are the tasks in your list:

    1.[T][ ] Prepare invoice
        Note: Former client Bob Lee (C002) was deleted.
    2.[T][ ] Read book
    3.[D][ ] Submit report (by: Sep 30 2026)
    4.[E][ ] Meeting (from: Sep 20 2026 to: Sep 20 2026)
____________________________________________________________

____________________________________________________________
    Bye. Hope to see you again soon!
____________________________________________________________

```
