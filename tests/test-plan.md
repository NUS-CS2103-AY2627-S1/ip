# D-Notes Test Plan

1. Start Vega and enter `note room code is 1234`.
   Expected: Vega confirms the note was recorded.
2. Enter `note bring an adapter`, followed by `notes`.
   Expected: both notes appear in insertion order with one-based numbering.
3. Enter `delete-note 1`, followed by `notes`.
   Expected: only `bring an adapter` remains.
4. Exit with `bye`, restart Vega, and enter `notes`.
   Expected: `bring an adapter` is restored from storage.
5. Enter `note` without text and `delete-note 99`.
   Expected: Vega shows a helpful error for each invalid command and continues running.
