package com.myapp.travelize.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Message(
    var senderName: String = "",
    var senderUid: String? = null,
    var msg: String? = null,
    var msgType: Int? = null,
    @ServerTimestamp
    var sent: Date? = null
)
