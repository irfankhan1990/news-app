package com.example.newsapp.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.newsapp.databinding.FragmentHomeBinding
import com.example.newsapp.resources.MySingleton
import com.example.newsapp.resources.News
import com.example.newsfresh.NewsItemClicked
import com.example.newsfresh.NewsListAdapter

class HomeFragment : Fragment(), NewsItemClicked {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mAdapter: NewsListAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.searchHomeImage.setOnClickListener(View.OnClickListener {
            fetchData(binding.searchHomeEt.text.toString())
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchData("")
        mAdapter = NewsListAdapter(this)
        binding.recyclerView.adapter = mAdapter
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    private fun fetchData(searchData: String) {
        val url =
            "https://newsdata.io/api/1/news?apikey=pub_486639596a79105a591146ffde10f6153457&country=pk"
//        val url = "http://api.mediastack.com/v1/news?access_key=e7e3fc4d7512b5eacff927f5115abd21"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener {
                val newsJsonArray = it.getJSONArray("results")
//                val newsJsonArray = it.getJSONArray("data")
                val newsArray = ArrayList<News>()
                for (i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("creator"),
                        newsJsonObject.getString("link"),
                        newsJsonObject.getString("image_url")
                    )
                    if (searchData != "") {
                        if (news.title.contains(searchData)) {
                            newsArray.add(news)
                        }
                    } else {
                        newsArray.add(news)
                    }
                }

                mAdapter.updateNews(newsArray)
            },
            Response.ErrorListener {

            }
        )
        MySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(item: News) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(item.url))
    }
}