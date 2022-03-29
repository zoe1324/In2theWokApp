package zoe.project.in2thewok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.ponnamkarthik.richlinkpreview.RichLinkView
import io.github.ponnamkarthik.richlinkpreview.ViewListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import zoe.project.in2thewok.databinding.FragmentInfoBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoFragment : Fragment() {
    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!
    private var healthArticles = arrayListOf<String>()


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
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        binding.rvInfo.hasFixedSize()
        binding.rvInfo.layoutManager = LinearLayoutManager(context)
        binding.rvInfo.itemAnimator = DefaultItemAnimator()
        healthArticles = (activity as? HomeActivity)!!.healthArticles
        binding.rvInfo.adapter = RecyclerAdapter(healthArticles, R.layout.layout_preview)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //    inner class RecyclerAdapter(private val titles: ArrayList<String>, val details: ArrayList<String>, val images: ArrayList<Int>, val layout: Int): RecyclerView.Adapter<ArticleFragment.ViewHolder>(){
    inner class RecyclerAdapter(val linkArray: ArrayList<String>, val layout: Int): RecyclerView.Adapter<InfoFragment.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.updateItems(linkArray[position])
        }

        override fun getItemCount(): Int {
            return linkArray.size
        }

//    TODO: Look at explanation of this fix

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

    }
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var urlPreview: RichLinkView = itemView.findViewById(R.id.richLinkView)

        fun updateItems(title: String){
            urlPreview.setLink(title, object : ViewListener{
                override fun onSuccess(status: Boolean) {

                }

                override fun onError(e: Exception?) {

                }
            })
        }
    }

//    companion object {
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
