package uz.mobilestudio.photo.activities

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuItemImpl
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import uz.mobilestudio.photo.R
import uz.mobilestudio.photo.databinding.ActivityMainBinding
import uz.mobilestudio.photo.databinding.DrawerLayoutBinding
import uz.mobilestudio.photo.fragments.HomeFragment
import uz.mobilestudio.photo.fragments.LikedFragment
import uz.mobilestudio.photo.fragments.PopularFragment
import uz.mobilestudio.photo.fragments.RandomFragment
import java.io.File

class MainActivity : AppCompatActivity() {

    lateinit var binding: DrawerLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DrawerLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarDrawer.toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.appBarDrawer.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }

        var lastFragment: Fragment

        val homeFragment = HomeFragment()
        val popularFragment = PopularFragment()
        val randomFragment = RandomFragment()
        val likedFragment = LikedFragment()
        supportFragmentManager.beginTransaction().add(R.id.container, likedFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.container, randomFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.container, popularFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.container, homeFragment).commit()
        supportFragmentManager.beginTransaction().hide(likedFragment).commit()
        supportFragmentManager.beginTransaction().hide(randomFragment).commit()
        supportFragmentManager.beginTransaction().hide(popularFragment).commit()

        lastFragment = homeFragment

        binding.appBarDrawer.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    val toolbar = findViewById<Toolbar>(R.id.toolbar)
                    toolbar.title = getString(R.string.home)
                    supportFragmentManager.beginTransaction().hide(lastFragment).commit()
                    lastFragment = homeFragment
                    supportFragmentManager.beginTransaction().show(lastFragment).commit()
                    true
                }
                R.id.popular -> {
                    val toolbar = findViewById<Toolbar>(R.id.toolbar)
                    toolbar.title = getString(R.string.popular)
                    supportFragmentManager.beginTransaction().hide(lastFragment).commit()
                    lastFragment = popularFragment
                    supportFragmentManager.beginTransaction().show(lastFragment).commit()
                    true
                }
                R.id.random -> {
                    val toolbar = findViewById<Toolbar>(R.id.toolbar)
                    toolbar.title = getString(R.string.random)
                    supportFragmentManager.beginTransaction().hide(lastFragment).commit()
                    lastFragment = randomFragment
                    supportFragmentManager.beginTransaction().show(lastFragment).commit()
                    true
                }
                R.id.like -> {
                    val toolbar = findViewById<Toolbar>(R.id.toolbar)
                    toolbar.title = getString(R.string.liked)
                    supportFragmentManager.beginTransaction().hide(lastFragment).commit()
                    lastFragment = likedFragment
                    supportFragmentManager.beginTransaction().show(lastFragment).commit()
                    true
                }
                else -> false
            }
        }

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_share -> {
                    shareApp()
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_rate -> {
                    rateApp()
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_clear -> {
                    deleteCache(this)
                    return@setNavigationItemSelectedListener true
                }
                else -> return@setNavigationItemSelectedListener false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private fun shareApp() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Photos & Effector")
        sharingIntent.putExtra(
            Intent.EXTRA_TEXT,
            "https://play.google.com/store/apps/details?id=uz.mobilestudio.photo"
        )
        startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

    private fun rateApp() {
        val uri: Uri = Uri.parse("market://details?id=$packageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    private fun deleteCache(context: Context) {
        val builder = android.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.clear_cache))
            .setMessage(getString(R.string.ask_clear_cache))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { dialogInterface: DialogInterface, _: Int ->
                try {
                    val dir: File = context.cacheDir
                    deleteDir(dir)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface: DialogInterface, _: Int ->
            }
        val create = builder.create()
        create.setCanceledOnTouchOutside(false)
        create.show()
    }

    private fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }
}