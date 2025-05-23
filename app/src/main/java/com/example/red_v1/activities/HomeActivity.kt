package com.example.red_v1.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.red_v1.R
import com.example.red_v1.databinding.ActivityHomeBinding
import com.example.red_v1.fragments.HomeFragment
import com.example.red_v1.fragments.MapFragment
import com.example.red_v1.fragments.MyActivityFragment
import com.example.red_v1.fragments.PlayerFragment
import com.example.red_v1.fragments.RedFragment
import com.example.red_v1.fragments.SearchFragment
import com.example.red_v1.listeners.HomeCallback
import com.example.red_v1.util.DATA_USERS
import com.example.red_v1.util.User
import com.example.red_v1.util.loadUrl
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.tabs.TabLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore


class HomeActivity : AppCompatActivity(), HomeCallback {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var tabLayout: TabLayout
    private var sectionPagerAdapter : SectionPageAdapter? = null
    private val firebaseAuth = FirebaseAuth.getInstance()
    private  val firebaseDB = FirebaseFirestore.getInstance()
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val myActivityFragment = MyActivityFragment()
    private var userId = FirebaseAuth.getInstance().currentUser?.uid
    private var user: User? = null
    private var currentFragment: RedFragment = homeFragment




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        sectionPagerAdapter = SectionPageAdapter(supportFragmentManager)
        val container = binding.container
        //val tabs = binding.tabs
        // Initialize TabLayout
        tabLayout = binding.tabs
        container.adapter = sectionPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        // define three tabs/pages to represent three different pages/windows
        // Add tabs
        val tabHome = tabLayout.newTab().setIcon(R.drawable.selector_home)
        tabHome.contentDescription = "Home"
        tabLayout.addTab(tabHome)

        val tabSearch = tabLayout.newTab().setIcon(R.drawable.selector_search)
        tabSearch.contentDescription = "Search"
        tabLayout.addTab(tabSearch)

        val tabActivity = tabLayout.newTab().setIcon(R.drawable.selector_myactivity)
        tabActivity.contentDescription = "My Activity"
        tabLayout.addTab(tabActivity)

        //link the postion of tags to windows
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))


        binding.fabMusic.setOnClickListener {
            // Replace the current fragment with PlayerFragment
            binding.fabMusic.visibility = View.GONE
            val playerFragment = PlayerFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container2, playerFragment)  // Replace the container with PlayerFragment
                .addToBackStack(null)  // Optionally add the transaction to the back stack
                .commit()
        }

        tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> {
                        binding.titleBar.visibility = View.VISIBLE
                        binding.titleBar.text = "Home"
                        binding.searchBar.visibility = View.GONE
                        currentFragment = homeFragment
                    }
                    1 -> {
                        binding.titleBar.visibility = View.GONE
                        binding.searchBar.visibility = View.VISIBLE
                        currentFragment = searchFragment
                    }
                    2 -> {
                        binding.titleBar.visibility = View.VISIBLE
                        binding.titleBar.text = "My Activity"
                        binding.searchBar.visibility = View.GONE
                        currentFragment = myActivityFragment
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        binding.logo.setOnClickListener{view ->
            startActivity((ProfileActivity.newIntent(this)))
        }
        binding.fab.setOnClickListener {
            startActivity(RedActivity.newIntent(this,userId,user?.username))
        }
        binding.homeProgressLayout.setOnTouchListener { v, event -> true }

        binding.search.setOnEditorActionListener{v,actionId,event ->
            if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH){
                searchFragment.newHashtag(v?.text.toString())
            }
                true

        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        binding.fabMusic.visibility = View.VISIBLE
    }


    //keep monitoring the current user, if it is signed out return to the login page
    override fun onResume() {
        super.onResume()
        userId = FirebaseAuth.getInstance().currentUser?.uid
        if(userId ==null){
            startActivity(LoginActivity.newIntent(this))
            finish()
        } else {
            populate()
        }
    }

    override fun onUserUpdated() {
        populate()
    }

    override fun onRefresh() {
        currentFragment.updateList()
    }

    fun populate(){
        binding.homeProgressLayout.visibility = View.VISIBLE
        //load the info from database
        firebaseDB.collection(DATA_USERS).document(userId!!).get()
            .addOnSuccessListener { documentSnapshot ->
                binding.homeProgressLayout.visibility = View.GONE
                user = documentSnapshot.toObject(User::class.java)
                user?.imageUrl?.let {
                    binding.logo.loadUrl(it, R.drawable.logo)
                }
                updateFragmentUser()
            }.addOnFailureListener { e ->
                e.printStackTrace()
                finish()
            }
    }

    fun updateFragmentUser(){
        homeFragment.setUser(user)
        searchFragment.setUser(user)
        myActivityFragment.setUser(user)
        currentFragment.updateList()
    }

    inner class SectionPageAdapter(fm:FragmentManager) : FragmentPagerAdapter(fm){


        override fun getItem(position: Int): Fragment {
            return when(position){
                0 -> homeFragment
                1 -> searchFragment
                else -> myActivityFragment
            }
        }
        override fun getCount() = 3
    }


    companion object{
        fun newIntent(context: Context): Intent = Intent(context, HomeActivity::class.java)
    }



}
