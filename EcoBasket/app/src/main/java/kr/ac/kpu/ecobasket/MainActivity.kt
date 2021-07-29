package kr.ac.kpu.ecobasket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_layout.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nav_menu.setNavigationItemSelectedListener(this)

        btn_menu.setOnClickListener {
            drawerLayout.openDrawer(Gravity.RIGHT)
        }

        nav_menu.getHeaderView(0).setOnClickListener {
            startActivity<MyInfoActivity>()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_guide ->
                startActivity<GuideActivity>()
            R.id.menu_pref ->
                startActivity<PreferencesActivity>()
        }
        return false
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }
}