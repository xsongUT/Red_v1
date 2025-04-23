package com.example.red_v1.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.red_v1.R
import com.example.red_v1.adapters.RedListAdapter
import com.example.red_v1.databinding.ActivityHomeBinding
import com.example.red_v1.databinding.FragmentSearchBinding
import com.example.red_v1.listeners.RedListener
import com.example.red_v1.listeners.RedListenerImpl
import com.example.red_v1.util.DATA_REDS
import com.example.red_v1.util.DATA_RED_HASHTAGS
import com.example.red_v1.util.DATA_USERS
import com.example.red_v1.util.DATA_USER_HASTAGS
import com.example.red_v1.util.Red
import com.example.red_v1.util.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SearchFragment : RedFragment() {

    private var currentHashtag = ""

    private var hashtagFollowed =false


    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_search, container, false)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener = RedListenerImpl(binding.redList,currentUser,callback)
        redsAdapter = RedListAdapter(userId!!, arrayListOf())
        redsAdapter?.setListener(listener)

        // Setup the RecyclerView
        binding.redList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = redsAdapter
            addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        }
        // Handle swipe-to-refresh
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            updateList()
        }
        // Handle follow/unfollow hashtag click
        binding.followHashtag.setOnClickListener{
            binding.followHashtag.isClickable = false
            val followed = currentUser?.followHashtags
            if (hashtagFollowed){
                followed?.remove(currentHashtag)
            }else {
                followed?.add(currentHashtag)
            }

            // Update the user's followed hashtags in Firestore
            firebaseDB.collection(DATA_USERS).document(userId).update(DATA_USER_HASTAGS,followed)
                    .addOnSuccessListener {
                        callback?.onUserUpdated()
                        binding.followHashtag.isClickable = true
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                        binding.followHashtag.isClickable = true
                    }

        }
    }

    // Clear binding when the view is destroyed to avoid memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    // Called from outside to start a new hashtag search
    fun newHashtag(term:String){
        currentHashtag = term
        binding.followHashtag.visibility = View.VISIBLE
        updateList()
    }

    // Fetch and display posts that contain the current hashtag
    override  fun updateList(){
        binding.redList?.visibility = View.GONE
        firebaseDB.collection(DATA_REDS).whereArrayContains(DATA_RED_HASHTAGS,currentHashtag).get()
            .addOnSuccessListener { list ->
                binding.redList?.visibility = View.VISIBLE
                val reds = arrayListOf<Red>()
                for(document in list.documents){
                    val red = document.toObject(Red::class.java)
                    red?.let{reds.add(it)}
                }
                // Sort posts by timestamp (latest first) and update the adapter
                val sortedReds: List<Red> = reds.sortedWith(compareByDescending { it.timestamp })
                redsAdapter?.updateReds(sortedReds)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
        // Update the follow icon depending on current follow status
        updateFollowDrawable()
    }

    // Update the follow/unfollow icon based on whether the current hashtag is followed
    private fun updateFollowDrawable(){
        hashtagFollowed = currentUser?.followHashtags?.contains(currentHashtag) == true
        context?.let{
            if(hashtagFollowed){
                binding.followHashtag.setImageDrawable(ContextCompat.getDrawable(it,R.drawable.follow))
            }else{
                binding.followHashtag.setImageDrawable(ContextCompat.getDrawable(it,R.drawable.follow_inactive))
            }
        }

    }
}