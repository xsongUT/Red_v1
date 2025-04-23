package com.example.red_v1.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.red_v1.R
import com.example.red_v1.adapters.RedListAdapter
import com.example.red_v1.databinding.FragmentHomeBinding
import com.example.red_v1.databinding.FragmentSearchBinding
import com.example.red_v1.listeners.RedListenerImpl
import com.example.red_v1.util.DATA_REDS
import com.example.red_v1.util.DATA_RED_HASHTAGS
import com.example.red_v1.util.DATA_RED_USER_IDS
import com.example.red_v1.util.Red


class HomeFragment : RedFragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listener for item interactions (like, repost, follow)
        listener = RedListenerImpl(binding.redList,currentUser,callback)

        // Adapter setup with current user's ID
        redsAdapter = RedListAdapter(userId!!, arrayListOf())
        redsAdapter?.setListener(listener)
        // Configure RecyclerView
        binding.redList?.apply {
            layoutManager=LinearLayoutManager(context)
            adapter = redsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        // Pull-to-refresh action
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            updateList()
        }



    }

    // Fetches and updates the list of reds based on followed users and hashtags
    override fun updateList() {
        binding.redList?.visibility =View.GONE
        currentUser?.let{
            val reds = arrayListOf<Red>()
            // Get posts by hashtags the user follows
            for(hashtag in it.followHashtags!!){
                firebaseDB.collection(DATA_REDS).whereArrayContains(DATA_RED_HASHTAGS,hashtag).get()
                    .addOnSuccessListener { list ->
                        for (document in list.documents){
                            val red = document.toObject(Red::class.java)
                            red?.let{
                                reds.add(it)
                            }
                            updateAdapter(reds)
                            binding.redList?.visibility = View.VISIBLE
                        }

                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                        binding.redList?.visibility = View.VISIBLE
                    }
            }
            // Get posts posted or reposted by followed users
            for (followUser in it.followUsers!!){
                firebaseDB.collection(DATA_REDS).whereArrayContains(DATA_RED_USER_IDS,followUser).get()
                    .addOnSuccessListener { list ->
                        for (document in list.documents){
                            val red = document.toObject(Red::class.java)
                            red?.let{
                                reds.add(it)
                            }
                            updateAdapter(reds)
                            binding.redList?.visibility = View.VISIBLE
                        }

                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                        binding.redList?.visibility = View.VISIBLE
                    }
            }
        }

    }

    // Updates the adapter with sorted and deduplicated posts
    private fun updateAdapter(reds: List<Red>){
        val sortedReds = reds.sortedWith(compareByDescending { it.timestamp })
        redsAdapter?.updateReds((removeDuplicates(sortedReds)))
    }
    // Removes duplicate posts based on their unique redId
    private fun removeDuplicates(originalList:List<Red>) =originalList.distinctBy { it.redId }

}