package zoe.project.in2thewok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import zoe.project.in2thewok.databinding.FragmentHomeBinding

/**
 * [HomeFragment], A [Fragment] class displayed by [HomeActivity],
 * linked via [FragmentHomeBinding]. Class displays Recommendations and User Bookmarks using
 * two RecyclerViews, managed by RecyclerView Adapter. Both post lists
 * are passed to recycler adapters.
 *
 * @constructor Creates a new Fragment
 */
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var recs = arrayListOf<Post>()
    private var bookmarked = arrayListOf<Post>()
    private lateinit var communicator: Communicator
    private lateinit var auth: FirebaseAuth

    /**
     * Creates View for [HomeFragment]
     *
     * @param inflater Layout inflater for [Fragment]
     * @param container Container ViewGroup for [Fragment]
     * @param savedInstanceState contains any data passed to the fragment via Bundle
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = Firebase.auth
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.rvRecs.hasFixedSize()
        binding.rvRecs.layoutManager = LinearLayoutManager(context)
        binding.rvRecs.itemAnimator = DefaultItemAnimator()
        recs = (activity as? HomeActivity)!!.recPosts
        binding.rvRecs.adapter = RecyclerAdapter(recs, R.layout.layout_recipe)
        bookmarked = (activity as? HomeActivity)!!.bookmarkedPosts
        binding.rvBookmarks.hasFixedSize()
        binding.rvBookmarks.layoutManager = LinearLayoutManager(context)
        binding.rvBookmarks.itemAnimator = DefaultItemAnimator()
        binding.rvBookmarks.adapter = RecyclerAdapter(bookmarked, R.layout.layout_recipe)
        communicator = activity as Communicator
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            communicator.signOut()
        }
        return binding.root
    }

    /**
     * Destroys the Fragment View so it is no longer visible
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * [RecyclerAdapter] customised class, adapts arrayList of Post instances to fit CardView items
     *
     * @param postArray The arrayList of Posts being adapted
     * @param layout The layout in which each post should be adapted to fit
     * @constructor Creates a new Adapter for the RecyclerView
     */
    inner class RecyclerAdapter(
        private val postArray: ArrayList<Post>,
        val layout: Int
    ) : RecyclerView.Adapter<HomeFragment.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return ViewHolder(view)
        }

        /**
         * Updates the ViewHolder according to a given position
         *
         * @param holder The ViewHolder being updated
         * @param position THe position in the array in which the current item is being adapted to fit
         */
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.init(postArray[position])
            holder.updateItems(postArray[position])
        }

        /**
         * Returns the size of the arrayList being adapted
         *
         * @return postArray.size
         */
        override fun getItemCount(): Int {
            return postArray.size
        }

        /**
         * Returns the id of the item in the arrayList being adapted
         *
         * @param position
         * @return position.toLong()
         */
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        /**
         * Returns item view type
         *
         * @param position The current array position
         * @return position
         */
        override fun getItemViewType(position: Int): Int {
            return position
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recipeTitle: TextView = itemView.findViewById(R.id.tvRecipeTitle)
        var recipeType: TextView = itemView.findViewById(R.id.tvRecipeDetails)
        var recipePhoto: ImageView = itemView.findViewById(R.id.ivRecipePhoto)

        /**
         * Initialises each item click listener within the RecyclerView
         *
         * @param post The current post in the list being passed in
         */
        fun init(post: Post) {
            itemView.setOnClickListener {
                communicator.recipePassComm(post)
            }
        }

        /**
         * Updates each RecyclerView item to display the content contained within the post instance
         *
         * @param post The current post in the list being passed in
         */
        fun updateItems(post: Post) {
            recipeTitle.text = post.title.toString()
            recipeType.text = post.cuisineType.toString()
            if (post.imageURI != null) {
                Picasso.get()
                    .load(post.imageURI)
                    .into(recipePhoto)
            } else {
                Picasso.get()
                    .load(R.drawable.cooking)
                    .into(recipePhoto)
            }
        }
    }
}
