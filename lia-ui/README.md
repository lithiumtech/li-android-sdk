# Lithium Community Android SDK UI

The `lia-ui` module package is an optional UI library that includes a support-focused Community workflow. The module includes set of activities and fragments supporting message lists, message posting, community browsing and navigation, and keyword search. For full details about the SDK components, see [Community Android SDK UI components](https://github.com/lithiumtech/lia-sdk-android/wiki/Community-Android-SDK-UI-components) and the [lia-ui Javadoc](https://lithiumtech.github.io/lia-sdk-android/)

The UI components module requires the `lia-core` module, and is compatible with Android Level 19 (4.4) and above.

### Getting started

1. Complete the tasks in the Quick Start section of the `lia-core` library readme.
2. Build an intent to start the `LiSupportHomeActivity`, like:
```java
private void startHelpAndSupport() {
    Intent i = new Intent(this, LiSupportHomeActivity.class);
    this.startActivity(i);
}
```
3. Invoke the intent in your app where appropriate, like:

```java
myButton.setOnClickListener(new View.OnClickListener() {
    public void onClick(View v) {
        startHelpAndSupport();
    }
});
```

### Activities index

| Activity | Description |
| -------- | ----------- |
| LiSupportHomeActivity | Main entry point for Community help and support actions. Includes header with search, an options menu, and two tabs for browsing and user subscriptions/posts |
| LiMessageListActivity | List of articles for the current board. |
| LiConversationActivity | Displays the message thread. |
| LiCreateMessageActivity | Creates a post form to create a message. |
| LiBrowseActivity | Creates a browse mechanism to navigate through the community structure. |
| LiSearchActivity | Creates a keyword search interaction and displays search results. |


### Fragment index

These fragments are used by the activities listed above. You can use them in custom activities as well.

| Fragment | Description |
| -------- | ----------- |
| LiMessageListFragment | Creates a list of articles for the selected board, including floated articles. |
| LiCreateMessageFragment | Creates the form for posting a message |
| LiBaseFragment | The abstract base fragment class for our UI elements. |
| LiBrowseFragment | Pulls the community structure and builds a navigation component to browse community categories and boards down to the message level. |
| LiConversationFragment | Pulls the selected conversation data (the thread topic and replies). Creates reply, kudo, and accept-as-solution functionality. |
| LiOnArticleRowClickListener | A listener for taps on rows on screens that show messages lists to bring up the message view. This is not used standalone. It is used by other fragments (LiUserActivityFragment, LiSearchFragment, LiMessageListFragment). |
| LiUserActivityFragment | Pulls the list messages posted by the current user, as well as the user's subscriptions. Builds a message list for display and creates the mechanism to tap to display the message view. Creates a button to create a new post. |
| LiSearchFragment | Creates the keyword search input for searching the community and displays the search results. |

### License

Except as otherwise notified, the Community Android SDK and the Lithium Community Reference app are licensed under the Apache 2.0 License.

Copyright 2018 Lithium Technologies

### Example app

The [Lithium Community Example Application](https://github.com/lithiumtech/lia-sdk-android/tree/master/lia-demo) is a basic implementation of the Community Android SDK. The reference application shows the fastest way to get a Lithium Community backed application running quickly. You can download the reference app from Github.

### Getting help

Visit the [Developers Discussion forum](https://community.lithium.com/t5/Developers-Discussion/bd-p/studio) on the Lithium Community to ask questions and talk with other members of the Lithium Developer community.

### Next steps

* Review our [providers and models documentation](https://github.com/lithiumtech/lia-sdk-android/wiki/Community-Android-SDK-API-providers)
* Learn about [authentication options](https://github.com/lithiumtech/lia-sdk-android/wiki/Authentication-with-the-Community-Android-SDK)
* Create your [app key and secret credentials](https://github.com/lithiumtech/lia-sdk-android/wiki/Getting-Started-with-the-Community-Android-SDK#get-authorization-credentials)
* Check out our [example app](https://github.com/lithiumtech/lia-sdk-android/tree/master/lia-demo)
