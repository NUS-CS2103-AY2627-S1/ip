# Console Ui test plan

This plan is the input to the project-specific `test-ui` skill. Add test cases to the JSON block below in the order they should run. Each case records its aim, the console input lines, and the complete expected output.

The expected output is compared exactly after normalizing only Windows versus Unix line endings. Keep leading spaces, tabs, trailing spaces, and the final newline intact. An `expected_output` list is also supported when one output line per JSON string is easier to edit; the runner adds a final newline to that form.

The runner sends each string in `inputs` as one line to a fresh program process. The configured Java command must use Java 25.

```json
{
  "program": ["java", "-cp", "src/main/java", "AnswerMe"],
  "working_directory": ".",
  "timeout_seconds": 30,
  "test_cases": [
    {
      "name": "Exit immediately",
      "aim": "Verify that the application starts successfully and exits cleanly when the user enters bye.",
      "inputs": ["bye"],
      "expected_output": "      >>                                                        >=>       >=>           \n     >>=>                                                       >> >=>   >>=>           \n    >> >=>     >==>>==>   >===>  >=>      >=>   >==>    >> >==> >=> >=> > >=>   >==>    \n   >=>  >=>     >=>  >=> >=>      >=>  >  >=> >>   >=>   >=>    >=>  >=>  >=> >>   >=>  \n  >=====>>=>    >=>  >=>   >==>   >=> >>  >=> >>===>>=>  >=>    >=>   >>  >=> >>===>>=> \n >=>      >=>   >=>  >=>     >=>  >=>>  >=>=> >>         >=>    >=>       >=> >>        \n>=>        >=> >==>  >=> >=> >=> >==>    >==>  >====>   >==>    >=>       >=>  >====>   \n\nHello! I'm AnswerMe, your personal assistant bot.\nWhat can I do for you today?\n\n____________________________________________________________\nWhat can I do for you today?\n____________________________________________________________\n\t____________________________________________________________\n\tBye. Hope to see you again soon!\n\t____________________________________________________________\n\n"
    }
  ]
}
```
