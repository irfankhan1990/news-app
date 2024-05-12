package com.example.newsapp.ui.favourite

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.databinding.FragmentFavouritesBinding
import com.example.newsapp.resources.News
import com.example.newsfresh.NewsItemClicked
import com.example.newsfresh.NewsListAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class FavouritesFragment : Fragment(), NewsItemClicked {
    private lateinit var mAdapter: NewsListAdapter
    private var _binding: FragmentFavouritesBinding? = null
    var favList = mutableListOf<News>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(FavouritesViewModel::class.java)

        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textGallery
//        galleryViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        binding.searchFavImage.setOnClickListener(View.OnClickListener {
            fetchData(binding.searchFavEt.text.toString())
        })
        binding.recyclerViewHome.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = NewsListAdapter(this)
        fetchData("")
        binding.recyclerViewHome.adapter = mAdapter
        return root
    }

    private fun fetchData(searchData: String) {
        Firebase.auth.currentUser?.let {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(Firebase.auth.currentUser!!.uid)
                .collection("urls")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (!task.result.isEmpty) {
                            favList.clear()
                            for (doc in task.result.documents) {
                                val d = doc.data?.values
                                if (searchData != "") {
                                    if (doc.data?.get("title").toString().contains(searchData)) {
                                        Log.d(ContentValues.TAG,"Clicked: " + binding.searchFavEt.text.toString())
                                        val res = hashMapToClass(d)
                                        favList.add(res)
                                    }else{
                                        Log.d(ContentValues.TAG, "Clicked ddddd: "+doc.data?.get("title").toString())
                                    }
                                } else {
                                    val res = hashMapToClass(d)
                                    favList.add(res)
                                }

                            }
                        }
                    } else {
//                        Toast.makeText(this,"not successful",Toast.LENGTH_SHORT).show()
                        Log.e(ContentValues.TAG, "onComplete: ", task.exception)
                    }
                    Log.d(ContentValues.TAG, "Fav List: $favList")
                    mAdapter.updateNews(favList)
                }
        }
    }

    private fun hashMapToClass(data: MutableCollection<Any>?): News {
        var title: String = ""
        var author: String = ""
        var url: String = ""
        var imageUrl: String = ""
        if (data != null) {
            var count = 0;
            for (doc in data) {
                if (count == 0) {
                    author = doc as String
                    count++
                } else if (count == 1) {
                    imageUrl = doc as String
                    count++
                } else if (count == 2) {
                    title = doc as String
                    count++
                } else if (count == 3) {
                    url = doc as String
                    count++
                }
            }
        }

        return News(title, author, url, imageUrl)
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