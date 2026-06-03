# tech lead notes

## isolate history cleanup mechanism
- remove history cleanup configs from existing components (if it exists)
- implement a dedicated fluxnova-cleanup-component which uses the fluxnova java api to periodically clear old deployments & hanging process instances.

## isolate the fluxnova front end and rest api
- currently every component has the fluxnova monitoring front end and the fluxnova rest api as part of their dependencies.
- this is not efficient
- we need to have a dedicated component that does not process anything but only exposes the rest api and the monitoring tool.