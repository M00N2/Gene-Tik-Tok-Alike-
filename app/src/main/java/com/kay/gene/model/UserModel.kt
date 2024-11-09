package com.kay.gene.model

data class UserModel(
    var id: String = "",
    var email: String = "",
    var username: String = "",
    var profilePic: String = "",
    var followerList: MutableList<String> = mutableListOf(),//empty list in beginning of followers
    var followingList: MutableList<String> = mutableListOf()//empty list in beginning of following
)
