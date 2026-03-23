#!/usr/bin/env bash
set -euo pipefail

PATCH_DIR="../minecraft-source/patches/features"
SOURCE_DIR="../minecraft-source/src/main/java"

# remove old patches
find "$PATCH_DIR" -name '*.patch' -delete

echo "Rebuilding feature patches ..."

git -C "$SOURCE_DIR" format-patch \
    strata/file-patches..HEAD \
    --no-signature \
    --subject-prefix="" \
    --output-directory ../../../patches/features \
    > /dev/null

echo "Successfully rebuilt feature patches"