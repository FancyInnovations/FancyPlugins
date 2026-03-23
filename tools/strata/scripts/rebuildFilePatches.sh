#!/usr/bin/env bash
set -euo pipefail

PATCH_DIR="../minecraft-source/patches/files"
SOURCE_DIR="../minecraft-source/src/main/java"

echo "Rebuilding file patches from ..."

mkdir -p "$PATCH_DIR"

# remove old patches
find "$PATCH_DIR" -name '*.patch' -delete

git -C "$SOURCE_DIR" diff-tree --name-only --no-commit-id -r HEAD | while read -r file; do
    patch_file="$PATCH_DIR/${file}.patch"
    mkdir -p "$(dirname "$patch_file")"
    echo "Rebuilding patch for $file"

    git -C "$SOURCE_DIR" format-patch --diff-algorithm=myers --full-index --no-signature --no-stat --no-numbered -1 HEAD --stdout "$file" > "$patch_file"
done

echo "Successfully rebuilt file patches"