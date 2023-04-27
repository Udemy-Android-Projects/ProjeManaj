package com.smartherd.projemanag.utils

// These are names of the fields in the document stored in the fireStore database
object Constants {
    const val USERS: String = "users"

    // TODO Updating the User Data Via HashMap in the FireStore Database (Step 5: Create constants for the key part of the hashmap)
    // Firebase database field names
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    // TODO Creating a Board Image (Step 7: Add constant variable for Boards.)
    // START
    // This  is used for the collection name for USERS.
    const val BOARDS: String = "boards"
    // END
    // TODO Preparing The Boards Recyclerview Adapter and UI Elements (Step 2: Add a field name as assignedTo which we are gonna use later on.)
    // START
    const val ASSIGNED_TO: String = "assignedTo"
    // END
    // TODO The TaskListActivity (Step 3: Add constant for DocumentId)
    // START
    const val DOCUMENT_ID: String = "documentId"
    // END
    // TODO Create Lists Inside a Board (Step 7: Add a new field for TaskList.)
    // START
    const val TASK_LIST: String = "taskList"
    // END
    // TODO Creating the Member Item and Toolbar (Step 1: Add constant for passing the board details through intent.)
    // START
    const val BOARD_DETAIL: String = "board_detail"
    // END
    // TODO Fetching and Displaying the Members of a Board (Step 3: Add field name as a constant which we will be using for getting the list of user details from the database.)
    // START
    const val ID: String = "id"
    // END
    const val EMAIL: String = "email"
    // TODO Loading Card Details to Set Card Title (Step 1: Add all the required constants for passing the details to CardDetailsActivity through intent.)
    // START
    const val TASK_LIST_ITEM_POSITION: String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION: String = "card_list_item_position"
    // END
}