import re
import subprocess
from pathlib import Path

text = Path("test/ui-test-plan.md").read_text(encoding="utf-8")
cases = re.split(r"(?m)^## Test case:\s*", text)[1:]
for number, case in enumerate(cases, 1):
    title, _, body = case.partition("\n")
    def block(label):
        match = re.search(r"(?ms)^### " + re.escape(label) + r"\s*\n```[^\n]*\n(.*?)\n```", body)
        if not match:
            raise ValueError(f"Missing {label} block in {title.strip()}")
        return match.group(1).replace("\r\n", "\n").rstrip("\n")
    commands = block("Input")
    expected = block("Expected output")
    result = subprocess.run(["java", "-cp", "build", "Nexus"], input=commands + "\n",
                            text=True, capture_output=True)
    actual = result.stdout.replace("\r\n", "\n").rstrip("\n")
    print(f"=== Test case {number}: {title.strip()} ===")
    print("--- Console input ---\n" + commands)
    print("--- Console output ---\n" + actual)
    if result.returncode != 0 or actual != expected:
        print("--- FAILED: stopping immediately ---\n--- Expected output ---\n" + expected)
        raise SystemExit(1)
    print("--- PASSED ---")
print(f"All {len(cases)} UI test cases passed.")
