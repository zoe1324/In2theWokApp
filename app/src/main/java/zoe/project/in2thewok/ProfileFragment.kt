package zoe.project.in2thewok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import zoe.project.in2thewok.databinding.FragmentProfileBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val postCollectionRef = Firebase.firestore.collection("posts")
    private val personCollectionRef = Firebase.firestore.collection("people")
//    private val docRef: DocumentReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        setQuestionAnswers()
        retrievePosts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Add functionality for the TextViews to change to users answers
    private fun setQuestionAnswers() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val tvA1 = binding.tvA1
            val tvA2 = binding.tvA2
            val tvA3 = binding.tvA3
            val tvA4 = binding.tvA4
            val querySnapshot = personCollectionRef
                .whereEqualTo("userID", auth.currentUser?.uid)
                .get()
                .await()
            val a1 = StringBuilder()
            val a2 = StringBuilder()
            val a3 = StringBuilder()
            val a4 = StringBuilder()
            for (document in querySnapshot.documents) {
                val person = document.toObject<Person>()
                a1.append(person?.q1)
                a2.append(person?.q2)
                a3.append(person?.q3)
                a4.append(person?.q4)
//                sb.append("$person\n") //append person followed by new line
            }
            // set string $ text to textview,
            // so switch the co-routine context as UI can only be modified inside Main dispatchers
            withContext(Dispatchers.Main) {
                tvA1.text = a1.toString()
                tvA2.text = a2.toString()
                tvA3.text = a3.toString()
                tvA4.text = a4.toString()
//                tvPosts.text = sb.toString()
            }
        } catch (e: Exception) {
            val context = context?.applicationContext
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    // TODO: Put a recycler view in for posts??
    private fun retrievePosts() = CoroutineScope(Dispatchers.IO).launch {

        try {
//            reference =
            val querySnapshot = postCollectionRef
                .whereEqualTo("userID", auth.currentUser?.uid)
                .get()
                .await()
            var idCount = 1
            var current = binding.tvA4.id
            var post: Post?
            var postTitle: String?
            var imageURI: String?
            for (document in querySnapshot.documents) {

                post = document.toObject<Post>()
                postTitle = (post?.title)
                imageURI = (post?.imageURI)

                withContext(Dispatchers.Main) {
                    val iv = ImageView(context?.applicationContext)
                    val tv = TextView(context?.applicationContext)
                    binding.clProfile.addView(tv)
                    binding.clProfile.addView(iv)
                    tv.text = postTitle
                    tv.setPadding(50, 50, 50, 0)
                    tv.id = idCount
                    idCount++
                    iv.id = idCount
                    idCount++
                    val params = tv.layoutParams as ConstraintLayout.LayoutParams
                    params.startToStart = current
                    params.endToEnd = current
                    params.topToBottom = current
                    tv.requestLayout()
                    current = tv.id
                    params.width = WRAP_CONTENT
                    params.height = WRAP_CONTENT

                    iv.adjustViewBounds = true
                    iv.maxHeight = 700
                    iv.maxWidth = 700
                    val ivParams = iv.layoutParams as ConstraintLayout.LayoutParams
                    ivParams.startToStart = current
                    ivParams.endToEnd = current
                    ivParams.topToBottom = current
                    ivParams.width = WRAP_CONTENT
                    ivParams.height = WRAP_CONTENT
                    iv.setPadding(100, 100, 100, 50)
                    iv.requestLayout()

                    Picasso.get()
                        .load(imageURI)
                        .into(iv)
                    current = iv.id
                }
            }
        } catch (e: Exception) {
            val context = context?.applicationContext
            withContext(Dispatchers.Main) {
                if (context != null) {
                    if(!(e.message.equals(null )))
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
                println(e.message)
            }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}