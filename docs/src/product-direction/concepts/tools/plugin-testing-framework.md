---
icon: dot
---

# Plugin Testing Framework

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/general](https://fancyinnovations.com/docs/general).
!!!


The Plugin Testing Framework is a tool that allows developers to test their plugins in a controlled environment. 
It provides a set of tools and libraries that make it easy to create and run tests for plugins, ensuring that they work as expected and do not introduce any bugs or issues.

**Workflow:**
1. Start a Minecraft server with the plugin installed.
2. Let a [lightweight minecraft client](lightweight-minecraft-client-sdk.md) connect to the server.
3. Run tests
4. Assert results (e.g., if the correct packets were sent)
5. Stop the server and the client

This process can be automated using GitHub Actions or other CI systems, allowing developers to run tests automatically whenever they make changes to their plugins.