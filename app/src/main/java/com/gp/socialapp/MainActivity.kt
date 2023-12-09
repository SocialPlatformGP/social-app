package com.gp.socialapp

import android.app.SearchManager
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.gp.auth.ui.AuthenticationActivity
import com.gp.socialapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    lateinit var drawerLayout: DrawerLayout
        lateinit var binding: ActivityMainBinding
    lateinit var navView: NavigationView
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var toolbar: Toolbar
    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var appBarLayout: AppBarLayout
    private val currentUser = FirebaseAuth.getInstance()
    lateinit var navHostFragment: NavHostFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)


        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.nav_view)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        toolbar = findViewById(R.id.toolbar)
        appBarLayout = findViewById(R.id.app_bar)

        setSupportActionBar(toolbar)

        navHostFragment= supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController= navHostFragment.findNavController()
        appBarConfiguration= AppBarConfiguration(setOf(
            R.id.post_nav_graph,
            R.id.chat_nav_graph,
        ),drawerLayout)

        setupActionBarWithNavController(navController,appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNavigationView.setupWithNavController(navController)


        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.logout->{
                    currentUser.signOut()
                    val intent = Intent(this, AuthenticationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    finish()
                    return@setNavigationItemSelectedListener true
                }
                R.id.Announcement->{

                    return@setNavigationItemSelectedListener true
                }
                R.id.Material->{
                    return@setNavigationItemSelectedListener true
                }
                else->{
                    navController.navigate(it.itemId)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
            }
        }
    navController.addOnDestinationChangedListener{_,destination,_->

        when (destination.id){
            com.gp.chat.R.id.privateChatFragment->{
                hideBottomNav()
                toolbar.menu.clear()

            }
            com.gp.chat.R.id.groupChatFragment->{
                hideBottomNav()
                toolbar.menu.clear()

            }
            com.gp.posts.R.id.suggest_post->{
                hideBottomNav()
            }
            com.gp.posts.R.id.searchFragment2->{
                hideBottomNav()
            }

            com.gp.posts.R.id.createPostFragment->{
                hideBottomNav()
                appBarLayout.visibility=View.GONE

            }
            com.gp.posts.R.id.postDetailsFragment->{
                hideBottomNav()
            }
            com.gp.chat.R.id.fullScreenImageDialogFragment->{
                hideBottomNav()
            }

            else->{
                showBottomNav()
                appBarLayout.visibility=View.VISIBLE
            }
        }
    }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu,menu)
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.search_Fragment)
        val searchView = searchItem?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        val currentFragment= navHostFragment.childFragmentManager.fragments[0]
        if(currentFragment is com.gp.posts.SearchFragment){
            currentFragment.navigateToFinalResult(query)

        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val currentFragment= navHostFragment.childFragmentManager.fragments[0]
        when(currentFragment){
            is com.gp.posts.SearchFragment->{
                currentFragment.updateSearchQuery(newText)
            }
            is com.gp.posts.presentation.postsSearch.SearchResultsFragment->{
                currentFragment.backToSuggest(newText)


            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.search_Fragment->{
                navController.navigate(com.gp.posts.R.id.suggest_post)
                return true
            }
            else->{
                return false
            }
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun hideBottomNav(){
        val params=bottomNavigationView.layoutParams as ViewGroup.LayoutParams
        val dpToPx = resources.displayMetrics.density // Conversion factor
        val heightInPixels = (0 * dpToPx).toInt()
        params.height=heightInPixels
        bottomNavigationView.layoutParams=params
        binding.appBarMain.contentInAppBarMain.height=heightInPixels
        bottomNavigationView.visibility= View.GONE
    }
    fun showBottomNav(){
        val params=bottomNavigationView.layoutParams as ViewGroup.LayoutParams
        val dpToPx = resources.displayMetrics.density // Conversion factor
        val heightInPixels = (80 * dpToPx).toInt()
        params.height=heightInPixels
        bottomNavigationView.layoutParams=params
        binding.appBarMain.contentInAppBarMain.height=heightInPixels
        bottomNavigationView.visibility= View.VISIBLE
    }



}