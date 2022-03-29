package zoe.project.in2thewok

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import zoe.project.in2thewok.databinding.FragmentHomeBinding
import zoe.project.in2thewok.databinding.FragmentProfileBinding

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var recs = arrayListOf<Post>()
    private var bookmarked = arrayListOf<Post>()
    private lateinit var communicator: Communicator
    private lateinit var auth: FirebaseAuth

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

        binding.btnLogout.setOnClickListener{
            auth.signOut()
            communicator.signOut()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    inner class RecyclerAdapter(private val postArray: ArrayList<Post>,
                                val layout: Int): RecyclerView.Adapter<HomeFragment.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.init(postArray[position])
            holder.updateItems(postArray[position])
        }

        override fun getItemCount(): Int {
            return postArray.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var recipeTitle: TextView = itemView.findViewById(R.id.tvRecipeTitle)
        var recipeType: TextView = itemView.findViewById(R.id.tvRecipeDetails)
        var recipePhoto: ImageView = itemView.findViewById(R.id.ivRecipePhoto)

        fun init(post: Post){
            itemView.setOnClickListener{
                communicator.recipePassComm(post)
            }
        }

        fun updateItems(post: Post){
            recipeTitle.text = post.title.toString()
            recipeType.text = post.cuisineType.toString()
            if (post.imageURI != null) {
                Picasso.get()
                    .load(post.imageURI)
                    .into(recipePhoto)
            }
            else{
                Picasso.get()
                    .load(R.drawable.cooking)
                    .into(recipePhoto)
            }
        }
    }
}
