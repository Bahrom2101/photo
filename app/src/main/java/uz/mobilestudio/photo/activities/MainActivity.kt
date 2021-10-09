package uz.mobilestudio.photo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import uz.mobilestudio.photo.R
import uz.mobilestudio.photo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

    }

    override fun onStart() {
        super.onStart()

        navController = findNavController(R.id.my_nav_host_fragment)

        binding.bottomNavigation.setupWithNavController(navController)

        binding.bottomNavigation.setOnItemSelectedListener { it ->
            when (it.itemId) {
                R.id.home -> {
                    navController.popBackStack()
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.popular -> {
                    navController.popBackStack()
                    navController.navigate(R.id.popularFragment)
                    true
                }
                R.id.random -> {
                    navController.popBackStack()
                    navController.navigate(R.id.randomFragment)
                    true
                }
                R.id.like -> {
                    navController.popBackStack()
                    navController.navigate(R.id.likedFragment)
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}