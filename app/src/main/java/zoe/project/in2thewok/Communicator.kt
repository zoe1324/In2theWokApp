package zoe.project.in2thewok

interface Communicator {
    fun recipePassComm(post: Post)
    fun updatePostList()
    fun updateBookmarkList(bookmarksList: ArrayList<String>?)
    fun updateRecList()
    fun signOut()
//    fun updateComments()
}