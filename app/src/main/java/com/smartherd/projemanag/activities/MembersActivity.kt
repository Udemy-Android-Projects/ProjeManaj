package com.smartherd.projemanag.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartherd.projemanag.R
import com.smartherd.projemanag.adapter.MembersAdapter
import com.smartherd.projemanag.databinding.ActivityMembersBinding
import com.smartherd.projemanag.databinding.DialogSearchMemberBinding
import com.smartherd.projemanag.firebase.FireStoreClass
import com.smartherd.projemanag.models.Board
import com.smartherd.projemanag.models.User
import com.smartherd.projemanag.utils.Constants
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MembersActivity : BaseActivity() {
    lateinit var binding: ActivityMembersBinding
    // TODO Creating the Member Item and Toolbar (Step 3: Create a global variable for Board Details.)
    // START
    // A global variable for Board Details.
    private lateinit var mBoardDetails: Board
    // END
    // TODO Adding a New Member to a Board (Step 1: A global variable for Users List.)
    // START
    // A global variable for Assigned Members List.
    private lateinit var mAssignedMembersList:ArrayList<User>
    // END

    // TODO Reloading Board Details On Change (Step 3: Declare a global variable for notifying any changes done or not.)
    // START
    // A global variable for notifying any changes done or not in the assigned members list.
    private var anyChangesDone: Boolean = false
    // END

    // Dialog binding
    lateinit var dialogBinding: DialogSearchMemberBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()
        // Change the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.colorPrimary)
        }

        // TODO Creating the Member Item and Toolbar (Step 4: Get the Board Details through intent and assign it to the global variable.)
        // START
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!! // We are retrieving the entire board object
        }
        // END
        // TODO Fetching and Displaying the Members of a Board (Step 5: Get the members list details from the database.)
        // START
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.fetching_members))
        FireStoreClass().getAssignedMembersListDetails(
            this@MembersActivity,
            mBoardDetails.assignedTo
        )
        // END
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarMembersActivity)
        val actionBar = supportActionBar
        val typeFace : Typeface = Typeface.createFromAsset(assets, "backoutwebwebfont.ttf")
        binding.toolbarTitle.text = resources.getText(R.string.members)
        binding.toolbarTitle.typeface = typeFace
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = null
        }
        binding.toolbarMembersActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    // TODO Fetching and Displaying the Members of a Board (Step 2: Create a function to setup assigned members list into recyclerview.)
    // START
    /**
     * A function to setup assigned members list into recyclerview.
     */
    fun setupMembersList(list: ArrayList<User>) {
        // TODO Adding a New Member to a Board (Step 2: Initialize the Assigned Members List.)
        // START
        mAssignedMembersList = list
        // END
        hideProgressDialog()
        binding.rvMembersList.layoutManager = LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)
        val adapter = MembersAdapter(this,list)
        binding.rvMembersList.adapter = adapter
    }

    // TODO Adding a New Member to a Board  (Step 3: Here we will get the result of the member if it found in the database.)
    // START
    fun memberDetails(user: User) {
        // TODO Adding a New Member to a Board  (Step 6: Here add the user id to the existing assigned members list of the board.)
        // START
        mBoardDetails.assignedTo.add(user.id)
        // TODO Adding a New Member to a Board (Step 9: Finally assign the member to the board in the database.)
        // START
        FireStoreClass().assignMemberToBoard(this@MembersActivity, mBoardDetails, user)
        // END
    }

    // TODO Adding New Members - UI (Step 4: Initialize the dialog for searching member from Database.)
    // START
    /**
     * Method is used to show the Custom Dialog.
     */
    private fun dialogSearchMember() {
        val dialog = Dialog(this)
        // Inflate dialog
        dialogBinding = DialogSearchMemberBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.tvAdd.setOnClickListener {
            val email = dialogBinding.etEmailSearchMember.text.toString()
            if(email.isNotEmpty()) {
                // TODO Adding a New Member to a Board (Step 5: Get the member details from the database.)
                // START
                // Show the progress dialog.
                showProgressDialog(resources.getString(R.string.please_wait))
                FireStoreClass().getMemberDetails(this@MembersActivity, email)
                // END
                dialog.dismiss()
            } else {
                Toast.makeText(
                    this@MembersActivity,
                    "Please enter members email address.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        dialogBinding.tvCancel.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    // TODO Adding New Members - UI (Step 3: Inflate the menu file for adding the member and also add the onOptionItemSelected function.)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_member -> {
                // Display dialog
                // TODO Adding New Members - UI (Step 5: Call the dialogSearchMember function here.)
                // START
                dialogSearchMember()
                // END
                return true /** Don't forget this */
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // TODO Adding a New Member to a Board (Step 7: Initialize the dialog for searching member from Database.)
    // START
    /**
     * A function to get the result of assigning the members.
     */
    fun memberAssignSuccess(user: User) {
        hideProgressDialog()
        mAssignedMembersList.add(user)
        // TODO Reloading Board Details On Change (Step 4: Here the list is updated so change the global variable which we have declared for notifying changes.)
        // START
        anyChangesDone = true
        // END
        setupMembersList(mAssignedMembersList)

        // TODO Adding the Notifications (Step 5: Call the AsyncTask class when the board is assigned to the user and based on the users detail send them the notification using the FCM token.)
        // START
        SendNotificationToUserAsyncTask(mBoardDetails.name, user.fcmToken).execute()
        // END
    }

    // TODO Reloading Board Details On Change (Step 5: Send the result to the base activity onBackPressed.)
    override fun onBackPressed() {
        if (anyChangesDone) {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    // Async tasks are commonly used in scenarios where a long-running task needs to be performed in the background without blocking the main thread

    // TODO  Adding the Notifications (Step 2: Create a AsyncTask class for sending the notification to user based on the FCM Token.)
    // START
    /**
     * “A nested class marked as inner can access the members of its outer class.
     * Inner classes carry a reference to an object of an outer class:”
     * source: https://kotlinlang.org/docs/reference/nested-classes.html
     *
     * This is the background class is used to execute background task.
     *
     * For Background we have used the AsyncTask
     *
     * Asynctask : Creates a new asynchronous task. This constructor must be invoked on the UI thread.
     */
    @SuppressLint("StaticFieldLeak")
    private inner class SendNotificationToUserAsyncTask(val boardName: String, val token: String) :
        AsyncTask<Any, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog("Please wait...")
        }

        override fun doInBackground(vararg params: Any?): String {
            var result : String

            var connection: HttpURLConnection? = null

            try {
                val url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                /**
                 * A URL connection can be used for input and/or output.  Set the DoOutput
                 * flag to true if you intend to use the URL connection for output,
                 * false if not.  The default is false.
                 */
                connection.doInput = true
                connection.doOutput = true
                /**
                 * Sets whether HTTP redirects should not be automatically followed by this instance.
                 * The default value comes from followRedirects, which defaults to true.
                 *
                 * When an HTTP client sends a request to an HTTP server and the server responds with a redirect status code (e.g. 301 or 302)
                 * along with a Location header indicating the new URL to which the client should be redirected,
                 * the client may automatically follow the redirect to the new URL. This is called "following redirects".
                 */
                connection.instanceFollowRedirects = false
                /**
                 * Set the method for the URL request, one of:
                 *  POST
                 */
                connection.requestMethod = "POST"
                /**
                 * Sets the general request property. If a property with the key already
                 * exists, overwrite its value with the new value.
                 */
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                // TODO Adding the Notifications (Step 3: Add the firebase Server Key.)
                // START
                // In order to find your Server Key or authorization key, follow the below steps:
                // 1. Goto Firebase Console.
                // 2. Select your project.
                // 3. Firebase Project Setting
                // 4. Cloud Messaging
                // 5. Finally, the SerkeyKey.
                // For Detail understanding visit the link: https://android.jlelse.eu/android-push-notification-using-firebase-and-advanced-rest-client-3858daff2f50
                connection.setRequestProperty(
                    Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"
                    /** The key value pair above is : {authorization, key = serverKey} */
                )
                /**
                 * Some protocols do caching of documents.  Occasionally, it is important
                 * to be able to "tunnel through" and ignore the caches (e.g., the
                 * "reload" button in a browser).  If the UseCaches flag on a connection
                 * is true, the connection is allowed to use whatever caches it can.
                 *  If false, caches are to be ignored.
                 *  The default value comes from DefaultUseCaches, which defaults to
                 * true.
                 */
                connection.useCaches = false

                /**
                 * Creates a new data output stream to write data to the specified
                 * underlying output stream. The counter written is set to zero.
                 */
                val wr = DataOutputStream(connection.outputStream)
                // TODO Adding the Notifications (Step 4: Create a notification data payload.)
                // START
                // Create JSONObject Request
                val jsonRequest = JSONObject()
                // Create a data object
                val dataObject = JSONObject()
                // Here you can pass the title as per requirement as here we have added some text and board name. The title is what the user will see when they open the notification
                dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the Board $boardName")
                // Here you can pass the message as per requirement as here we have added some text and appended the name of the Board Admin.
                dataObject.put(
                    Constants.FCM_KEY_MESSAGE,
                    "You have been assigned to the new board by ${mAssignedMembersList[0].name}"
                )
                // Here add the data object and the user's token in the jsonRequest object.
                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO, token)
                // END

                /**
                 * Writes out the string to the underlying output stream as a
                 * sequence of bytes. Each character in the string is written out, in
                 * sequence, by discarding its high eight bits. If no exception is
                 * thrown, the counter written is incremented by the
                 * length of s.
                 */
                wr.writeBytes(jsonRequest.toString())
                wr.flush() // Flushes this data output stream.
                wr.close() // Closes this output stream and releases any system resources associated with the stream

                val httpResult: Int =
                    connection.responseCode // Gets the status code from an HTTP response message.

                if (httpResult == HttpURLConnection.HTTP_OK) {
                    /**
                     * Returns an input stream that reads from this open connection.
                     */
                    val inputStream = connection.inputStream

                    /**
                     * Creates a buffering character-input stream that uses a default-sized input buffer.
                     */
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val sb = StringBuilder()
                    var line: String?
                    try {
                        /**
                         * Reads a line of text.  A line is considered to be terminated by any one
                         * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
                         * followed immediately by a linefeed.
                         */
                        while (reader.readLine().also { line = it } != null) /** This simply means that read a line
                                                                            if there are lines to read and that the line should not be null */
                        {
                            sb.append(line + "\n")
                        }
                    } catch(e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            inputStream.close()
                        } catch (e : IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()
                } else {
                    result = connection.responseMessage
                }
            } catch (e : SocketTimeoutException) {
                result = "Connection Timeout"
            } catch (e : Exception) {
                result = "Error : " + e.message
            } finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            hideProgressDialog()
        }

    }

}