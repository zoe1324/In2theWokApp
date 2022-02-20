package zoe.project.in2thewok

import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.net.toUri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.rpc.context.AttributeContext
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import zoe.project.in2thewok.databinding.FragmentProfileBinding
import java.io.File
import java.lang.Byte.decode
import java.net.URLDecoder
import java.net.URLEncoder.encode
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
//    private val docRef: DocumentReference? = null
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
    private fun retrievePosts() = CoroutineScope(Dispatchers.IO).launch {

        try {
//            reference =
            val querySnapshot = postCollectionRef
                .whereEqualTo("userID", auth.currentUser?.uid)
                .get()
                .await()
            var idCount = 1
            for(document in querySnapshot.documents){
                var post = document.toObject<Post>()
                var caption = (post?.caption)
                var imageURI = (post?.imageURI)
                withContext(Dispatchers.Main) {
//                    val set = ConstraintSet()
//                    val constraintLayout = binding.clProfile
//                    set.clone(constraintLayout)
                    var iv = ImageView(context?.applicationContext)
                    binding.clProfile.addView(iv)
                    iv.id = idCount
                    idCount++
                    var current = binding.tvA4.id
                    var params = iv.layoutParams as ConstraintLayout.LayoutParams
                    params.startToStart = current
                    params.endToEnd = current
                    params.topToBottom = current
                    iv.adjustViewBounds = true
                    iv.maxHeight = 700
                    iv.maxWidth = 700
                    params.width = WRAP_CONTENT
                    params.height = WRAP_CONTENT

                    iv.requestLayout()

                    Picasso.get()
                        .load(imageURI)
                        .into(iv)


//
//                    set.connect(iv.id, set.TOP, current, set.BOTTOM, 0)
//                    set.connect(iv.id, ConstraintSet.LEFT, binding.clProfile.id, ConstraintSet.LEFT, 0)
//                    set.connect(iv.id, ConstraintSet.RIGHT, binding.clProfile.id, ConstraintSet.RIGHT, 0)
//                    set.applyTo(constraintLayout)
                    current = iv.id
                }
            }
        } catch (e: Exception){
            val context = context?.applicationContext
            withContext(Dispatchers.Main){
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
//
//                sb.append("$caption\n") //append person followed by new line


//                    binding.clProfile.addView(iv)
//                    iv.

//                    iv.layoutParams.height = 500
//                    iv.layoutParams.width = 500


//                    iv.


//                    changes the image size
//                    iv.x = 600F
//                    iv.y = 600F

//                    Picasso.get()
//                        .load("https://firebasestorage.googleapis.com/v0/b/in2thewok.appspot.com/o/images%2FV5Jhkt4jPANYZWFpnQGA6QmCcV23%2Fimage61?alt=media&token=b32a1cc9-a5ec-4bef-8a7c-718ab07aa66f")
//                        .into(iv)
//            }


                //                    val encodedFileName = URLDecoder.decode("image%3A61.jpeg", "UTF-8")
//                    val storageRef = FirebaseStorage.getInstance().reference.child("images/V5Jhkt4jPANYZWFpnQGA6QmCcV23/image61")
//                    iv.setBackgroundColor(Color.CYAN)
//                    iv.
//                    val localUri
//                    val testImage = binding.testImage
//                    iv.setImageBitmap(imageURI)
//                    val localFile = File.createTempFile("tempImage", "jpg")
//                    storageRef.getFile(localFile).addOnSuccessListener{
//                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
//                        iv.setImageBitmap(bitmap)
//                    }.addOnFailureListener{
//                        Toast.makeText(context?.applicationContext, it.message, Toast.LENGTH_LONG).show()
//                    }
//                    binding.testImage.setImageURI(imageURI)
                //

//                tvPosts.text = sb.toString()
//                }
//            }
//
//             set string $ text to textview,
//             so switch the co-routine context as UI can only be modified inside Main dispatchers

//        }
//    }

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