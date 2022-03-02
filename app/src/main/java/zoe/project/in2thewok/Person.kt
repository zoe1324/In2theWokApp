package zoe.project.in2thewok

//userPosts: Contains a list of post ids to link the user to their posts
//userBookmarks: Contains a list of post ids to link the user to posts they have bookmarked

data class Person (
    var username: String = "",
    var q1: String = "",
    var q2: String = "",
    var q3: String = "",
    var q4: String = "",
//    val userPosts: MutableList<String> = mutableListOf(),
//    val userBookmarks: List<String> = mutableListOf(),
    val userID: String = "",
    val bookmarks: ArrayList<String> = arrayListOf()
)