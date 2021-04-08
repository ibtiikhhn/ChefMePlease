package com.muibsols.chefmeplease.UI.Activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.muibsols.chefmeplease.R
import com.muibsols.chefmeplease.UI.Fragments.*

class BaseActivity : AppCompatActivity() {

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    lateinit var drawerToggle: ImageView
    lateinit var chatLink:ImageView
    lateinit var navView: NavigationView
    lateinit var drawer: DrawerLayout
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        initViews()
        firebaseAuth = FirebaseAuth.getInstance()

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                R.id.activeOrders -> {
                    startActivity(Intent(this, ActiveOrdersActivity::class.java))
                }
                R.id.userFav -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                }
                R.id.logout -> {
                    Firebase.auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                R.id.rateApp -> {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + applicationContext.packageName)))
                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + applicationContext.packageName)))
                    }
                }
                R.id.shareApp -> {
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "Checkout Chef Me Please App at : http://play.google.com/store/apps/details?id=" + application.packageName)
                    sendIntent.type = "text/plain"
                    startActivity(sendIntent)
                }
            }
            true
        }

        chatLink.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        drawerToggle.setOnClickListener{
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            } else {
                drawer.openDrawer(GravityCompat.START)
            }
        }

    }

    fun initViews() {
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewpager)
        setupViewPager(viewPager)
        chatLink = findViewById(R.id.chatIMG)
        tabLayout.setupWithViewPager(viewPager)

        drawerToggle = findViewById(R.id.drawerIMG)
        navView = findViewById(R.id.nav_view)
        drawer = findViewById(R.id.drawer_layout)
    }

    class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val mFragmentList=  ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        fun addFragment(fragment: Fragment, title: String){
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList.get(position)
        }
    }

    fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(LunchSpecialFragment(), "Lunch Specials")
        adapter.addFragment(SteakFragment(), "Steak")
        adapter.addFragment(PoultryFragment(), "Poultry")
        adapter.addFragment(PorkFragment(), "Pork")
        adapter.addFragment(SeafoodFragment(), "SeaFood")
        viewPager.adapter = adapter
    }
}