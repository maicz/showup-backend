# Testing Enhanced Group Process

This file contains the curl commands used to start and verify the new `group-lifecycle-process` defined in `group-lifecycle.bpmn`.

## Start Process Instance

To start a new instance of the group process, use the following curl command. Note that we provide the required variables `isApproved` and `groupTier` to exercise the different paths through the gateways.

### Path 1: Approved Premium Group
```bash
curl -X POST "http://localhost:8082/engine-rest/process-definition/key/group-lifecycle-process/start" \
     -H "Content-Type: application/json" \
     -d '{
       "variables": {
         "groupName": {"value": "Super Hikers", "type": "String"},
         "isApproved": {"value": true, "type": "Boolean"},
         "groupTier": {"value": "PREMIUM", "type": "String"}
       }
     }'
```

### Path 2: Approved Standard Group
```bash
curl -X POST "http://localhost:8082/engine-rest/process-definition/key/group-lifecycle-process/start" \
     -H "Content-Type: application/json" \
     -d '{
       "variables": {
         "groupName": {"value": "Casual Hikers", "type": "String"},
         "isApproved": {"value": true, "type": "Boolean"},
         "groupTier": {"value": "STANDARD", "type": "String"}
       }
     }'
```

### Path 3: Rejected Group
```bash
curl -X POST "http://localhost:8082/engine-rest/process-definition/key/group-lifecycle-process/start" \
     -H "Content-Type: application/json" \
     -d '{
       "variables": {
         "groupName": {"value": "Invalid Group", "type": "String"},
         "isApproved": {"value": false, "type": "Boolean"}
       }
     }'
```

## Verify Results
You can check the process instance status and logs to ensure the correct delegates (e.g., `RejectGroupDelegate`, `ProcessGroupActivationDelegate`, `ConfigurePremiumFeaturesDelegate`, or `ConfigureStandardFeaturesDelegate`) were executed based on the input variables.
