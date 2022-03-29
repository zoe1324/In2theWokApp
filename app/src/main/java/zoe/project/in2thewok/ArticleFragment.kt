package zoe.project.in2thewok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.ponnamkarthik.richlinkpreview.RichLinkView
import io.github.ponnamkarthik.richlinkpreview.ViewListener
import zoe.project.in2thewok.databinding.FragmentArticleBinding

@Suppress("EmptyFunctionBlock")

/**
 * A simple [Fragment] subclass.
 * Use the [ArticleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArticleFragment : Fragment() {
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private var articles = arrayListOf<String>()


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
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        binding.rvArticles.hasFixedSize()
        binding.rvArticles.layoutManager = LinearLayoutManager(context)
        binding.rvArticles.itemAnimator = DefaultItemAnimator()
        articles = (activity as? HomeActivity)!!.articles
        binding.rvArticles.adapter = RecyclerAdapter(articles, R.layout.layout_preview)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class RecyclerAdapter(val linkArray: ArrayList<String>,
                                val layout: Int): RecyclerView.Adapter<ArticleFragment.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.itemTitle.text = titles[position]
//            holder.itemDescription.text = details[position]
//            holder.itemImage.setImageResource(images[position])
            holder.updateItems(linkArray[position])
        }

        override fun getItemCount(): Int {
            return linkArray.size
        }

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
                override fun onSuccess(status: Boolean) {}
                override fun onError(e: Exception?) {}
            })

        }

    }
}
