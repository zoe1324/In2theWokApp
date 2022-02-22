package zoe.project.in2thewok

import android.net.Uri
import android.provider.MediaStore
import java.net.URI
import java.util.*

data class Post(
    var userID: String = "",
    var username: String = "",
    var imageURI: String? = "",
    var video: MediaStore.Video? = null,
    var title: String? = "",
    var ingredients: List<String?> = listOf(),
    var cuisineType: String? = "",
    var steps: List<String?> = listOf(),
    var story: String? = ""
)
