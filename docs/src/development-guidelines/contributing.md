---
icon: dot
order: 80
---
# Contributing

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins](https://fancyinnovations.com/docs/minecraft-plugins).
!!!

This topic is all about the development guidelines that we follow. It is important to follow these guidelines to maintain the quality of the code and to make sure that the code is consistent and easy to read.

## Pull Requests

Generally, pull requests are welcome. However, it is important to follow the guidelines below when creating a pull request.

If you plan to make a major change to the code, please join our Discord server and discuss it with us before creating a pull request out of nowhere. This will help to avoid wasting time on a pull request that might not be accepted.

### Code Style

Please try to adapt to the code style that is already present in the project. If you are unsure about the code style, please join our Discord server and ask for help.

If you have suggestions for a better code style, please join our Discord server and discuss it with us.

### Commit Messages

Please make sure that your commit messages are clear and concise. It is important to write a good commit message so that it is easy to understand what the commit is about.

Keep the commit message short and to the point. If you need to write a longer message, please use the body of the commit.

Follow the format described in the [Monorepo](monorepo.md) guidelines.

### Testing

Please make sure that your code is tested before marking the pull request as ready for review. This means that the code should compile and run without any errors. If you are unsure about how to test your code, please join our Discord server and ask for help.

When creating a PR for FancyNpcs, please run the `/fancynpcs run_tests` command in-game and attach a screenshot of the output to the PR.

### Documentation

Please add documentation to your code. This means that you should add comments to your code so that it is easy to understand what the code is doing. Also, please add Javadocs to the code if it is a public API.

If you modified, added, or removed any commands, please update the help command messages and the docs.

### Code Reviews

All pull requests will be reviewed by a project maintainer. The review process is intended to ensure that the code meets our quality standards and follows our guidelines. Please be patient, as reviews may take some time.

### Branching Strategy

Please create a new branch for each feature or bugfix. This helps to keep the main branch clean and makes it easier to manage multiple pull requests.

### Issue Tracking

If you are working on an issue, please make sure to reference the issue number in your commit messages and pull request description. This helps to keep track of what changes are related to which issues.
