package com.nafanya.words.core.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.nafanya.words.R
import com.nafanya.words.core.di.WordApplication
import com.nafanya.words.databinding.ActivityMainBinding
import com.nafanya.words.feature.about.AboutActivity
import com.nafanya.words.feature.tts.TtsProvider
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var ttsProvider: TtsProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as WordApplication).applicationComponent.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_manage_words, R.id.nav_learn, R.id.nav_test, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        ttsProvider.initialize()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu.forEach {
            val text = it.title!!
            val span = SpannableString(text)
            val typedValue = TypedValue()
            theme.resolveAttribute(
                androidx.appcompat.R.attr.colorControlNormal,
                typedValue,
                true
            )
            val array = obtainStyledAttributes(
                typedValue.resourceId,
                intArrayOf(androidx.appcompat.R.attr.colorControlNormal)
            )
            span.setSpan(
                ForegroundColorSpan(
                    array.getColor(0, Color.GRAY)
                ),
                0,
                span.length,
                0
            )
            it.title = span
            array.recycle()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_about) {
            startActivity(Intent(this, AboutActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        ttsProvider.releaseTts()
        super.onDestroy()
    }
}
