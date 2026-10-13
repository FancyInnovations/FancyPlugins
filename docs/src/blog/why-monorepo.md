---
authors:
- name: Oliver Schlüter
  email: oliver@fancyinnovations.com
  link: https://github.com/OliverSchlueter
  avatar: https://avatars.githubusercontent.com/u/79666085?v=4

date: 2025-03-21
description: The reasons why we switched to a monorepo for our Minecraft plugins and libraries.
---

![](../static/monorepo.png)

# Why We Switched to a Monorepo

## Introduction

Managing multiple repositories for our Minecraft plugins and libraries became increasingly complex. Over time, we noticed that our two main plugins, **FancyNpcs** and **FancyHolograms**, were becoming more independent of each other. However, our long-term vision is to ensure a consistent **user experience** and **developer experience** across all our plugins.

### Our Goals:
- Maintain a consistent **command structure** across all plugins.
- Standardize the **configuration structure**.
- Establish a **uniform API design**.
- Follow the same **design patterns** in all plugins.

To make it easier to achieve these goals, we decided to **switch to a monorepo**. This approach lowers the barrier to collaboration and helps us maintain consistency more effectively.

## Problems with Multiple Repositories

### Dependency Management

Our plugins rely on **FancyLib** and **FancySitula** as shared libraries. In a multi-repo setup, testing changes in **FancyLib** requires us to publish a new version to Maven local, update the version in each plugin, and then test the changes. This is a **time-consuming process**.

With a **monorepo**, dependencies are linked directly to the source code, allowing for seamless testing and modification. This approach offers several advantages:
- Changes in **FancyLib** can be tested instantly without publishing intermediate versions.
- Dependencies are updated in a **single place**, preventing version mismatches between plugins.
- Redundant dependency versions are eliminated, making maintenance easier.

### Code Duplication

Both **FancyNpcs** and **FancyHolograms** rely on **spawning and managing entities with packets**, but they implement this functionality in different ways. As a result, there is **significant code duplication**, leading to maintenance challenges and duplicated bugs.

With a **monorepo**, we can extract common functionality into a shared library, ensuring:
- **Code reuse** across multiple plugins.
- **Less duplication**, reducing the chances of bugs occurring in multiple places.
- **Easier maintenance**, as fixes and improvements benefit all plugins simultaneously.

## CI/CD Pipelines

Managing CI/CD pipelines for multiple repositories introduces unnecessary overhead. Each plugin requires its own pipeline, meaning any updates or improvements must be **duplicated across multiple repositories**.

A **monorepo** simplifies this by:
- Providing a **single CI/CD pipeline** that builds and tests all plugins in one place.
- Ensuring **better compatibility** between plugins by automatically testing their interactions.
- Reducing maintenance efforts, as changes to the pipeline only need to be made **once**.

## Conclusion

Switching to a monorepo has streamlined our development process, improved collaboration, and helped us maintain a consistent structure across all our plugins. By eliminating dependency headaches, reducing code duplication, and unifying our CI/CD pipelines, we’ve set ourselves up for **a more efficient and scalable** future.

