package ru.hivislav.simpleweather.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.hivislav.simpleweather.R
import ru.hivislav.simpleweather.databinding.ActivityMainBinding
import ru.hivislav.simpleweather.view.main.MainFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, MainFragment.newInstance())
                .commit()
        }
    }
}