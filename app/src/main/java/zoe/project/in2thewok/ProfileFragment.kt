package zoe.project.in2thewok

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import zoe.project.in2thewok.databinding.FragmentProfileBinding
import kotlinx.coroutines.Dispatchers as Dispatchers

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
//    private var param1: String? = null
//    private var param2: String? = null
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val postCollectionRef = Firebase.firestore.collection("posts")
    private val personCollectionRef = Firebase.firestore.collection("people")
    private val db = FirebaseFirestore.getInstance()
    private val docRef: DocumentReference? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

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
    private fun setQuestionAnswers() = CoroutineScope(Dispatchers.IO).launch{
        try{
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
            for(document in querySnapshot.documents){
                val person = document.toObject<Person>()
                a1.append(person?.q1)
                a2.append(person?.q2)
                a3.append(person?.q3)
                a4.append(person?.q4)
//                sb.append("$person\n") //append person followed by new line
            }
            // set string $ text to textview,
            // so switch the co-routine context as UI can only be modified inside Main dispatchers
            withContext(Dispatchers.Main){
                tvA1.text = a1.toString()
                tvA2.text = a2.toString()
                tvA3.text = a3.toString()
                tvA4.text = a4.toString()
//                tvPosts.text = sb.toString()
            }
        } catch (e: Exception){
            val context = context?.applicationContext
            withContext(Dispatchers.Main){
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun retrievePosts() = CoroutineScope(Dispatchers.IO).launch{

        try{
//            reference =
            val querySnapshot = postCollectionRef
                .whereEqualTo("userID", auth.currentUser?.uid)
                .get()
                .await()
////            val sb = StringBuilder()
            for(document in querySnapshot.documents){
                val post = document.toObject<Post>()
                val caption = (post?.caption)
                val imageURI = (post?.imageURI)?.toUri()
////                sb.append("$caption\n") //append person followed by new line
////                iv.setImageURI(imageURI)
                withContext(Dispatchers.Main){
//                    val iv = ImageView(context?.applicationContext)
//                    binding.clProfile.addView(iv)
//                    iv.layoutParams.height = 500
//                    iv.layoutParams.width = 500
//                    iv.x = 500F
//                    iv.y = 500F
//                    iv.
//                    val localUri
//                    val testImage = binding.testImage
//                    testImage.setImageURI(imageURI)
//                    binding.testImage.setImageURI(imageURI)
                //                    setBackgroundColor(Color.CYAN)

//                tvPosts.text = sb.toString()
                }
            }
//
//             set string $ text to textview,
//             so switch the co-routine context as UI can only be modified inside Main dispatchers

        } catch (e: Exception){
            val context = context?.applicationContext
            withContext(Dispatchers.Main){
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment HomeFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            HomeFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}