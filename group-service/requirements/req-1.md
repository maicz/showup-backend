# BPMN enhancements

## 1. rename the bpmn file to something more suggestive [DONE]
- Renamed `process.bpmn` to `group-lifecycle.bpmn`.

## 2. extend the bpmn file with at least 3 new delegates and 2 gateways [DONE]
- Extended `group-lifecycle.bpmn` with 2 gateways (`Is Approved?` and `Group Tier?`) and 4 delegates (`rejectGroupDelegate`, `processGroupActivationDelegate`, `configurePremiumFeaturesDelegate`, `configureStandardFeaturesDelegate`).
- The new delegates and gateways make use of the variables: `isApproved` (Boolean) and `groupTier` (String).
- Created [testing.md](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/group-service/requirements/testing.md) in the `requirements` folder storing the curl commands used to start the new process for the different execution paths.
- Ensured the documented tests in `testing.md` send the newly required variables.

## 3. implement the 3 new delegates [DONE]
- Implemented `RejectGroupDelegate`, `ProcessGroupActivationDelegate`, `ConfigurePremiumFeaturesDelegate`, and `ConfigureStandardFeaturesDelegate` in package `com.mz.group_service.delegates`. These log messages indicating execution of the delegate.

