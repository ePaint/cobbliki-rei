#!/usr/bin/env bash
# Publish the current build to Modrinth. Reads MODRINTH_API_TOKEN from wiki/.env
# (i.e. ../.env relative to this script) and the version from gradle.properties.
#
#   ./publish-modrinth.sh            # build (if needed) + publish current version
#   ./publish-modrinth.sh --release  # mark version_type=release instead of beta
set -euo pipefail
cd "$(dirname "$0")"

SLUG="cobbliki-rei"
GAME_VERSIONS='["1.21.1"]'
LOADERS='["fabric"]'
TYPE="beta"; [ "${1:-}" = "--release" ] && TYPE="release"

TOKEN="${MODRINTH_API_TOKEN:-}"
[ -n "$TOKEN" ] || TOKEN="$(grep -hE '^MODRINTH_API_TOKEN=' ../.env .env 2>/dev/null | head -1 | cut -d= -f2- | tr -d '\r"')"
[ -n "$TOKEN" ] || { echo "Set MODRINTH_API_TOKEN (env var or .env)"; exit 1; }
VER="$(grep -E '^modVersion=' gradle.properties | cut -d= -f2 | tr -d '\r')"
JAR="build/libs/cobbliki-rei-$VER.jar"
[ -f "$JAR" ] || ./gradlew build --console=plain >/dev/null
echo "Publishing $SLUG $VER ($TYPE) from $JAR"

api() { curl -s -H "Authorization: $TOKEN" "$@"; }

PID="$(api "https://api.modrinth.com/v2/project/$SLUG" | python -c "import sys,json;print(json.load(sys.stdin)['id'])")"
[ -n "$PID" ] || { echo "project $SLUG not found"; exit 1; }

EXISTS="$(api "https://api.modrinth.com/v2/project/$SLUG/version" | python -c "import sys,json;print('1' if any(v['version_number']=='$VER' for v in json.load(sys.stdin)) else '0')")"
if [ "$EXISTS" = "1" ]; then echo "version $VER already exists on Modrinth — nothing to do"; exit 0; fi

# Keep the project flagged client-side (idempotent).
api -X PATCH "https://api.modrinth.com/v2/project/$SLUG" \
  -H "Content-Type: application/json" \
  -d '{"client_side":"required","server_side":"unsupported"}' >/dev/null || true

DATA="$(VER="$VER" PID="$PID" TYPE="$TYPE" GAME_VERSIONS="$GAME_VERSIONS" LOADERS="$LOADERS" python - <<'PY'
import os, json
deps = [
    ("MdwFAVRL", "required"),   # Cobblemon
    ("nfn13YXA", "required"),   # Roughly Enough Items
    ("P7dR8mSH", "required"),   # Fabric API
    ("Ha28R6CL", "required"),   # Fabric Language Kotlin
    ("s7N7AsqL", "optional"),   # CobbleDollars
    ("HU6mkUZs", "optional"),   # tmcraft
    ("SszvX85I", "optional"),   # Cobblemon: Mega Showdown
    ("XP2jcAo0", "optional"),   # pastureLoot
]
ver = os.environ["VER"]
print(json.dumps({
    "name": f"Cobbliki REI {ver}",
    "version_number": ver,
    "changelog": f"https://github.com/ePaint/cobbliki-rei/releases/tag/v{ver}",
    "dependencies": [{"project_id": p, "dependency_type": t} for p, t in deps],
    "game_versions": json.loads(os.environ["GAME_VERSIONS"]),
    "version_type": os.environ["TYPE"],
    "loaders": json.loads(os.environ["LOADERS"]),
    "featured": True,
    "project_id": os.environ["PID"],
    "file_parts": ["file"],
    "primary_file": "file",
}))
PY
)"

curl -s -X POST "https://api.modrinth.com/v2/version" \
  -H "Authorization: $TOKEN" \
  -F "data=$DATA;type=application/json" \
  -F "file=@$JAR;type=application/java-archive" \
  | python -c "import sys,json;d=json.load(sys.stdin);print('published:', d.get('version_number'), d.get('id')) if d.get('id') else print('ERROR:', d)"
