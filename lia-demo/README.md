# Lithium Community Android SDK Example  

This reference app provides an example of how to use the Lithium Community Android SDK.
This README assumes that you have Android Studio installed and a working Community instance.

## Getting Started

1. Jump to [Setting up the dev branch](#setting-up-the-dev-branch) if this is not the `master` branch.
1. Sign in to your Community and go to **Community Admin > System > API Apps**.
1. Create an app key and secret for your Community integration app following the steps in the [Get Authorization Credentials](https://github.com/lithiumtech/lia-sdk-android/wiki/Getting-Started-with-the-Community-Android-SDK#get-app-credentials) section of Getting Started with the Community Android SDK.
1. Clone [li-android-sdk-example](https://github.com/lithiumtech/lia-sdk-android/tree/master/lia-demo).
1. Open the project in Android Studio.
1. Add your credentials and the Community URL to `strings.xml`.

    ```xml
    <string name="clientName">PLACE YOUR CLIENT APP NAME HERE</string>
    <string name="clientId">PLACE YOUR COMMUNITY CLIENT ID HERE</string>
    <string name="clientSecret">PLACE YOUR COMMUNITY CLIENT SECRET HERE</string>
    <string name="communityUrl">PLACE YOUR COMMUNITY URL HERE</string>
    <string name="tenantId">PLACE COMMUNITY TENANT ID HERE</string>
    ```    
1. In Android Studio, click **Run**.

> To test push notifications, follow the instructions [here](https://github.com/lithiumtech/lia-sdk-android/wiki/Setting-up-push-notifications) to add 
your push notification server key to Community Admin.

> Remember to replace the `google-services.json` file in the project.

## License

Except as otherwise noted, the Community Android SDK and the Lithium Community Reference app are licensed under the Apache 2.0 License.

Copyright 2018 Lithium Technologies

## Setting up the dev branch

If this is not the `master` branch then the required SDK version may not be available
publicly yet and the build could fail or the application could crash. We can still run the
example application by publishing the `core` and `ui` SDK to our local maven repository.

Follow these instructions:

1. clone the [core sdk](https://github.com/lithiumtech/lia-sdk-android) project locally.
1. open the project in Android Studio.
1. via the terminal navigate into the project directory.
1. checkout the same branch as example application or alternatively the `dev` branch.
1. run the following command on lia-core module: `./gradlew clean assembleSnapshot uploadLocalArchives`

This will publish the core sdk to the local maven repository. By default the local maven repository
is at `~/.m2`. We can validate that the SDK is published by navigating to 
`<MAVEN_HOME>/repository/lithium/community/android/sdk/li-android-sdk-core`.

1. cd to lia-ui module
1. follow step 5 for the ui sdk also.

This will publish the ui sdk to the local maven repository. You can validate that the SDK is published by
navigating to `<MAVEN_HOME>/repository/lithium/community/android/sdk/li-android-sdk-ui`.

Now the required artifacts are available locally and we can build the example app and run it. Continue
to the [Getting Started](#getting-started) section.
