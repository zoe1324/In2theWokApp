package zoe.project.in2thewok

import android.net.Uri
import android.provider.MediaStore
import java.net.URI

data class Post(
    var userID: String = "",
    var username: String = "",
    var imageURI: String? = "",
    var video: MediaStore.Video? = null,
    var title: String? = "",
    var ingredients: List<String?>,
    var cuisineType: String? = "",
    var steps: List<String?>,
    var story: String? = ""
)