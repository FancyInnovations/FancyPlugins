---
order: 8
icon: dot
---

![](../static/monorepo.png)

#

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins](https://fancyinnovations.com/docs/minecraft-plugins).
!!!

## What is a monorepo?

A monorepo (monolithic repository) is a version control strategy where multiple related projects, such as Minecraft plugins and libraries, reside within a single repository. 
Instead of maintaining separate repositories for each plugin or library, all codebases share the same version control system, dependencies, and development workflow.

Read more about monorepos there:
- https://monorepo.tools/
- https://medium.com/@lewimuchiri/setting-up-a-gradle-monorepo-with-spring-boot-and-version-catalogs-41fdf1112dac
- https://dev.to/scorsi/why-i-choose-to-use-a-mono-repo-for-a-very-large-project-hkp
- https://docs.gradle.org/current/userguide/intro_multi_project_builds.html

## Benefits of a Monorepo

- **Code Reuse**: Share common libraries between plugins without duplication.
- **Simplified Dependency Management**: Easily reference shared libraries without external repositories.
- **Consistent Development Workflow**: Standardized build tools and CI/CD pipelines across all projects.
- **Atomic Changes**: Simultaneously update multiple plugins and libraries within the same commit.
- **Easier Refactoring**: Modify shared code with full visibility of its impact on all projects.

## Repository Structure

The dependencies between plugins and libraries are managed using Gradle, a build automation tool for Java projects.

There are three main folders in the monorepo:

- `plugins/`: Contains all Minecraft plugins.
- `libraries/`: Contains all shared libraries.
- `docs/`: Contains documentation for the plugins and libraries.

## Development Workflow

1. Clone the Repository
2. Make Changes
3. Test Changes
4. Commit Changes
5. Push Changes
6. Review Changes
7. Merge Changes
8. Publish Changes
9. Monitor Changes

Commit messages should follow this format:

```
<scope>: <short description>
```

scope: The affected component (e.g., plugin, library, docs, ci etc.)

Example:

```
fancynpcs: Fix NPE in teleport command
```