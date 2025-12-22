# Minecraft Download Platform Integrations

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/general](https://fancyinnovations.com/docs/general).
!!!


Being able to see metrics like downloads, views and revenue for Minecraft plugins is crucial for developers to understand their audience and improve their products.
This document outlines the integration of Minecraft download platforms with FancyAnalytics to provide these insights.

Normally, when a plugin developer wants to see how many downloads their plugin has, they have to go to each platform individually and check the stats.
Having all this data in one place makes it much easier to analyze and make decisions based on the data.
This data can also be used to compare the performance of different plugins across platforms.

There should be metrics for views and downloads, as well as revenue (on Modrinth).

## Supported Platforms

- **Modrinth**:
  - Has analytics endpoints that provide downloads, views and revenue data
  - The endpoints aren't documented, but can be found in the source code
- **Hangar**: 
  - Has an API that provides downloads and views data
  - The endpoints are documented
- **Jenkins**:
  - Quite many dev builds aree  
  - Has no API, but can be done with plugins