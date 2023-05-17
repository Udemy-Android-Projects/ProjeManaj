package com.smartherd.projemanag.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.smartherd.projemanag.R
import com.smartherd.projemanag.activities.MainActivity
import com.smartherd.projemanag.activities.SignInActivity
import com.smartherd.projemanag.firebase.FireStoreClass
import com.smartherd.projemanag.utils.Constants

// TODO Preparing the Notification Feature  (Step 2: Add the firebase Messaging Service class.)
// START
class mFirebaseMessagingService : FirebaseMessagingService() {
    // There are two types of messages data messages and notification messages. Data messages are handled
    // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
    // traditionally used with GCM(Google Cloud Messaging). Notification messages are only received here in onMessageReceived when the app
    // is in the foreground. When the app is in the background an automatically generated notification is displayed.
    // When the user taps on the notification they are returned to the app. Messages containing both notification
    // and data payloads are treated as notification messages.

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Handle FCM messages here. This enables us to know where the message came from
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${message.from}")

        // TODO Adding the Notifications (Step 7: Once the notification is sent successfully it will be received here.)
        // START
        // Check if message contains a data payload.
        message.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + message.data)
            // The Title and Message are assigned to the local variables
            val title = message.data[Constants.FCM_KEY_TITLE]
            val message = message.data[Constants.FCM_KEY_MESSAGE]

            // Send the notification to the user who is supposed to receive it
            sendNotification(title!!,message!!)

        }

        // Check if message contains a notification payload.
        message.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.e(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]


    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token : String) {

    }

    private fun sendNotification(title : String,messageBody : String /* TODO  Adding the Notifications (Step 6: Change the notification definition as add the parameters for title and message.)*/) {
        // TODO Adding the Notifications (Step 9: Now once the notification is received and visible in the notification tray than we can navigate them into the app as per requirement.)
        // As here we will navigate them to the main screen if user is already logged in or to the login screen.
        val intent = if(FireStoreClass().getCurrentUserID().isNotEmpty()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this,SignInActivity::class.java)
        }
        intent.addFlags( /** These flag are used to prevent activities from overlapping by ensuring that only one instance of an activity is open  */
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP) // Makes this activity the top most activity in the stack
        // The user might be in another application where a normal intent can't be used to start an activity. A pendingIntent is therefore used
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT /*This flag indicates that this intent should only be used once*/)
        val channelId = this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            // TODO Adding the Notifications (Step 8: Set the title and message for the notification which will be visible in the notification tray.)
            // START
            .setContentTitle(title)
            .setContentText(messageBody)
            // END
            .setAutoCancel(true) // When set to true the notification is automatically canceled when the user clicks it in the panel
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel Projemanag title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())

    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }


}