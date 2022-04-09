package zoe.project.in2thewok

/**
 * [Person] data class, used as an object structure representing
 * a user and their details.
 *
 * @param username The user's displayName, appears when they comment on a recipe
 * @param q1 The user's answer to questionnaire question one
 * @param q2 The user's answer to questionnaire question two
 * @param q3 The user's answer to questionnaire question three
 * @param q4 The user's answer to questionnaire question four
 * @param userID The user's FirebaseAuth userID, links to their FirebaseAuth account to person collection instance
 * @param bookmarks A list of postIDs, representing user's bookmarked posts, initially empty
 */
data class Person (
    var username: String = "",
    var q1: String = "",
    var q2: String = "",
    var q3: String = "",
    var q4: String = "",
    val userID: String = "",
    val bookmarks: ArrayList<String> = arrayListOf()
)
