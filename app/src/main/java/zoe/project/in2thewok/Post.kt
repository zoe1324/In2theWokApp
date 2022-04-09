package zoe.project.in2thewok

import android.provider.MediaStore

/**
 * [Post] data class, used as an object structure representing
 * a post and its attributes.
 *
 * @param postID The DocumentID of the post instance, a document reference for Firestore posts Collection
 * @param userID The FirebaseAuth userID of the user that made the post, links to their FirebaseAuth account to person collection instance
 * @param username The poster's displayName, not yet displayed with the post
 * @param imageURI Remote URI, the Firebase Storage URL of the attached photo of a recipe, value is null if not included in post
 * @param video Video parameter, not included in implementation, remains for future development, null on default
 * @param title Title of the recipe post
 * @param ingredients A list of recipe ingredients
 * @param cuisineType The recipe's cuisine type, e.g. Italian
 * @param steps A list of recipe steps
 * @param story The written memory/story associated with the recipe
 * @param comments A list of user comments made by other users, initially empty
 */
data class Post(
    var postID: String? = null,
    var userID: String? = "",
    var username: String? = "",
    var imageURI: String? = "",
    var video: MediaStore.Video? = null,
    var title: String? = "",
    var ingredients: ArrayList<String>? = arrayListOf(),
    var cuisineType: String? = "",
    var steps: ArrayList<String>? = arrayListOf(),
    var story: String? = "",
    var comments: ArrayList<String>? = arrayListOf()
)
