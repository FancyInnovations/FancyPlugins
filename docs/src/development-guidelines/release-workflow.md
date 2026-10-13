---
icon: dot
---

# Release Workflow

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins](https://fancyinnovations.com/docs/minecraft-plugins).
!!!

This document describes the steps to be taken when releasing a new version of a plugin.

## Before the release

1. Make sure that all relevant changes are noted in the `CHANGELOG.md` file
2. Manually test all changes and make sure that all existing features are still working as expected
3. Update all dependencies to the latest version
4. Optional: give out a pre-release version to the community for testing

## Release

1. Update the version number in the `VERSION` file
2. Update the supported versions in the deployment config files, if necessary
3. Commit and push the changes
4. Create a new tag with the version number
5. Copy the changelog to the changelog page on the documentation website
6. Run the deployment workflow
7. Confirm that the deployment was successful
8. Send out a message to the community that a new version is available

## After the release

1. Clear the `CHANGELOG.md` file, so that it is ready for the next release
2. Wait for feedback from the community