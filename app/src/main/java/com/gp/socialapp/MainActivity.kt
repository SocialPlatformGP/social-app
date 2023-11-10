package com.gp.socialapp

import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var toolbar: Toolbar
    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var appBarLayout: AppBarLayout
    private val currentUser = FirebaseAuth.getInstance()


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

        val navHostFragment= supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
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
            else->{
                showBottomNav()
                appBarLayout.visibility=View.VISIBLE
            }
        }
    }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu,menu)
        return super.onCreateOptionsMenu(menu)
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