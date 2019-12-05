# Tourgether

## Setup instructions
#### place your firebase api key file ```google-services.json``` to ```/app```

 1. ![](https://i.imgur.com/bRHyuux.png)
 2. ![](https://i.imgur.com/L9hxAo6.png)

## Notable Features
### Cooperative Database
 - Firebase
     - store all data except checkin pictures
     - store spot pictures
     - managing user accoutns
 - Web server
     - store checkin pictures
     - send hot spot notification and hot checkin notification (See **Notification and News**)

### Dual Versions
Tourgether has two different versions, community version and google comment versoin.
To switch version, comment out either of following lines in ```MyApplication.java```
```java
public static final String VERSION_OPTION = VERSION_ALL_FEATURE; // community version
public static final String VERSION_OPTION = VERSION_ONLY_GOOGLE_COMMENT; // google comment version
```

### Notifications and News
While notification is handled by Android system, Tourgether will store the notification content in the application using ```NewsFragment.java```
There are five types of notifications and news.
 - Hot spot
     - Definition: When many users post checkins in a specific spot
     - Example: 10 users post checkins in MRT station
     - Send by: Web server
     - Target: All users
 - Hot checkin
     - Definition: When a post receives many likes from other users
     - Example: A checkin receives 15 likes
     - Send by: Web server
     - Target: All users
 - Like
     - Definition: A user's post receives like from another user
     - Example: User1 presses like to a post of User0
     - Send by: User1
     - Target: User0
 - Comment
     - Definition: A user's post receives a comment from another user
     - Example: User1 leaves a comment to a post of User0
     - Send by: User1
     - Target: User0
 - Collected
     - Definition: A user's post is collected by another user
     - Example: User1 presses collect to a post of User0
     - Send by: User1
     - Target: User0

## Confusing Terms in the Code
 - spot : The given tourist spots. In Tourgether, there are 52 spots.
 - collect : star(收藏)
