Love2Brew App

***********************************  Overview  ***********************************

App loads JSON data and png images from a remote web service API

The main activity loads a list adapter with Coffee Brewers

Once a brewer is selected, a tabbed activity is launched

The tabbed activity displays 3 tabs of information on the selected coffee brewer

The menu has a button to return to the main activity

The menu has a button to open a dialog to "Set a Reminder"

The reminder dialog allows 12 hour, 24 hour, 5 minute, and 90 second reminders

The reminder is loaded to the alarm manager service, when the receiver is notified,
a pending intent in launched to the Notification Service, displaying a notification
that "Coffee is Ready"

***********************************  Features  ***********************************

The following features of Android were exercised in the app:

    - Layout Design - XML
    - ListView and Relative Layout
    - Custom ListView Adapter implementation
    - HTTP URL Connections to download JSON Arrays
    - De-serializing JSON Array to JSON Objects
    - MVC - Definition of Model Class for Data / Controllers in MainActivity
    - Asynchronous task for image download
    - HTTP URL Connections for image downloads
    - File Objects / writing and reading to storage
    - Tabbed Activity
    - Explicit Activity start
    - Dialog Fragments
    - menu design
    - onClick event handlersstart activities with data
    - Manifest Permissions - Read storage, Write Storage, Vibrate
    - Notifications, AlarmNotification Receivers, Pending Intents
    - Programmatic display of images

