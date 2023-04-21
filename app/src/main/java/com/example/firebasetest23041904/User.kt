package com.example.firebasetest23041904

import java.io.Serializable

data class User(
    var userKey: String = "", var userNo: String = "",
    var userName: String = "", var userAge: String = ""
): Serializable
