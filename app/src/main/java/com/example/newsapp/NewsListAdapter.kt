package com.example.newsfresh

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.resources.News
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class NewsListAdapter(private val listener: NewsItemClicked) :
    RecyclerView.Adapter<NewsViewHolder>() {

    private val items: ArrayList<News> = ArrayList()
    private var favList = mutableListOf<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        val viewHolder = NewsViewHolder(view)
        view.setOnClickListener {
            listener.onItemClicked(items[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = items[position]
        holder.titleView.text = currentItem.title
        holder.author.text = currentItem.author
        Glide.with(holder.itemView.context).load(currentItem.imageUrl).into(holder.image)
        val auth = Firebase.auth
        auth.currentUser?.let {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(Firebase.auth.currentUser!!.uid)
                .collection("urls")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if ( !task.result.isEmpty) {
                            for (docName in task.result.documents){
                                val dd=docName.id
                                Log.d(ContentValues.TAG, "doc Name: $dd")
                                if(docName.id==currentItem.url.replace("/","")){
                                    favList.add(currentItem.url.replace("/",""))
                                    holder.favImage.setBackgroundResource(R.drawable.baseline_favorite)
                                }
                            }
//                            favList = task.result.get("urls") as MutableList<String>
////                        Toast.makeText(this,"onComplete: $urls",Toast.LENGTH_SHORT).show()
//                            Log.d(ContentValues.TAG, "onComplete: $$favList")
//                            if (favList.isNotEmpty()) {
//                                if (favList.contains(currentItem.url))
//                                    holder.favImage.setBackgroundResource(R.drawable.baseline_favorite)
//                            }
                        }
                    } else {
//                        Toast.makeText(this,"not successful",Toast.LENGTH_SHORT).show()
                        Log.e(ContentValues.TAG, "onComplete: ", task.exception)
                    }
                }
        }
        holder.favImage.setOnClickListener(View.OnClickListener {
            Log.d(ContentValues.TAG, "onClick: $$favList")
            if (favList.contains(currentItem.url.replace("/",""))) {
                val db = FirebaseFirestore.getInstance()
                val users = db.collection("users")
                users.document(Firebase.auth.currentUser!!.uid).collection("urls").document(currentItem.url.replace("/","")).delete()
//                    .update("urls", FieldValue.arrayRemove(currentItem.url))
                favList.remove(currentItem.url.replace("/",""))
                holder.favImage.setBackgroundResource(R.drawable.baseline_favorite_border)
//                data1.uid?.let { it1 -> cities.document(it1).set(data1) }
//                db.collection("users").document(Firebase.auth.currentUser!!.uid)
//                    .update("language","English")
            } else {
                val db = FirebaseFirestore.getInstance()
                val users = db.collection("users")
                users.document(Firebase.auth.currentUser!!.uid).collection("urls").document(currentItem.url.replace("/","")).set(currentItem)
//                    .update("urls", FieldValue.arrayUnion(currentItem.url))
                holder.favImage.setBackgroundResource(R.drawable.baseline_favorite)
                favList.add(currentItem.url.replace("/",""))
            }
        })
    }

    fun updateNews(updatedNews: MutableList<News>) {
        items.clear()
        items.addAll(updatedNews)

        notifyDataSetChanged()
    }
}

class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleView: TextView = itemView.findViewById(R.id.title)
    val image: ImageView = itemView.findViewById(R.id.image)
    val author: TextView = itemView.findViewById(R.id.author)
    val favImage: ImageView = itemView.findViewById(R.id.fav_image)
}

interface NewsItemClicked {
    fun onItemClicked(item: News)
}