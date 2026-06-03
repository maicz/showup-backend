# Testing Enhanced Event Process

This file contains the curl commands used to start and verify the new `event-service-process` defined in `event-lifecycle.bpmn`.

## Start Process Instance

To start a new instance of the event process, use the following curl command. Note that we provide the required variables `isValid` and `eventType` to exercise the different paths through the gateways.

### Path 1: Valid Private Event
```bash
curl -X POST "http://localhost:8083/engine-rest/process-definition/key/event-service-process/start" \
     -H "Content-Type: application/json" \
     -d '{
       "variables": {
         "eventName": {"value": "Secret Meetup", "type": "String"},
         "isValid": {"value": true, "type": "Boolean"},
         "eventType": {"value": "PRIVATE", "type": "String"}
       }
     }'
```

### Path 2: Valid Public Event
```bash
curl -X POST "http://localhost:8083/engine-rest/process-definition/key/event-service-process/start" \
     -H "Content-Type: application/json" \
     -d '{
       "variables": {
         "eventName": {"value": "Open Concert", "type": "String"},
         "isValid": {"value": true, "type": "Boolean"},
         "eventType": {"value": "PUBLIC", "type": "String"}
       }
     }'
```

### Path 3: Invalid Event (Early Termination)
```bash
curl -X POST "http://localhost:8083/engine-rest/process-definition/key/event-service-process/start" \
     -H "Content-Type: application/json" \
     -d '{
       "variables": {
         "eventName": {"value": "Spam Event", "type": "String"},
         "isValid": {"value": false, "type": "Boolean"}
       }
     }'
```

## Verify Results
You can check the process instance status and history via the REST API or the Camunda/Fluxnova Webapp to ensure the correct delegates were executed based on the input variables.
