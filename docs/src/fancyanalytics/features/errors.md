---
icon: dot
order: 300
---

# Errors

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/web-services/fancyanalytics](https://fancyinnovations.com/docs/web-services/fancyanalytics).
!!!

Our analytics platform supports error and exception tracking across all projects.

Errors are structured records of exceptions that occur in your applications. 
They help you **debug**, **monitor stability**, and **gain insights into failure patterns**.


## What is an Error?

An **error** represents a crash, exception, or runtime issue that happened inside an application. It includes:

- A **timestamp**
- The **exception class** (e.g. `NullPointerException`)
- The **exception message**
- The **stack trace**
- Optional **custom properties** for extra context
