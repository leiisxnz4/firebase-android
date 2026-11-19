package com.franlop.android_firebase.model

data class Contact(
    var key: String? = null,
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    var uid: String = ""
)