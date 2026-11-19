package com.franlop.android_firebase.model

data class Note(
    var key: String? = null,
    val title: String = "",
    val content: String = "",
    var uid: String = ""
)