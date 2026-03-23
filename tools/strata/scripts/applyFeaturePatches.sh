#!/usr/bin/env bash
set -euo pipefail

PATCH_DIR="../minecraft-source/patches/features"
REAL_PATCH_DIR="$(realpath "$PATCH_DIR")"
SOURCE_DIR="../minecraft-source/src/main/java"
REAL_SOURCE_DIR="$(realpath "$SOURCE_DIR")"

echo "Applying feature patches ..."

git -C "$SOURCE_DIR" reset --hard strata/file-patches > /dev/null

find "$REAL_PATCH_DIR" -name '*.patch' | sort | while read -r patch; do
    if ! git -C "$SOURCE_DIR" am --3way $patch > /dev/null 2>&1; then
        echo "Failed to apply patch: $(basename "$patch")"
    else
        echo "Applied patch: $(basename "$patch")"
    fi

done

echo "Successfully applied all feature patches"