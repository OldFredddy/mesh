#!/usr/bin/env sh
set -eu
PROJECT_ROOT=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_VERSION=8.10.2
GRADLE_CACHE="${GRADLE_USER_HOME:-$HOME/.gradle}/wrapper/mesh-bootstrap/gradle-${GRADLE_VERSION}"
if command -v gradle >/dev/null 2>&1; then
    exec gradle -p "$PROJECT_ROOT" "$@"
fi
if [ ! -x "$GRADLE_CACHE/bin/gradle" ]; then
    mkdir -p "$(dirname -- "$GRADLE_CACHE")"
    ARCHIVE="$(dirname -- "$GRADLE_CACHE")/gradle-${GRADLE_VERSION}-bin.zip"
    if command -v curl >/dev/null 2>&1; then
        curl --fail --location --retry 2 --output "$ARCHIVE" "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
    elif command -v wget >/dev/null 2>&1; then
        wget -O "$ARCHIVE" "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
    else
        echo 'Install Gradle 8.10.2 or make curl/wget available.' >&2
        exit 1
    fi
    unzip -q -o "$ARCHIVE" -d "$(dirname -- "$GRADLE_CACHE")"
    rm -f "$ARCHIVE"
fi
exec "$GRADLE_CACHE/bin/gradle" -p "$PROJECT_ROOT" "$@"
