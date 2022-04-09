package zoe.project.in2thewok

/**
 * [Communicator] Interface acts as a communicator between Fragment classes
 * and [HomeActivity], instance must be declared inside Fragment for use.
 * All methods implemented in [HomeActivity].
 */
interface Communicator {

    /**
     * Used in both [HomeFragment] and [ProfileFragment] to pass a Post object to [HomeActivity],
     * [HomeActivity] passes this to [RecipeFragment] to display the details of full post.
     *
     * @param post The current recipe Post instance
     */
    fun recipePassComm(post: Post)

    /**
     * Used by [AddFragment], to update the list of User posts to be displayed by [ProfileFragment]
     * after a user post is made.
     */
    fun updatePostList()

    /**
     * Used by [RecipeFragment] when user bookmarks a recipe, passes the new current list
     * of postIDs over to [HomeActivity] to update the list of Posts.
     *
     * @param bookmarksList The updated list of postIDs of the user's bookmarks.
     */
    fun updateBookmarkList(bookmarksList: ArrayList<String>?)

    /**
     * Used by [HomeFragment] when user taps 'Log out' button, communicates to [HomeActivity]
     * to sign out.
     */
    fun signOut()
}
