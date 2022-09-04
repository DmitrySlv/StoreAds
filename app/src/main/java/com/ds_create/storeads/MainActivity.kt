package com.ds_create.storeads

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.ds_create.storeads.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.main_content.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    private fun init() = with(binding) {
        val toggle = ActionBarDrawerToggle(this@MainActivity,
           drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this@MainActivity)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       when (item.itemId) {
           R.id.my_ads -> {
               Toast.makeText(this, "Pressed id_my_ads", Toast.LENGTH_LONG).show()
           }
           R.id.id_car -> {
               Toast.makeText(this, "Pressed id_car", Toast.LENGTH_LONG).show()
           }
           R.id.id_pc -> {
               Toast.makeText(this, "Pressed id_pc", Toast.LENGTH_LONG).show()
           }
           R.id.sign_up -> {
               Toast.makeText(this, "Pressed sign_up", Toast.LENGTH_LONG).show()
           }
           R.id.id_sign_in -> {
               Toast.makeText(this, "Pressed id_sign_in", Toast.LENGTH_LONG).show()
           }
           R.id.id_sign_out -> {
               Toast.makeText(this, "Pressed id_sign_out", Toast.LENGTH_LONG).show()
           }
       }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}