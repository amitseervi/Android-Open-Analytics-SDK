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
1. To integerate analytics sdk library add gradle depndency on the module `implementation project(":analyticssdk")`
2. Add AnalyticsSDKInitializer file to initialize sdk on app startup.
3. while initializing sdk before calling `RignisAnalytics.initialize(context)` provide necessary configuration like baseUrl for analytics server
4. other configuration can be changed afterwards as well which are not critical to functionality
5. 

## Build
1. Currently aar file integration is failing due to koin class not found

## Design
Architecture design of library
![Library Architecture](analyticssdk/design/design.webp?raw=true "System Design")


## Testing
1. Testing in progress

## Improvenment
1. Fallback logic can be improved
2. using compression technique to reduce api payload
3. Keeping open tcp connection and reusing it for further event sync  

