package com.ecommerce.app.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ecommerce.app.R
import com.ecommerce.app.databinding.ActivityHomeBinding
import com.ecommerce.app.utils.DebugHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView

    var textCartItemCount: TextView? = null
    var mCartItemCount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        navController = findNavController(R.id.nav_host_fragment_content_main)
        drawerLayout = binding.drawerLayout
        navView = binding.navView

        appBarConfiguration = AppBarConfiguration(
            setOf(  R.id.homeFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.appBarMain.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == navController.graph.startDestinationId) {
                toggle.isDrawerIndicatorEnabled = true
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                toggle.isDrawerIndicatorEnabled = false
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }

        setupMenuOption()
        bottomNavigationListerner()
    }

    public fun setupToolbar(title: String) {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun bottomNavigationListerner() {
        binding.appBarMain.bottomNavigation.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        navController.navigate(R.id.homeFragment)
                        return@OnNavigationItemSelectedListener true
                    }

                    R.id.navigation_trending -> {
                        launchWishlistScreen()
                        return@OnNavigationItemSelectedListener true
                    }

                    R.id.navigation_categories -> {
                        navController.navigate(R.id.categoryFragment)
                        return@OnNavigationItemSelectedListener true
                    }

                    R.id.navigation_account -> {
                        showBottomNavigationBar(false)
                        navController.navigate(R.id.addressFragment)
                        return@OnNavigationItemSelectedListener true
                    }
                    else-> false
                }
            })
    }


    fun showBottomNavigationBar(isVisible:Boolean) {
        binding.appBarMain.bottomNavigation.visibility = if(isVisible) View.VISIBLE else View.GONE
    }



    private fun setupMenuOption() {

        addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main, menu)

             /*   val cartItem = menu.findItem(R.id.action_cart)
                // Get the action view of the cart item to set up the badge
                val actionView: View = cartItem.actionView!!
                // Assuming that the cart_badge is a TextView within the action view
                textCartItemCount = actionView.findViewById(R.id.cart_badge)

                actionView.setOnClickListener {
                    DebugHandler.log("Hello Activity Cart 11")
                    onOptionsItemSelected(cartItem)
                }

                setupBadge(mCartItemCount)*/
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {

           /*         android.R.id.home -> {
                        // Handle back arrow button press here
                        DebugHandler.log("Hello Back Button")
                        removeFragmentFromStack()
                        return true
                    }*/

                    R.id.action_search -> {
                        DebugHandler.log("Hello Activity Search")
                        true
                    }
                    R.id.action_wishlist -> {
                        DebugHandler.log("Hello Activity Wishlist")
                        launchWishlistScreen()
                        true
                    }
                    R.id.action_cart -> {
                        DebugHandler.log("Hello Activity Cart")
                        launchCartScreen()
                        true
                    }
                    else -> false
                }
            }

        })
    }


     fun setupBadge(mCartItemCount:Int) {
        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount!!.visibility != View.GONE) {
                    textCartItemCount!!.visibility = View.GONE
                }
            } else {
                textCartItemCount!!.text = Math.min(mCartItemCount, 99).toString()
                if (textCartItemCount!!.visibility != View.VISIBLE) {
                    textCartItemCount!!.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun launchWishlistScreen(){
        navController.navigate(R.id.wishlistFragment)
    }
    private fun launchCartScreen(){
        navController.navigate(R.id.cartFragment)
    }



    override fun onResume() {
        super.onResume()
        showBottomNavigationBar(false)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            DebugHandler.log("Hello Item =="+item.itemId.toString())
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                // Handle fragment back navigation
                if (navController.currentDestination?.id != navController.graph.startDestinationId) {
                    DebugHandler.log("Hello currentDestination Open  $navController.currentDestination?.id")
                    navController.popBackStack() // Navigate back in the fragment stack
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}