package com.nafanya.words.feature.about

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.nafanya.words.BuildConfig
import com.nafanya.words.R
import com.nafanya.words.databinding.ActivityAboutLayoutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.appVersionText.text = getString(
            R.string.app_version,
            getString(R.string.app_name),
            BuildConfig.VERSION_NAME
        )
        supportActionBar?.title = getString(R.string.about)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
