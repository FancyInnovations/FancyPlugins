---
icon: dot
order: 100
---

# Alerts (soon)

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/web-services/fancyanalytics](https://fancyinnovations.com/docs/web-services/fancyanalytics).
!!!

Our analytics platform allows you to define **alerts** that notify you when something important happens — a metric crosses a threshold, an event occurs, or an error spikes.

With alerts, you can **react faster**, **stay informed**, and **automate monitoring** for your apps, plugins, or services.

## What is an Alert?

An **alert** is a rule that monitors your data in real-time and triggers a notification when conditions are met.

You can create alerts for:

- **Metrics** — e.g. `cpu_usage_percent > 90`
- **Events** — e.g. `event == 'minecraft_server_crashed'`
- **Errors** — e.g. `5+ errors in 10s`
- **Logs** — e.g. `ERROR logs > 10 in 1 minute`
