# /// script
# requires-python = ">=3.11"
# dependencies = ["requests>=2.31"]
# ///
import argparse
import json
import os
import pathlib
import re
import subprocess
import sys

import requests

API = "https://api.modrinth.com/v2"
SLUG = "cobbliki-rei"
GAME_VERSIONS = ["1.21.1"]
LOADERS = ["fabric"]
DEPENDENCIES = [
    ("MdwFAVRL", "required"),   # Cobblemon
    ("nfn13YXA", "required"),   # Roughly Enough Items
    ("P7dR8mSH", "required"),   # Fabric API
    ("Ha28R6CL", "required"),   # Fabric Language Kotlin
    ("s7N7AsqL", "optional"),   # CobbleDollars
    ("HU6mkUZs", "optional"),   # tmcraft
    ("SszvX85I", "optional"),   # Cobblemon: Mega Showdown
    ("XP2jcAo0", "optional"),   # pastureLoot
]
ROOT = pathlib.Path(__file__).resolve().parent.parent


def token() -> str:
    env = os.environ.get("MODRINTH_API_TOKEN", "").strip()
    if env:
        return env
    for path in (ROOT.parent / ".env", ROOT / ".env"):
        if not path.exists():
            continue
        for line in path.read_text(encoding="utf8").splitlines():
            if line.startswith("MODRINTH_API_TOKEN="):
                return line.split("=", 1)[1].strip().strip('"')
    sys.exit("MODRINTH_API_TOKEN not set (env var or .env)")


def mod_version() -> str:
    m = re.search(r"^modVersion=(.+)$", (ROOT / "gradle.properties").read_text(encoding="utf8"), re.M)
    if not m:
        sys.exit("modVersion not found in gradle.properties")
    return m.group(1).strip()


def jar_path(version: str) -> pathlib.Path:
    jar = ROOT / "build" / "libs" / f"cobbliki-rei-{version}.jar"
    if jar.exists():
        return jar
    gradlew = ROOT / ("gradlew.bat" if os.name == "nt" else "gradlew")
    subprocess.run([str(gradlew), "build", "--console=plain"], cwd=ROOT, check=True)
    if not jar.exists():
        sys.exit(f"build did not produce {jar.name}")
    return jar


def api(tok: str) -> requests.Session:
    s = requests.Session()
    s.headers.update({"Authorization": tok, "User-Agent": "ePaint/cobbliki-rei-publisher"})
    return s


def project_id(s: requests.Session) -> str:
    r = s.get(f"{API}/project/{SLUG}")
    r.raise_for_status()
    return r.json()["id"]


def published_versions(s: requests.Session) -> set[str]:
    r = s.get(f"{API}/project/{SLUG}/version")
    r.raise_for_status()
    return {v["version_number"] for v in r.json()}


def set_client_only(s: requests.Session) -> None:
    r = s.patch(f"{API}/project/{SLUG}", json={"client_side": "required", "server_side": "unsupported"})
    r.raise_for_status()


def publish(s: requests.Session, pid: str, version: str, jar: pathlib.Path, version_type: str) -> dict:
    data = {
        "name": f"Cobbliki REI {version}",
        "version_number": version,
        "changelog": f"https://github.com/ePaint/cobbliki-rei/releases/tag/v{version}",
        "dependencies": [{"project_id": p, "dependency_type": t} for p, t in DEPENDENCIES],
        "game_versions": GAME_VERSIONS,
        "version_type": version_type,
        "loaders": LOADERS,
        "featured": True,
        "project_id": pid,
        "file_parts": ["file"],
        "primary_file": "file",
    }
    files = {
        "data": (None, json.dumps(data), "application/json"),
        "file": (jar.name, jar.read_bytes(), "application/java-archive"),
    }
    r = s.post(f"{API}/version", files=files)
    if not r.ok:
        sys.exit(f"publish failed [{r.status_code}]: {r.text}")
    return r.json()


def main() -> None:
    ap = argparse.ArgumentParser(description="Publish the current build to Modrinth.")
    ap.add_argument("--release", action="store_true", help="version_type=release (default: beta)")
    args = ap.parse_args()

    s = api(token())
    version = mod_version()
    if version in published_versions(s):
        print(f"version {version} already on Modrinth, nothing to do")
        return
    jar = jar_path(version)
    set_client_only(s)
    out = publish(s, project_id(s), version, jar, "release" if args.release else "beta")
    print(f"published {out['version_number']} -> {API.rsplit('/', 1)[0]}/project/{SLUG}/version/{out['id']}")


if __name__ == "__main__":
    main()
