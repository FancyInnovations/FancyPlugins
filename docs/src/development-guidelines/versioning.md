---
icon: dot
order: 9
---
# Versioning

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins](https://fancyinnovations.com/docs/minecraft-plugins).
!!!

We use [Semantic Versioning](https://semver.org/) (Semver) for versioning.

The version is structured as follows: `major.minor.patch.<build id>`. The build id is optional and is only used for
development builds.

!!!warning
This versioning system is not getting used yet. We will start applying the rules in the version 3.0.0 of all plugins.
!!!

## Semver

### Major

The major version is increased when there are very significant changes to the codebase. This can be breaking changes to the API and/or major changes to the behavior of the plugin. 
When the major version is increased, the minor and patch versions are reset to 0.

Data might be lost when updating to a new major version.

Breaking changes in the API are expected when updating to a new major version.

### Minor

The minor version is increased when there are new features added to the codebase. This could be new commands, new
features or support for a new minecraft version. This is done more often than increasing the major version. When the
minor version is increased, the patch version is reset to 0.

Data will not be lost when updating to a new minor version.

No breaking changes in the API are expected when updating to a new minor version.

### Patch

The patch version is increased when there are bug fixes or small changes to the codebase. This is done very often.

Data will not be lost when updating to a new patch version.

Breaking changes in the API are not expected when updating to a new patch version.

### Build

The build id is optional and is only used for development builds. The build id is increased every time a new build is
created.

The build id is not reset when increasing the major, minor or patch version - it is completely independent.

Disclaimers for development builds:
- Development builds are not recommended for production use.
- Development builds might contain bugs.
- Development builds might contain incomplete features.
- Development builds might contain breaking changes.
- Development builds might contain experimental features.
- Development builds might contain untested features.
- Development builds might contain unoptimized code.
- Development builds might contain security vulnerabilities.
- Development builds might contain incomplete documentation.
- Development builds might cause data loss.
- Development builds might cause server crashes.
- Development builds might cause server lag.
- Development builds might cause other issues.

## Changelog

Every release should have a changelog. The changelog will be added to all platforms where the plugin is available. The
changelog should contain the following information:

- The version number
- A list of changes that were made in this release
- A list of bug fixes that were made in this release
- A list of new features that were added in this release
- A list of changes to the API that were made in this release
- A list of breaking changes that were made in this release
