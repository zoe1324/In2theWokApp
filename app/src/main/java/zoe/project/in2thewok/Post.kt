package zoe.project.in2thewok

import android.net.Uri
import android.provider.MediaStore
import java.net.URI

data class Post(
    var userID: String = "",
    var username: String = "",
    var imageURI: String? = "",
//    var video: MediaStore.Video,
    var caption: String? = ""
)