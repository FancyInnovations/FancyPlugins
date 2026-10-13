# Pulling Metrics

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/general](https://fancyinnovations.com/docs/general).
!!!


At the moment, the application actively pushes data to FancyAnalytics. 
It would be nice to have the ability that FancyAnalytics can pull data from the application. 
This would allow FancyAnalytics to be more flexible and not rely on the application to send data. 
This would also allow FancyAnalytics to pull data from other sources, such as databases or other applications.

## Prometheus data schema support

Prometheus is a powerful monitoring and alerting toolkit that is widely used in the industry.
Many applications expose their metrics in a format that is compatible with Prometheus.

FancyAnalytics should support this data schema as well.