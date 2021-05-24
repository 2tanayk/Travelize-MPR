package com.myapp.travelize.models

data class Chat(
    var id: String? = null,
    var name: String? = null,
    var memberNames:MutableList<String> = mutableListOf(),
    var members:MutableList<String> = mutableListOf(),
    var passionIntersection:MutableList<String> = mutableListOf(),
    var url: String? = null,
    //client
    var lastUnreadMsg: String? = null,
    //client
    var unreadMsgCt: String? = null,
    //client
    var lastUnreadMsgTimestamp: String? = null
)
