
# Lithium Community Android SDK

The Lithium Community Android SDK helps application developers integrate Lithium Community into Android applications, providing support for browsing boards, posting and interacting with messages, managing attachments, sending push notifications, and authenticating users.

* The Community Android SDK is written in Java
* The Community Android SDK packages are compatible with Android API level 19 and above
* We recommend installing the SDK using [Gradle](https://gradle.org/)

All native Lithium Community implementations using the Community Android SDK must use the `li-android-sdk-core` library. This library delivers the basic capabilities of authenticating and interacting with a community while adding support for third-party push notification providers.

The SDK also includes an optional Support UI library. (See the `li-android-sdk-ui` [package and readme](https://github.com/lithiumtech/li-android-sdk-ui).)

The SDK supports Lithium Registration authentication, LithiumSSO, and custom SSO implementations.

| Package | Provides |
| ------- | -------- |
| li-android-sdk-core | Authentication<br>API Providers<br>Custom API Providers<br>Push Notification Support (Firebase Cloud Messaging) |

### Requirements

The Community Android SDK requires:

* Access to a Lithium Community instance running version 18.4 or later
* A Community user account with the **Manage API Apps** permission grated
* Android Studio
* Authorization credentials: App Key and Secret (generated in **Community Admin > System > API Apps**)

### Get Started

To get started with Community Android SDK development, see the instructions in [Getting Started with the Community Android SDK](https://github.com/lithiumtech/li-android-sdk-core/wiki/Getting-Started-with-the-Community-Android-SDK).

The guide will take you through generating credentials, building your AARs, adding required dependencies, and app initialization.

### License

Except as otherwise noted, the Community Android SDK and the Lithium Community Reference app are licensed under the Apache 2.0 License.

Copyright 2018 Lithium Technologies

### Example app

The [Lithium Community Android SDK Example](https://github.com/lithiumtech/li-android-sdk-example/blob/dev/README.md) is a basic implementation of the Community Android SDK. The reference application shows the fastest way to get a Lithium Community backed application running quickly.

### Getting help

Visit the [Developers Discussion forum](https://community.lithium.com/t5/Developers-Discussion/bd-p/studio) on the Lithium Community to ask questions and talk with other members of the Lithium Developer community.

### Next steps

* Review our [providers and models documentation](https://github.com/lithiumtech/lia-sdk-android/wiki/API-Reference#about-the-api-providers)
* Review the [li-android-sdk Javadoc](https://lithiumtech.github.io/li-android-sdk/)
* Run through our [tutorials](https://github.com/lithiumtech/lia-sdk-android/wiki/Tutorials)
* Read about our [Support UI components](https://github.com/lithiumtech/lia-sdk-android/wiki/Support-UI-Components)
* Learn about [authentication options](https://github.com/lithiumtech/lia-sdk-android/wiki/Authenticating-a-user)
* Create your [app key and secret credentials](https://github.com/lithiumtech/lia-sdk-android/wiki/Getting-Started#getting-app-credentials)
* Check out our [reference app](https://github.com/lithiumtech/lia-sdk-android/tree/master/lia-demo)

### Status

[![Build Status](https://travis-ci.org/lithiumtech/lia-sdk-android.svg?branch=master)](https://travis-ci.org/lithiumtech/lia-sdk-android)
