package com.flius.guiddy

data class Profile(
    var nickname: String,
    var country: String,
    var sex: String,
    var language: String,
    val profileImageUrl: String,
    val uid: String
){
    constructor(): this("", "", "","", "","")
}
