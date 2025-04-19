package com.example.red_v1.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.red_v1.R
import com.example.red_v1.adapters.RedListAdapter
import com.example.red_v1.databinding.ActivityHomeBinding
import com.example.red_v1.databinding.FragmentSearchBinding
import com.example.red_v1.listeners.RedListener
import com.example.red_v1.util.DATA_REDS
import com.example.red_v1.util.DATA_RED_HASHTAGS
import com.example.red_v1.util.Red
import com.example.red_v1.util.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SearchFragment : RedFragment() {

    private var currentHashtag = ""
    private var redsAdapter: RedListAdapter? = null
    private var currentUser: User? = null
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val listener: RedListener? = null


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

        redsAdapter = RedListAdapter(userId!!, arrayListOf())
        redsAdapter?.setListener(listener)
        binding.redList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = redsAdapter
            addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        }

        binding.swipeRefresh.isRefreshing = false
        updateList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun newHashtag(term:String){
        currentHashtag = term
        binding.followHashtag.visibility = View.VISIBLE
        updateList()
    }

    fun updateList(){
        binding.redList?.visibility = View.GONE
        firebaseDB.collection(DATA_REDS).whereArrayContains(DATA_RED_HASHTAGS,currentHashtag).get()
            .addOnSuccessListener { list ->
                binding.redList?.visibility = View.VISIBLE
                val reds = arrayListOf<Red>()
                for(document in list.documents){
                    val red = document.toObject(Red::class.java)
                    red?.let{reds.add(it)}
                }
                val sortedReds: List<Red> = reds.sortedWith(compareByDescending { it.timestamp })
                redsAdapter?.updateReds(sortedReds)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}