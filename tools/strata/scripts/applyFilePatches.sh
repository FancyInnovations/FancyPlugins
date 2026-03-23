#!/usr/bin/env bash
set -euo pipefail

PATCH_DIR="../minecraft-source/patches/files"
REAL_PATCH_DIR="$(realpath "$PATCH_DIR")"
SOURCE_DIR="../minecraft-source/src/main/java"
REAL_SOURCE_DIR="$(realpath "$SOURCE_DIR")"

echo "Applying file patches ..."

git -C "$SOURCE_DIR" reset --hard strata/decompiled-sources > /dev/null

find "$REAL_PATCH_DIR" -name '*.patch' | sort | while read -r patch; do
    if ! git -C "$SOURCE_DIR" apply --3way "$patch" > /dev/null 2>&1; then
        echo "Failed to apply patch: $(basename "$patch")"
    else
        echo "Applied patch: $(basename "$patch")"
    fi

done

git -C "$SOURCE_DIR" commit --author="Strata <strata@fancyinnovations.com>" -am "Apply file patches" > /dev/null
git -C "$SOURCE_DIR" tag -f strata/file-patches > /dev/null

echo "Successfully applied all file patches"