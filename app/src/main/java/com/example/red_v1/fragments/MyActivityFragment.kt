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
import com.example.red_v1.databinding.FragmentMyActivityBinding
import com.example.red_v1.listeners.RedListenerImpl
import com.example.red_v1.util.DATA_REDS
import com.example.red_v1.util.DATA_RED_USER_IDS
import com.example.red_v1.util.Red


class MyActivityFragment : RedFragment() {
    private var _binding: FragmentMyActivityBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener = RedListenerImpl(binding.redList,currentUser,callback)

        redsAdapter = RedListAdapter(userId!!, arrayListOf())
        redsAdapter?.setListener(listener)
        binding.redList?.apply {
            layoutManager= LinearLayoutManager(context)
            adapter = redsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            updateList()
        }
    }

    override fun updateList() {
        binding.redList.visibility = View.GONE
        val reds = arrayListOf<Red>()

        firebaseDB.collection(DATA_REDS).whereArrayContains(DATA_RED_USER_IDS,userId!!).get()
            .addOnSuccessListener { list ->
                for (document in list.documents){
                    val red = document.toObject(Red::class.java)
                    red?.let{
                        reds.add(red)
                    }
                }
                val sortedList = reds.sortedWith(compareByDescending { it.timestamp })
                redsAdapter?.updateReds(sortedList)
                binding.redList?.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                binding.redList?.visibility = View.VISIBLE
            }
    }


}