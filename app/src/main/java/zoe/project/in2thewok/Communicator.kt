package zoe.project.in2thewok

/**
 * [Communicator] Interface acts as a communicator between Fragment classes
 * and [HomeActivity], instance must be declared inside Fragment for use.
 */
interface Communicator {
    fun recipePassComm(post: Post)
    fun updatePostList()
    fun updateBookmarkList(bookmarksList: ArrayList<String>?)
    fun signOut()
}
