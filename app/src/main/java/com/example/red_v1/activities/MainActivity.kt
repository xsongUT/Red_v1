package com.example.red_v1.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.red_v1.databinding.ActivityMainBinding
import com.example.red_v1.R

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    companion object {
        fun setBackgroundDrawable(button: ImageButton, resourceId: Int) {
            button.setBackgroundResource(resourceId)
            button.tag = resourceId
        }
        fun setBackgroundColor(view: View, color: Int) {
            view.setBackgroundColor(color)
            view.tag = color
        }
    }
    private fun initMenu() {
        addMenuProvider(object : MenuProvider {
            // XXX Write me, menu provider overrides
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.player_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> {
                        navController.navigate(R.id.action_playerFragment_to_settingsFragment)
                        true
                    }
                    else -> false
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        initMenu()
        // Set up our nav graph
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    // navigateUp:
    // If we came here from within the app, pop the back stack
    // If we came here from another app, return to it.
    override fun onSupportNavigateUp(): Boolean {
        // XXX Write me
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
