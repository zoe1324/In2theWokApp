package zoe.project.in2thewok

data class Person (
    var username: String = "",
    var q1: String = "",
    var q2: String = "",
    var q3: String = "",
    var q4: String = "",
    val userID: String = "",
    val bookmarks: ArrayList<String> = arrayListOf()
)
