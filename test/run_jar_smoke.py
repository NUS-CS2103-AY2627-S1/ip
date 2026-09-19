"""Launch the actual release JAR in an empty folder and drive its JavaFX controls."""
import pathlib
import subprocess
import sys
import tempfile

jar = pathlib.Path(sys.argv[1]).resolve()
source = pathlib.Path(__file__).with_name("JarSmokeAgent.java").resolve()
with tempfile.TemporaryDirectory(prefix="gary-jar-test-") as temporary:
    work = pathlib.Path(temporary)
    classes = work / "classes"
    classes.mkdir()
    subprocess.run(["javac", "-cp", str(jar), "-d", str(classes), str(source)], check=True)
    manifest = work / "MANIFEST.MF"
    manifest.write_text("Premain-Class: gary.smoke.JarSmokeAgent\n", encoding="utf-8")
    agent = work / "smoke-agent.jar"
    subprocess.run(["jar", "cfm", str(agent), str(manifest), "-C", str(classes), "."], check=True)
    for number, farewell in enumerate(("bye", "BYE")):
        session = work / f"session-{number}"
        session.mkdir()
        result = subprocess.run(
            ["java", f"-javaagent:{agent}={farewell}", "-jar", str(jar)],
            cwd=session, capture_output=True, text=True, timeout=45,
        )
        print(result.stdout, result.stderr, flush=True)
        print("JAVA_EXIT_CODE", result.returncode, flush=True)
        if result.returncode or "SMOKE_GUI_PASS" not in result.stdout:
            raise SystemExit("Release JAR GUI smoke test failed")
        print("SMOKE_PROCESS_EXITED_SUCCESSFULLY", farewell, flush=True)
        if "release smoke test" not in (session / "data" / "gary.txt").read_text():
            raise SystemExit("Release JAR did not persist the task")
print("All release JAR smoke tests passed")
