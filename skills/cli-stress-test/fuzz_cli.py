#!/usr/bin/env python3
import argparse
import subprocess
import sys


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--classpath", required=True)
    parser.add_argument("--main", required=True)
    parser.add_argument("--input-file", required=True)
    parser.add_argument("--java", default="java")
    parser.add_argument("--timeout", type=float, default=3)
    args = parser.parse_args()
    with open(args.input_file, encoding="utf-8") as file:
        inputs = file.read().splitlines()
    source = f"{len(inputs)} commands from {args.input_file}"
    data = "\n".join(inputs) + "\n"
    try:
        result = subprocess.run([args.java, "-cp", args.classpath, args.main], input=data,
                                text=True, capture_output=True, timeout=args.timeout)
    except subprocess.TimeoutExpired:
        print(f"FAIL: timeout ({source})\n{data}")
        return 1
    output = result.stdout + result.stderr
    if result.returncode != 0 or "Exception" in output or "Error:" in output or "at Puke." in output:
        print(f"FAIL: crash or stack trace ({source}, exit={result.returncode})\n{data}\n--- output ---\n{output}")
        return 1
    print(f"PASS: {source}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
