---
icon: dot
---

# Lightweight Minecraft Client SDK

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/general](https://fancyinnovations.com/docs/general).
!!!


## Problem

We need a lightweight Minecraft Client which can easily run in GitHub Actions or other CI systems to run e2e tests.

## Solution

The solution is to create a lightweight Minecraft Client SDK that can be used in GitHub Actions or other CI systems to run e2e tests. 
The SDK should be able to have the basic functionality of a Minecraft Client:
- Connect to a Minecraft server
- Run commands
- Interact with the world and entities
- Send and receive packets

The SDK should provide a simple API that can be used to interact with the Minecraft server.

The SDK should have an easy way to get notified of any incoming packets from the server, to test our packet library.
