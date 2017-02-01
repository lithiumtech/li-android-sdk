
# Lithium Community Android SDK Readme

The Lithium Community Android SDK helps application developers integrate Lithium Community into Android applications, providing support for browsing boards, posting and interacting with messages, managing attachments, sending push notifications, and authenticating users.

* The Community Android SDK is written in Java
* The Community Android SDK packages are compatible with Android API level 19 and above
* We recommend installing the SDK using [Gradle](https://gradle.org/)

All native Lithium Community implementations using the Community Android SDK must use the `li-android-sdk-core` library. This library delivers the basic capabilities of authenticating and interacting with a community while adding support for third-party push notification providers.

The SDK also includes an optional Support UI library. (See the `li-android-sdk-ui` [package and readme](https://github.com/lithiumtech/li-android-sdk-ui).)

The SDK supports Lithium Registration authentication, LithiumSSO, and custom SSO implementations.

| Package | Provides |
| ------- | -------- |
| li-android-sdk-core | Authentication<br>API Providers<br>Custom API Providers<br>Push Notification Support (Firebase GCM) |

## Quickstart
This quick start provides the basic steps to get started using the Community Android SDK. For full instructions, see [Getting Started with the Community Android SDK](https://github.com/lithiumtech/li-android-sdk-core/wiki/Getting-Started-with-the-Community-Android-SDK).

### Requirements
The Community Android SDK requires:
* Access to a Lithium Community instance running version 17.1 or later
* A Community user account with the **Manage API Apps** permission grated
* Android Studio
* Authorization credentials: App Key and Secret (generated in **Community Admin > System > API Apps**)

### Get Started

1. Create an Android project.
1. Download and unpack the SDK resources to your local environment.
1. Add the AAR files in the `/apps/libs` folder for the application in your local file system.
1. In Android Studio, open the `build.gradle` for your project. It will be named something like `build.gradle (Project: MyApplication)`.
1. Add the following line in `allprojects` section of `build.gradle`.

    ```
    flatDir {
      dirs 'libs'
    }
    ```
1. Open the `build.gradle` file for the module. It will be named something like `build.gradle (Module.app)`.
1. Add the following to the dependencies section:

    ```
   //lithium-sdk libraries
   compile(name:'li-core-sdk-1.0', ext:'aar')
   compile(name:'li-sdk-ui-components-1.0', ext:'aar')
   //Third party depedencies required for
   compile 'com.android.support:customtabs:25.1.0'
   compile 'com.squareup.okhttp3:okhttp:3.5.0'
   compile 'com.android.support:appcompat-v7:25.1.0'
   compile 'com.android.support:design:25.1.0'
   compile 'com.android.support:support-v4:25.1.0'
   compile 'com.android.support:recyclerview-v7:25.1.0'
   compile 'com.google.code.gson:gson:2.8.0'
   compile 'com.squareup.picasso:picasso:2.5.2'
    ```
1. Add your app credentials to your project in  `/res/values/strings.xml`.

    ```xml
    <string name="clientId">API App Key Goes Here</string>
    <string name="clientSecret">App Secret Goes Here</string>
    <string name="communityURL">URL to your Community Goes Here</string>
    ```
1. Initialize your app with the following:
    
    ```java
    LiSDKManager.init(this, liAppCredentials);
    ```
1. Build your app credentials with one of the following.
    For Lithium Registration:
    
    ```java
    LiAppCredentials liAppCredentials = new LiAppCredentials.Builder()
            .setClientKey(getString(R.string.clientId))
            .setClientSecret(getString(R.string.clientSecret))
            .setCommunityUri(getString(R.string.communityURL))
            .setDeferredLogin(true).build();
    ```
    
    For LithiumSSO:
    
    ```java
    LiAppCredentials liAppCredentials = new LiAppCredentials.Builder("<SSO_TOKEN>")
            .setClientKey(getString(R.string.clientId))
            .setClientSecret(getString(R.string.clientSecret))
            .setCommunityUri(getString(R.string.communityURL))
            .setDeferredLogin(true).build();
    ```
Congratulations! You're app is initialized and you're ready to start developing.

## License
Except as otherwise noted, the Community Android SDK and the Lithium Community Reference app are licensed under the Apache 2.0 License.

Copyright 2017 Lithium Technologies

## Reference app
The Lithium Community Reference Application is a basic implementation of the Community Android SDK. The reference application shows the fastest way to get a Lithium Community backed application running quickly. You can download the reference app from Github.

## Getting help
Visit the [Current Betas Forum](https://community.lithium.com/t5/Current-betas/bd-p/BetaCurrent) on the Lithium Community to learn about our beta program, ask questions, and talk with other members of the Lithium Developer community.

## Next steps
* Review our [providers and models documentation](https://github.com/lithiumtech/li-android-sdk-core/wiki/Community-Android-SDK-API-providers)
* Read about our [Support UI components](https://github.com/lithiumtech/li-android-sdk-ui/wiki/Community-Android-SDK-UI-components)
* Learn about [authentication options](https://github.com/lithiumtech/li-android-sdk-core/wiki/Authentication-with-the-Community-Android-SDK)
* Create your [app key and secret credentials](https://github.com/lithiumtech/li-android-sdk-core/wiki/Getting-Started-with-the-Community-Android-SDK#get-authorization-credentials)
* Check out our [reference app](https://github.com/lithiumtech/li-android-sdk-example)
