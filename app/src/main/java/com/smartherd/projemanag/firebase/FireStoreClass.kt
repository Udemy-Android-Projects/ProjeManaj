package com.smartherd.projemanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smartherd.projemanag.R
import com.smartherd.projemanag.activities.*
import com.smartherd.projemanag.models.Board
import com.smartherd.projemanag.models.User
import com.smartherd.projemanag.utils.Constants

// TODO  Using The Firestore Database to Store User Details(Step 1: As you can see we are now authenticated by Firebase but for more inserting more details we need to use the DATABASE in Firebase.)
// START
// Before start with database we need to perform some steps in Firebase Console and add a dependency in Gradle file.
// Follow the Steps:
// Step 1: Go to the "Database" tab in the Firebase Console in your project details in the navigation bar under "Develop".
// Step 2: In the Database Page and Click on the Create Database in the Cloud Firestore in the test mode. Click on Next
// Step 3: Select the Cloud Firestore location and press the Done.
// Step 4: Now the database is created in the test mode and now add the cloud firestore dependency.
// Step 5: For more details visit the link: https://firebase.google.com/docs/firestore
// END

// TODO Using The Firestore Database to Store User Details (Step 3: Create a class where we will add the operation performed for the firestore database.)
class FireStoreClass : BaseActivity() {
    // Create a instance of Firebase Firestore
    private val mFireStore = Firebase.firestore

    // TODO Using The Firestore Database to Store User Details (Step 4: Create a function to make an entry of the registered user in the firestore database.)
    /**
     * A function to make an entry of the registered user in the firestore database.
     * The activity parameter is of type SighUpActivity since that is the activity that contains all the information about the user since the user details are entered at this point
     */
    fun registerUser(activity: SignUpActivity, userInfo: User) {
        Log.e("FireStore","has been accessed")
        // Create a collection/dataBase called 'users' as defined in the Constants class
        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID. Every single user will have their own document
            .document(userInfo.id)
                // SetOptions.merge() is a method provided by the Firebase Firestore SDK for Android that is used to merge data into an existing document or create a new document if it does not exist.
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                Log.e("FireStore", "has been accessed and is successful ")
                // Here call a function of base activity for transferring the result to it.
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(
                    "Didn't work",
                    "Error writing document",
                    e
                )
            }
        Log.e("FireStore", "done")
    }

    // TODO Signing In And Getting User Data(Step 1: Create a function to SignIn using firebase and get the user details from Firestore Database.)
    fun signedInUserDetails(activity: Activity) {  // TODO Loading the Image and Username to Display it in the Drawer (Step 1: We can use the same function to get the current logged in user details. As we need to modify only few things here where now any activity can be passed as a parameter)
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener {
                // This is the data structure,called document, that contains user information
                    document ->
                // TODO Signing In And Getting User Data (STEP 2: Pass the result to base activity.)
                // START
                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)!!
                // Since this method takes any activity as a parameter we have to differentiate between methods
                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is ProfileActivity -> {
                        // TODO Populating The Profile Activity With User Data (Step 3: Modify the parameter and check the instance of activity and send the success result to it.)
                        activity.setUserDataInUI(loggedInUser)
                    }
                }
            }
            .addOnFailureListener { e ->
                // TODO Loading the Image and Username to Display it in the Drawer (Step 2: Hide the progress dialog in failure function based on instance of activity.)
                // START
                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    // END
                }
                // END
                Log.e(
                    "Didn't work",
                    "Error writing document",
                    e
                )
            }
    }

    // TODO Updating the User Data Via HashMap in the FireStore Database (Step 2 : Create a function to update the user profile data into the database.)
    fun updateUserProfileData(activity: ProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS) // Collection Name
            .document(getCurrentUserID()) // Document ID
            .update(userHashMap) // A hashmap of fields which are to be updated.
            .addOnSuccessListener {
                // Profile data is updated successfully.
                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")

                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                // Notify the success result.
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )

            }
    }

    // TODO Creating a Board Image (Step 9: Create a function for creating a board and making an entry in the database.)
    // START
    fun createBoard(activity: CreateBoardActivity, board: Board) {
        mFireStore.collection(Constants.BOARDS) // Collection name
            .document() // No document ID needed in this case...it will be generated by firestore
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board created successfully.")
                Toast.makeText(activity, "Board created successfully.", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }

    // TODO Preparing The Boards Recyclerview Adapter and UI Elements (Step 3: Create a function to get the list of created boards from the database.)
    // START
    /**
     * A function to get the list of created boards from the database.
     */
    fun getBoardsList(activity: MainActivity) {  // This is the activity that calls this function
        // The collection name for BOARDS
        mFireStore.collection(Constants.BOARDS)
            // A where array query is used since we want the list of the board/boards in which the user is assigned. So here you can pass the current user id.
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { returnedDocument ->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, returnedDocument.documents.toString())
                // Here we have created a new instance for Boards ArrayList.
                val boardsList: ArrayList<Board> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in returnedDocument.documents) {
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id /** Initial position where board.documentId is set */
                    boardsList.add(board)
                }
                // Here pass the result to the base activity.
                activity.populateBoardsListToUI(boardsList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }

    // TODO The TaskListActivity (Step 6: Create a function to get the Board Details.)
    // START
    /**
     * A function to get the Board Details.
     */
    fun getBoardDetails(activity: TaskListActivity, documentId: String) {
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                // TODO Create Lists Inside a Board (Step 1: Assign the board document id to the Board Detail object)
                // START
                val board = document.toObject(Board::class.java)!!
                board.documentId = documentId /** board.documentId = documentId can also work since the value board.documentId is set when getting the board list */
                // Send the result of board to the TaskListActivity activity.
                activity.boardDetails(board) // The board object with the documentID is then passed at this point
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }

    // TODO Create Lists Inside a Board (Step 8: Create a function to add the task list in the board detail.)
    /**
     * A function to create a task list in the board detail.
     */
    // TODO Deleting And Updating Cards (Step 1: Change the functions parameters as required and also update the result based on the instance of activity.)
    fun addTaskList(activity: Activity, board: Board) {
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList
        // Here we only want to alter the taskList option in the board document, that is why we are using the update method instead of set
        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList updated successfully.")
                if(activity is TaskListActivity)
                    activity.addTaskListSuccess()
                else if(activity is CardDetailsActivity)
                    activity.addTaskListSuccess()
            }
            .addOnFailureListener { e ->
                if(activity is TaskListActivity)
                    activity.hideProgressDialog()
                else if(activity is CardDetailsActivity)
                    activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }
    /**
     * A function for getting the user id of current logged user.
     */
    override fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    // TODO Fetching and Displaying the Members of a Board (Step 4: Create a function for getting the list of users details from the database.)
    // START
    /**
     * A function to get the list of user details which is assigned to the board.
     */
    fun getAssignedMembersListDetails(activity: Activity, /* TODO Passing The Memberlist to The Card(Step 4: Change the function parameters as required and also pass the result based on activity instance by passing the general activity as a parameter.) */
                                      assignedTo: ArrayList<String>) {
        mFireStore.collection(Constants.USERS) // Collection Name
            .whereIn(Constants.ID, assignedTo) // Here the database field name and the id's of the members.
            .get()
            .addOnSuccessListener{ document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val usersList: ArrayList<User> = ArrayList()
                for (i in document.documents) {
                    // Convert all the document snapshot elements to the object using the data model class.
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }

                if(activity is MembersActivity)
                    activity.setupMembersList(usersList)
                if(activity is TaskListActivity) // TODO Passing The Memberlist to The Card
                    activity.boardMembersDetailList(usersList)

            }
            .addOnFailureListener { e ->
                if(activity is MembersActivity)
                    activity.hideProgressDialog()
                else if(activity is TaskListActivity)
                    activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while fetching members from in the board.",
                    e
                )

            }
    }

    // TODO Adding a New Member to a Board (Step 4: Create a function to get the user details from Firestore Database using the email address.)
    // START
    /**
     * A function to get the user details from Firestore Database using the email address.
     */
    fun getMemberDetails(activity: MembersActivity, email: String) {
        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener { document ->
                if (document.documents.size > 0) {
                    val user = document.documents[0].toObject(User::class.java)!! // Position 0 since we are getting only one result since the email is unique
                    // Here call a function of base activity for transferring the result to it.
                    activity.memberDetails(user)
                } else {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member found.")
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details",
                    e)
            }
    }
    // TODO Adding a New Member to a Board (Step 8: Create a function to assign a updated members list to board.)
    // START
    /**
     * A function to assign a updated members list to board.
     */
    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User) {
        // For updates to be successful we need a hash map
        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo
        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener{
                activity.memberAssignSuccess(user)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }
}