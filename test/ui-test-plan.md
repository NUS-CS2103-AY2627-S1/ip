# Console UI Test Plan

The runner compiles every Java source under `src/main/java`, starts a fresh `crystal.Crystal` process in an isolated working directory for each test case, and checks each response before sending the next command. It supports case-specific starting files and compares expected saved files exactly without touching the developer's real data file. Console output is compared exactly after normalizing line endings. The shared `LINE` variable represents the application's horizontal divider.

Each test case below specifies its aim, command inputs, and expected output. An exchange with `expect_exit` must cause the program to terminate successfully.

```json
{
  "prompt": "You: ",
  "timeout_seconds": 5,
  "variables": {
    "LINE": "____________________________________________________________"
  },
  "program": {
    "source_dir": "src/main/java",
    "classes_dir": "_temp/ui-test-classes",
    "working_dir": "_temp/ui-test-work",
    "main_class": "crystal.Crystal"
  },
  "test_cases": [
    {
      "id": "UI-01",
      "aim": "Add todo, deadline, and event tasks and list their type-specific details",
      "exchanges": [
        {
          "input": "todo read book",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [T][ ] read book",
            "         Now you have 1 task in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "deadline do homework /by no idea :-p",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [D][ ] do homework (by: no idea :-p)",
            "         Now you have 2 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "event project meeting /from Mon 2pm /to 4pm",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [E][ ] project meeting (from: Mon 1400 to: 1600)",
            "         Now you have 3 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "list",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Ta-da!!! Here are all your tasks:",
            "         1.[T][ ] read book",
            "         2.[D][ ] do homework (by: no idea :-p)",
            "         3.[E][ ] project meeting (from: Mon 1400 to: 1600)",
            "{{LINE}}"
          ]
        },
        {
          "input": "bye",
          "expect_exit": true,
          "expected_output": [
            "{{LINE}}",
            "Crystal: Byeee!!! You did amazing today. See you soon!",
            "{{LINE}}"
          ]
        }
      ]
    },
    {
      "id": "UI-02",
      "aim": "Mark and unmark a task while guarding against repeated status changes",
      "exchanges": [
        {
          "input": "todo read book",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [T][ ] read book",
            "         Now you have 1 task in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "mark 1",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Yayyy!!! You finished this task:",
            "         [T][X] read book",
            "{{LINE}}"
          ]
        },
        {
          "input": "mark 1",
          "expected_output": [
            "{{LINE}}",
            "Crystal: This task is already done, good job!",
            "         [T][X] read book",
            "{{LINE}}"
          ]
        },
        {
          "input": "unmark 1",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! I've put this task back on your list:",
            "         [T][ ] read book",
            "{{LINE}}"
          ]
        },
        {
          "input": "unmark 1",
          "expected_output": [
            "{{LINE}}",
            "Crystal: This task is already waiting for you!",
            "         [T][ ] read book",
            "{{LINE}}"
          ]
        },
        {
          "input": "bye",
          "expect_exit": true,
          "expected_output": [
            "{{LINE}}",
            "Crystal: Byeee!!! You did amazing today. See you soon!",
            "{{LINE}}"
          ]
        }
      ]
    },
    {
      "id": "UI-03",
      "aim": "Reject commands that do not begin with a recognized task type",
      "exchanges": [
        {
          "input": "read book",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! I don't know what that means :-(",
            "{{LINE}}"
          ]
        },
        {
          "input": "list",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Your task list is all clear, well done!",
            "{{LINE}}"
          ]
        },
        {
          "input": "bye",
          "expect_exit": true,
          "expected_output": [
            "{{LINE}}",
            "Crystal: Byeee!!! You did amazing today. See you soon!",
            "{{LINE}}"
          ]
        }
      ]
    },
    {
      "id": "UI-04",
      "aim": "Report empty, padded, malformed, unsafe, and out-of-range commands as Crystal exceptions",
      "exchanges": [
        {
          "input": "",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! I cant reply to nothing...",
            "{{LINE}}"
          ]
        },
        {
          "input": " list",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! Please remove spaces from the start or end of your command!",
            "{{LINE}}"
          ]
        },
        {
          "input": "list ",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! Please remove spaces from the start or end of your command!",
            "{{LINE}}"
          ]
        },
        {
          "input": "todo",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! A todo must have a description!",
            "{{LINE}}"
          ]
        },
        {
          "input": "deadline",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! A deadline must have a description and a /by time!",
            "{{LINE}}"
          ]
        },
        {
          "input": "event",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! An event must have a description, a /from time and a /to time!",
            "{{LINE}}"
          ]
        },
        {
          "input": "deadline report /by Friday /by Saturday",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! A deadline must have a description and a /by time!",
            "{{LINE}}"
          ]
        },
        {
          "input": "event meeting /from Monday /from Tuesday /to Wednesday",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! An event must have a description, a /from time and a /to time!",
            "{{LINE}}"
          ]
        },
        {
          "input": "todo unsafe | task",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! Task details cannot contain ' | '!",
            "{{LINE}}"
          ]
        },
        {
          "input": "mark",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! You have to mark a task number!",
            "{{LINE}}"
          ]
        },
        {
          "input": "mark abc",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! You have to mark a task number!",
            "{{LINE}}"
          ]
        },
        {
          "input": "unmark",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! You have to unmark a task number!",
            "{{LINE}}"
          ]
        },
        {
          "input": "delete abc",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! You have to delete a task number!",
            "{{LINE}}"
          ]
        },
        {
          "input": "mark 1",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! That task number does not exist!",
            "{{LINE}}"
          ]
        },
        {
          "input": "list",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Your task list is all clear, well done!",
            "{{LINE}}"
          ]
        },
        {
          "input": "bye",
          "expect_exit": true,
          "expected_output": [
            "{{LINE}}",
            "Crystal: Byeee!!! You did amazing today. See you soon!",
            "{{LINE}}"
          ]
        }
      ]
    },
    {
      "id": "UI-05",
      "aim": "Delete a task and shift the remaining tasks forward in the list",
      "exchanges": [
        {
          "input": "todo read book",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [T][ ] read book",
            "         Now you have 1 task in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "event project meeting /from Aug 6th 2pm /to 4pm",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [E][ ] project meeting (from: Aug 6th 1400 to: 1600)",
            "         Now you have 2 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "todo borrow book",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [T][ ] borrow book",
            "         Now you have 3 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "delete 2",
          "expected_output": [
            "{{LINE}}",
            "Crystal: All gone!!! I've removed this task:",
            "         [E][ ] project meeting (from: Aug 6th 1400 to: 1600)",
            "         Now you have 2 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "list",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Ta-da!!! Here are all your tasks:",
            "         1.[T][ ] read book",
            "         2.[T][ ] borrow book",
            "{{LINE}}"
          ]
        },
        {
          "input": "bye",
          "expect_exit": true,
          "expected_output": [
            "{{LINE}}",
            "Crystal: Byeee!!! You did amazing today. See you soon!",
            "{{LINE}}"
          ]
        }
      ]
    },
    {
      "id": "UI-06",
      "aim": "Exercise every task format and mutation that must be reflected in the saved data file",
      "exchanges": [
        {
          "input": "todo read book",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [T][ ] read book",
            "         Now you have 1 task in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "deadline return book /by June 6th",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [D][ ] return book (by: June 6th)",
            "         Now you have 2 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "event project meeting /from Aug 6th 2pm /to 4pm",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [E][ ] project meeting (from: Aug 6th 1400 to: 1600)",
            "         Now you have 3 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "mark 2",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Yayyy!!! You finished this task:",
            "         [D][X] return book (by: June 6th)",
            "{{LINE}}"
          ]
        },
        {
          "input": "todo join sports club",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [T][ ] join sports club",
            "         Now you have 4 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "delete 4",
          "expected_output": [
            "{{LINE}}",
            "Crystal: All gone!!! I've removed this task:",
            "         [T][ ] join sports club",
            "         Now you have 3 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "bye",
          "expect_exit": true,
          "expected_output": [
            "{{LINE}}",
            "Crystal: Byeee!!! You did amazing today. See you soon!",
            "{{LINE}}"
          ]
        }
      ],
      "expected_files": {
        "data/crystal.txt": [
          "T | 0 | read book",
          "D | 1 | return book | June 6th",
          "E | 0 | project meeting | Aug 6th 1400 | 1600"
        ]
      }
    },
    {
      "id": "UI-07",
      "aim": "Load saved tasks on restart and persist every mutation against that existing list",
      "initial_files": {
        "data/crystal.txt": [
          "T | 0 | read book",
          "D | 1 | return book | 02 Dec 2019 1800",
          "E | 0 | project meeting | Monday 0600 | 1830"
        ]
      },
      "exchanges": [
        {
          "input": "list",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Ta-da!!! Here are all your tasks:",
            "         1.[T][ ] read book",
            "         2.[D][X] return book (by: 02 Dec 2019 1800)",
            "         3.[E][ ] project meeting (from: Monday 0600 to: 1830)",
            "{{LINE}}"
          ]
        },
        {
          "input": "mark 1",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Yayyy!!! You finished this task:",
            "         [T][X] read book",
            "{{LINE}}"
          ]
        },
        {
          "input": "unmark 2",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! I've put this task back on your list:",
            "         [D][ ] return book (by: 02 Dec 2019 1800)",
            "{{LINE}}"
          ]
        },
        {
          "input": "todo join sports club",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [T][ ] join sports club",
            "         Now you have 4 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "delete 3",
          "expected_output": [
            "{{LINE}}",
            "Crystal: All gone!!! I've removed this task:",
            "         [E][ ] project meeting (from: Monday 0600 to: 1830)",
            "         Now you have 3 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "list",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Ta-da!!! Here are all your tasks:",
            "         1.[T][X] read book",
            "         2.[D][ ] return book (by: 02 Dec 2019 1800)",
            "         3.[T][ ] join sports club",
            "{{LINE}}"
          ]
        },
        {
          "input": "bye",
          "expect_exit": true,
          "expected_output": [
            "{{LINE}}",
            "Crystal: Byeee!!! You did amazing today. See you soon!",
            "{{LINE}}"
          ]
        }
      ],
      "expected_files": {
        "data/crystal.txt": [
          "T | 1 | read book",
          "D | 0 | return book | 02 Dec 2019 1800",
          "T | 0 | join sports club"
        ]
      }
    },
    {
      "id": "UI-08",
      "aim": "Report corrupted saved data and continue with a safe empty task list",
      "initial_files": {
        "data/crystal.txt": [
          "this is not valid task data"
        ]
      },
      "expected_startup_output": [
        "{{LINE}}",
        "  ____ ______   ______ _____  _    _",
        " / ___|  _ \\ \\ / / ___|_   _|/ \\  | |",
        "| |   | |_) \\ V /\\___ \\ | | / _ \\ | |",
        "| |___|  _ < | |  ___) || |/ ___ \\| |___",
        " \\____|_| \\_\\|_| |____/ |_/_/   \\_\\_____|",
        "",
        "Hiii!!! I'm Crystal, your sparkly task bestie!",
        "[Crystal's command guide:",
        "- To add a todo, enter 'todo [description]'",
        "- To add a deadline, enter 'deadline [description] /by [deadline]'",
        "- To add an event, enter 'event [description] /from [start] /to [end]'",
        "- To add a within-period task, enter 'within [description] /from [start] /to [end]'",
        "- To view your task list, enter 'list'",
        "- To view dated tasks on a date, enter 'list /on [date]'",
        "- To find tasks by description, enter 'find [keyword]'",
        "- To mark a task as done, enter 'mark [task number]'",
        "- To mark a task as not done, enter 'unmark [task number]'",
        "- To delete a task, enter 'delete [task number]'",
        "- To view this command guide, enter 'help'",
        "- To exit, enter 'bye']",
        "{{LINE}}",
        "Crystal: Oopsies!!! Your saved task data is invalid."
      ],
      "exchanges": [
        {
          "input": "list",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Your task list is all clear, well done!",
            "{{LINE}}"
          ]
        },
        {
          "input": "bye",
          "expect_exit": true,
          "expected_output": [
            "{{LINE}}",
            "Crystal: Byeee!!! You did amazing today. See you soon!",
            "{{LINE}}"
          ]
        }
      ],
      "expected_files": {
        "data/crystal.txt": [
          "this is not valid task data"
        ]
      }
    },
    {
      "id": "UI-09",
      "aim": "Parse and normalize supported calendar dates and 12-hour and 24-hour times",
      "exchanges": [
        {
          "input": "deadline return book /by 2/12/2019 1800",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [D][ ] return book (by: 02 Dec 2019 1800)",
            "         Now you have 1 task in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "deadline submit report /by 2019-10-15",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [D][ ] submit report (by: 15 Oct 2019)",
            "         Now you have 2 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "event breakfast /from 6am /to 6.30pm",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [E][ ] breakfast (from: 0600 to: 1830)",
            "         Now you have 3 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "event workshop /from 630pm /to Monday",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [E][ ] workshop (from: 1830 to: Monday)",
            "         Now you have 4 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "deadline call client /by 18:45",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [D][ ] call client (by: 1845)",
            "         Now you have 5 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "list",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Ta-da!!! Here are all your tasks:",
            "         1.[D][ ] return book (by: 02 Dec 2019 1800)",
            "         2.[D][ ] submit report (by: 15 Oct 2019)",
            "         3.[E][ ] breakfast (from: 0600 to: 1830)",
            "         4.[E][ ] workshop (from: 1830 to: Monday)",
            "         5.[D][ ] call client (by: 1845)",
            "{{LINE}}"
          ]
        },
        {
          "input": "bye",
          "expect_exit": true,
          "expected_output": [
            "{{LINE}}",
            "Crystal: Byeee!!! You did amazing today. See you soon!",
            "{{LINE}}"
          ]
        }
      ],
      "expected_files": {
        "data/crystal.txt": [
          "D | 0 | return book | 02 Dec 2019 1800",
          "D | 0 | submit report | 15 Oct 2019",
          "E | 0 | breakfast | 0600 | 1830",
          "E | 0 | workshop | 1830 | Monday",
          "D | 0 | call client | 1845"
        ]
      }
    },
    {
      "id": "UI-10",
      "aim": "Parse month-name dates despite missing spaces and common separator variations",
      "exchanges": [
        {
          "input": "deadline first release /by 2Oct2026",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [D][ ] first release (by: 02 Oct 2026)",
            "         Now you have 1 task in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "deadline second release /by 2 Dec2026",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [D][ ] second release (by: 02 Dec 2026)",
            "         Now you have 2 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "deadline third release /by 2Nov 2026",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [D][ ] third release (by: 02 Nov 2026)",
            "         Now you have 3 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "event launch /from 2nd-Oct-2026 6am /to 3 October 2026 18:30",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [E][ ] launch (from: 02 Oct 2026 0600 to: 03 Oct 2026 1830)",
            "         Now you have 4 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "bye",
          "expect_exit": true,
          "expected_output": [
            "{{LINE}}",
            "Crystal: Byeee!!! You did amazing today. See you soon!",
            "{{LINE}}"
          ]
        }
      ],
      "expected_files": {
        "data/crystal.txt": [
          "D | 0 | first release | 02 Oct 2026",
          "D | 0 | second release | 02 Dec 2026",
          "D | 0 | third release | 02 Nov 2026",
          "E | 0 | launch | 02 Oct 2026 0600 | 03 Oct 2026 1830"
        ]
      }
    },
    {
      "id": "UI-11",
      "aim": "Expand two-digit years to the 2000s in numeric and month-name dates",
      "exchanges": [
        {
          "input": "deadline numeric date /by 2/12/26",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [D][ ] numeric date (by: 02 Dec 2026)",
            "         Now you have 1 task in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "deadline compact date /by 2Oct26",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [D][ ] compact date (by: 02 Oct 2026)",
            "         Now you have 2 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "deadline partial spacing /by 2 Dec26 630pm",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [D][ ] partial spacing (by: 02 Dec 2026 1830)",
            "         Now you have 3 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "deadline spaced date /by 2Nov 26",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [D][ ] spaced date (by: 02 Nov 2026)",
            "         Now you have 4 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "bye",
          "expect_exit": true,
          "expected_output": [
            "{{LINE}}",
            "Crystal: Byeee!!! You did amazing today. See you soon!",
            "{{LINE}}"
          ]
        }
      ],
      "expected_files": {
        "data/crystal.txt": [
          "D | 0 | numeric date | 02 Dec 2026",
          "D | 0 | compact date | 02 Oct 2026",
          "D | 0 | partial spacing | 02 Dec 2026 1830",
          "D | 0 | spaced date | 02 Nov 2026"
        ]
      }
    },
    {
      "id": "UI-12",
      "aim": "List unnumbered dated tasks on a date without mutating their stored indexes",
      "initial_files": {
        "data/crystal.txt": [
          "T | 0 | mention 02 Dec 2026",
          "D | 0 | submit report | 02 Dec 2026 0900",
          "D | 0 | later deadline | 03 Dec 2026",
          "E | 0 | workshop | 02 Dec 2026 0800 | 02 Dec 2026 1000",
          "E | 0 | conference | 01 Dec 2026 | 03 Dec 2026",
          "E | 0 | overnight trip | 01 Dec 2026 | 02 Dec 2026",
          "W | 0 | collect certificate | 01 Dec 2026 | 03 Dec 2026",
          "E | 0 | weekly call | Monday 0600 | Tuesday 0700"
        ]
      },
      "exchanges": [
        {
          "input": "list /on 2Dec26",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Yay, here are your dated tasks on 02 Dec 2026:",
            "         - [D][ ] submit report (by: 02 Dec 2026 0900)",
            "         - [E][ ] workshop (from: 02 Dec 2026 0800 to: 02 Dec 2026 1000)",
            "         - [E][ ] conference (from: 01 Dec 2026 to: 03 Dec 2026)",
            "         - [E][ ] overnight trip (from: 01 Dec 2026 to: 02 Dec 2026)",
            "         - [W][ ] collect certificate (from: 01 Dec 2026 to: 03 Dec 2026)",
            "{{LINE}}"
          ]
        },
        {
          "input": "list /on 4/12/26",
          "expected_output": [
            "{{LINE}}",
            "Crystal: No dated tasks on 04 Dec 2026, yay!",
            "{{LINE}}"
          ]
        },
        {
          "input": "list /on",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! To list tasks on a date, enter 'list /on [date]'!",
            "{{LINE}}"
          ]
        },
        {
          "input": "list /on nonsense",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! I couldn't understand that date!",
            "{{LINE}}"
          ]
        },
        {
          "input": "list /on 31/02/26",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! I couldn't understand that date!",
            "{{LINE}}"
          ]
        },
        {
          "input": "list",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Ta-da!!! Here are all your tasks:",
            "         1.[T][ ] mention 02 Dec 2026",
            "         2.[D][ ] submit report (by: 02 Dec 2026 0900)",
            "         3.[D][ ] later deadline (by: 03 Dec 2026)",
            "         4.[E][ ] workshop (from: 02 Dec 2026 0800 to: 02 Dec 2026 1000)",
            "         5.[E][ ] conference (from: 01 Dec 2026 to: 03 Dec 2026)",
            "         6.[E][ ] overnight trip (from: 01 Dec 2026 to: 02 Dec 2026)",
            "         7.[W][ ] collect certificate (from: 01 Dec 2026 to: 03 Dec 2026)",
            "         8.[E][ ] weekly call (from: Monday 0600 to: Tuesday 0700)",
            "{{LINE}}"
          ]
        },
        {
          "input": "bye",
          "expect_exit": true,
          "expected_output": [
            "{{LINE}}",
            "Crystal: Byeee!!! You did amazing today. See you soon!",
            "{{LINE}}"
          ]
        }
      ],
      "expected_files": {
        "data/crystal.txt": [
          "T | 0 | mention 02 Dec 2026",
          "D | 0 | submit report | 02 Dec 2026 0900",
          "D | 0 | later deadline | 03 Dec 2026",
          "E | 0 | workshop | 02 Dec 2026 0800 | 02 Dec 2026 1000",
          "E | 0 | conference | 01 Dec 2026 | 03 Dec 2026",
          "E | 0 | overnight trip | 01 Dec 2026 | 02 Dec 2026",
          "W | 0 | collect certificate | 01 Dec 2026 | 03 Dec 2026",
          "E | 0 | weekly call | Monday 0600 | Tuesday 0700"
        ]
      }
    },
    {
      "id": "UI-13",
      "aim": "Find tasks by a case-insensitive keyword in their descriptions without changing stored tasks",
      "initial_files": {
        "data/crystal.txt": [
          "T | 1 | read book",
          "D | 1 | return book | June 6th",
          "E | 0 | project meeting | book | Friday"
        ]
      },
      "exchanges": [
        {
          "input": "find   BOOK",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Yay, I found these matching tasks:",
            "         1.[T][X] read book",
            "         2.[D][X] return book (by: June 6th)",
            "{{LINE}}"
          ]
        },
        {
          "input": "find homework",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Aww, I couldn't find any matching tasks!",
            "{{LINE}}"
          ]
        },
        {
          "input": "find",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Oopsies!!! To find tasks, enter 'find [keyword]'!",
            "{{LINE}}"
          ]
        },
        {
          "input": "list",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Ta-da!!! Here are all your tasks:",
            "         1.[T][X] read book",
            "         2.[D][X] return book (by: June 6th)",
            "         3.[E][ ] project meeting (from: book to: Friday)",
            "{{LINE}}"
          ]
        },
        {
          "input": "bye",
          "expect_exit": true,
          "expected_output": [
            "{{LINE}}",
            "Crystal: Byeee!!! You did amazing today. See you soon!",
            "{{LINE}}"
          ]
        }
      ],
      "expected_files": {
        "data/crystal.txt": [
          "T | 1 | read book",
          "D | 1 | return book | June 6th",
          "E | 0 | project meeting | book | Friday"
        ]
      }
    },
    {
      "id": "UI-14",
      "aim": "Display the complete command guide when the user enters help",
      "exchanges": [
        {
          "input": "help",
          "expected_output": [
            "{{LINE}}",
            "[Crystal's command guide:",
            "- To add a todo, enter 'todo [description]'",
            "- To add a deadline, enter 'deadline [description] /by [deadline]'",
            "- To add an event, enter 'event [description] /from [start] /to [end]'",
            "- To add a within-period task, enter 'within [description] /from [start] /to [end]'",
            "- To view your task list, enter 'list'",
            "- To view dated tasks on a date, enter 'list /on [date]'",
            "- To find tasks by description, enter 'find [keyword]'",
            "- To mark a task as done, enter 'mark [task number]'",
            "- To mark a task as not done, enter 'unmark [task number]'",
            "- To delete a task, enter 'delete [task number]'",
            "- To view this command guide, enter 'help'",
            "- To exit, enter 'bye']",
            "{{LINE}}"
          ]
        },
        {
          "input": "bye",
          "expect_exit": true,
          "expected_output": [
            "{{LINE}}",
            "Crystal: Byeee!!! You did amazing today. See you soon!",
            "{{LINE}}"
          ]
        }
      ]
    },
    {
      "id": "UI-15",
      "aim": "Add, normalize, persist, and list tasks with flexible completion periods",
      "exchanges": [
        {
          "input": "within collect certificate /from 15Jan27 /to 25 January 2027",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [W][ ] collect certificate (from: 15 Jan 2027 to: 25 Jan 2027)",
            "         Now you have 1 task in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "list /on 20Jan27",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Yay, here are your dated tasks on 20 Jan 2027:",
            "         - [W][ ] collect certificate (from: 15 Jan 2027 to: 25 Jan 2027)",
            "{{LINE}}"
          ]
        },
        {
          "input": "within reversed period /from 25Jan27 /to 15Jan27",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [W][ ] reversed period (from: 25 Jan 2027 to: 15 Jan 2027)",
            "         Now you have 2 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "within flexible period /from 31Feb27 /to someday",
          "expected_output": [
            "{{LINE}}",
            "Crystal: Okkk!!! Here's your new task!!",
            "         [W][ ] flexible period (from: 31Feb27 to: someday)",
            "         Now you have 3 tasks in the list.",
            "{{LINE}}"
          ]
        },
        {
          "input": "bye",
          "expect_exit": true,
          "expected_output": [
            "{{LINE}}",
            "Crystal: Byeee!!! You did amazing today. See you soon!",
            "{{LINE}}"
          ]
        }
      ],
      "expected_files": {
        "data/crystal.txt": [
          "W | 0 | collect certificate | 15 Jan 2027 | 25 Jan 2027",
          "W | 0 | reversed period | 25 Jan 2027 | 15 Jan 2027",
          "W | 0 | flexible period | 31Feb27 | someday"
        ]
      }
    }
  ]
}
```

After `UI-12`, the runner verifies that filtered listing did not change `data/crystal.txt`:

```text
T | 0 | mention 02 Dec 2026
D | 0 | submit report | 02 Dec 2026 0900
D | 0 | later deadline | 03 Dec 2026
E | 0 | workshop | 02 Dec 2026 0800 | 02 Dec 2026 1000
E | 0 | conference | 01 Dec 2026 | 03 Dec 2026
E | 0 | overnight trip | 01 Dec 2026 | 02 Dec 2026
W | 0 | collect certificate | 01 Dec 2026 | 03 Dec 2026
E | 0 | weekly call | Monday 0600 | Tuesday 0700
```

After `UI-13`, the runner verifies that searching did not change `data/crystal.txt`.
