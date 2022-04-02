package com.example.parstagram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.parstagram.MainActivity
import com.example.parstagram.Post
import com.example.parstagram.PostAdapter
import com.example.parstagram.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery


/**
 * A simple [Fragment] subclass.
 * Use the [FeedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
open class FeedFragment : Fragment() {
    lateinit var rvPosts : RecyclerView
    lateinit var adapter : PostAdapter
    var allPosts: MutableList<Post> = mutableListOf()
    lateinit var swipeContainer: SwipeRefreshLayout



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPosts = view.findViewById<RecyclerView>(R.id.postRecyclerView)

        adapter = PostAdapter(requireContext(), allPosts)
        rvPosts.adapter = adapter

        swipeContainer = view.findViewById(R.id.swipeContainer)

        rvPosts.layoutManager = LinearLayoutManager(requireContext())
        queryPosts()

        swipeContainer.setOnRefreshListener {
            fetchTimelineAsync()
        }
    }

    fun fetchTimelineAsync() {
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        query.include(Post.KEY_USER)
        query.addDescendingOrder("createdAt")
        query.findInBackground(object: FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if(e != null){
                    Log.e(TAG, "Error fetching posts")
                }else{
                    if(posts != null){
                        for(post in posts){
                            Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser()?.username)
                        }

                        adapter.clear()
                        if(posts.size >= 20){
                            allPosts.addAll(posts.subList(0, 19))
                        }
                        else{
                            allPosts.addAll(posts)
                        }

                        adapter.notifyDataSetChanged()

                        swipeContainer.setRefreshing(false)
                    }
                }
            }

        })
    }

    open fun queryPosts(){
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        query.include(Post.KEY_USER)
        query.addDescendingOrder("createdAt")
        query.findInBackground(object: FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if(e != null){
                    Log.e(TAG, "Error fetching posts")
                }else{
                    if(posts != null){
                        for(post in posts){
                            Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser()?.username)
                        }

                        if(posts.size >= 20){
                            allPosts.addAll(posts.subList(0, 19))
                        }
                        else{
                            allPosts.addAll(posts)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }

        })
    }



    companion object{
        const val TAG = "FeedFragment"
    }
}