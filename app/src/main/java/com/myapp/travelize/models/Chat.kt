package com.myapp.travelize.models

data class Chat(
    var id: String? = null,
    var name: String? = null,
    var photoRef: String? = null,
    var lastUnreadMsg: String? = null,
    var unreadMsgCt: String? = null,
    var lastUnreadMsgTimestamp: String? = null
)
