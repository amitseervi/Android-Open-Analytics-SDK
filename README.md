# Rignis Analytics SDK

## Download
1. use git clone to download project `git clone https://github.com/Rudderstack-Hiring/sdk-assignment-june-2024-amitseervi.git`
2. checkout branch solution-v2 using `git checkout solution-v2`
3. open project into android studio

## Configuration
1. Once project opened in android studio sync the project.
2. Currently no environment variable or secret key required for using project locally.
3. for generating release build or library provide property file and keystore file in keystore folder located in root directory and change signing config in gradle file of analyticssdk

## Intergration
1. To integerate analytics sdk library add gradle depndency on the module `implementation project(":analyticssdk")` or `implementation("com.rignis.analyticssdk:1.0.0")` if published to some repository
2. Add AnalyticsSDKInitializer file to initialize SDK on app startup. check : [AnalyticsSDKInitializer](app/src/main/java/com/rignis/demo/AnalyticsSDKInitializer.kt)
3. while initializing sdk before calling `RignisAnalytics.initialize(context)` provide necessary configuration like baseUrl for analytics server
4. other configuration can be changed afterwards as well which are not critical to functionality
5. Supported configuration from clients.
   - setBaseUrl(Mandatory) : set base url for your analytics server.
   - setBackgroundSyncEnabled(optional, default = true) : background sync allowed when device is idle, connected to network and battery is not low
   - setForegroundSyncInterval(optional, default =  5 seconds) : after single event triggered how much time syncer will wait for batch bucket to get filled before sending this event to server
   - setForegroundSyncBatchSize(optional, default = 10) : after 20 events syncer will not wait for foregroundSyncInterval to finish and forcefully try to sync with network with collected batch
   - setBackgroundSyncIntervalInHour(optinal, default = 4 hours) : after every 4 hours syncer will check for device constraint conditions if allowed by system it will invoke syncing 
   - setNetworkRequestPayloadSize(optional, default = 20) : max number of events which can be sent in single post request
   - optOutAnalytics(optional, default = false) : if client decides to disable further analytics event to stop getting collected client can send this flag to true
   - setEventExpiryTime(optional, default = 10 days) : event will be persisted by this much amount in milliseconds in offline database once event ttl expired it will be cleared when user opens the application
   - setNetworkRequestTimeout(optional, default = 30 seconds) : timeout value for sync network request
  
   - Client id which is mandatory field should be added in application manfiest file with following example
      `<meta-data
            android:name="com.rignis.analyticssdk.clientid"
            android:value="d625a8ed-d251-4c5f-bde0-4dd657a4f885" />`

## Build
1. To publish setup repository credential in library publishing section under analyticssdk module and use `./gradlew :analyticssdk:publish` command to publish library to repository
2. To build demo project using published library change `implementation(project(":analyticssdk"))` to `implementation("com.rignis.analyticssdk:1.0.0")`

## Run
1. to run if you do not have server URL, you can enable clearTextTraffic in debug build and use localhost as server using following repository : [](https://github.com/amitseervi/Simple-Analytics-Event-Listener)
2. provide your local host ip address and port as base url in configuration

## Design
Architecture design of library
![Library Architecture](analyticssdk/design/design.webp?raw=true "System Design")


## Testing
1. Few test cases are present under androidTest source. to run instrumentation test connect android device and run command `./gradlew :analyticssdk:connectedAndroidTest`
