## main process
- create a new bpmn process with a relevant name and id.
- the process must contain at least 3 service tasks
- one of the service tasks must be called in a loop implemented in the bpmn part
- the process contains a variable called audit_log which is a json containing important audit events
- from the sub_process call map an output variable called comm_audit_log
- when each sub process ends, ensure that the contents of the comm_audit_log are merged into the parent audit_log

## sub process
- create a new bpmn process with a relevant name and id.
- the process must contain at least 5 service tasks and 2 gateways
- the process will have an audit_log variable which it will populate with randomly generated test data
- this is the sub process called by the main process
- audit_log from sub process is mapped to comm_audit_log from main process

## other notes
- remember to use async continuations where applicable