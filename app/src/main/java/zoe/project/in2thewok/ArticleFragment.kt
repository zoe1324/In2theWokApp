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

/**
 * [ArticleFragment], A [Fragment] class displayed by [HomeActivity],
 * linked via [FragmentArticleBinding].
 *
 * @constructor Creates a new Fragment
 * @suppress TooGenericExceptionCaught
 */
@Suppress("EmptyFunctionBlock")

class ArticleFragment : Fragment() {
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private var articles = arrayListOf<String>()

    /**
     * Creates View for [ArticleFragment]
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
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        binding.rvArticles.hasFixedSize()
        binding.rvArticles.layoutManager = LinearLayoutManager(context)
        binding.rvArticles.itemAnimator = DefaultItemAnimator()
        articles = (activity as? HomeActivity)!!.articles
        binding.rvArticles.adapter = RecyclerAdapter(articles, R.layout.layout_preview)
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
     * [RecyclerAdapter] customised class, adapts arrayList of links to fit RichLinkPreview views, created from
     * Library: io.github.ponnamkarthik:richlinkpreview:1.0.9@aar
     *
     * @param postArray The arrayList of Posts being adapted
     * @param layout The layout in which each post should be adapted to fit
     * @constructor Creates a new Adapter for the RecyclerView
     */
    inner class RecyclerAdapter(
        val linkArray: ArrayList<String>,
        val layout: Int
    ) : RecyclerView.Adapter<ArticleFragment.ViewHolder>() {

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
            holder.updateItems(linkArray[position])
        }

        /**
         * Returns the size of the arrayList being adapted
         *
         * @return linkArray.size
         */
        override fun getItemCount(): Int {
            return linkArray.size
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
        var urlPreview: RichLinkView = itemView.findViewById(R.id.richLinkView)

        /**
         * Updates each RecyclerView item to display the RichLinkPreview,
         * uses external library function setLink() to adapt RichLinkView to URL
         *
         * @param title The current link in the list being passed in
        */
        fun updateItems(title: String) {
            urlPreview.setLink(title, object : ViewListener {
                override fun onSuccess(status: Boolean) {}
                override fun onError(e: Exception?) {}
            })

        }

    }
}
