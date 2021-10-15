package uz.mobilestudio.photo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import uz.mobilestudio.photo.R
import uz.mobilestudio.photo.databinding.ActivityMainBinding
import uz.mobilestudio.photo.fragments.HomeFragment
import uz.mobilestudio.photo.fragments.LikedFragment
import uz.mobilestudio.photo.fragments.PopularFragment
import uz.mobilestudio.photo.fragments.RandomFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        var lastFragment = Fragment()

        val homeFragment = HomeFragment()
        val popularFragment = PopularFragment()
        val randomFragment = RandomFragment()
        val likedFragment = LikedFragment()
        supportFragmentManager.beginTransaction().add(R.id.container,likedFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.container,randomFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.container,popularFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.container,homeFragment).commit()
        supportFragmentManager.beginTransaction().hide(likedFragment).commit()
        supportFragmentManager.beginTransaction().hide(randomFragment).commit()
        supportFragmentManager.beginTransaction().hide(popularFragment).commit()

        lastFragment = homeFragment

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction().hide(lastFragment).commit()
                    lastFragment = homeFragment
                    supportFragmentManager.beginTransaction().show(lastFragment).commit()
                    true
                }
                R.id.popular -> {
                    supportFragmentManager.beginTransaction().hide(lastFragment).commit()
                    lastFragment = popularFragment
                    supportFragmentManager.beginTransaction().show(lastFragment).commit()
                    true
                }
                R.id.random -> {
                    supportFragmentManager.beginTransaction().hide(lastFragment).commit()
                    lastFragment = randomFragment
                    supportFragmentManager.beginTransaction().show(lastFragment).commit()
                    true
                }
                R.id.like -> {
                    supportFragmentManager.beginTransaction().hide(lastFragment).commit()
                    lastFragment = likedFragment
                    supportFragmentManager.beginTransaction().show(lastFragment).commit()
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()

//        navController = findNavController(R.id.my_nav_host_fragment)
//
//        binding.bottomNavigation.setupWithNavController(navController)
//
//        binding.bottomNavigation.setOnItemSelectedListener { it ->
//            when (it.itemId) {
//                R.id.home -> {
//                    navController.saveState()
//                    navController.popBackStack()
//                    navController.navigate(R.id.homeFragment)
//                    true
//                }
//                R.id.popular -> {
//                    navController.popBackStack()
//                    navController.navigate(R.id.popularFragment)
//                    true
//                }
//                R.id.random -> {
//                    navController.popBackStack()
//                    navController.navigate(R.id.randomFragment)
//                    true
//                }
//                R.id.like -> {
//                    navController.popBackStack()
//                    navController.navigate(R.id.likedFragment)
//                    true
//                }
//                else -> false
//            }
//        }
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