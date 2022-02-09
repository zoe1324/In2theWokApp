package zoe.project.in2thewok

import android.provider.MediaStore
import java.net.URI

data class Post (
    var userID: String = "",
    var username: String = "",
//    var imageURI: URI,
//    var video: MediaStore.Video,
    var caption: String = ""
)