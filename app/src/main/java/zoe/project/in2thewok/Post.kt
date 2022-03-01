package zoe.project.in2thewok

import android.provider.MediaStore

data class Post(
    var postID: String? = null,
    var userID: String = "",
    var username: String = "",
    var imageURI: String? = "",
    var video: MediaStore.Video? = null,
    var title: String? = "",
    var ingredients: ArrayList<String> = arrayListOf(),
    var cuisineType: String? = "",
    var steps: ArrayList<String> = arrayListOf(),
    var story: String? = "",
    var comments: ArrayList<String> = arrayListOf()
)
