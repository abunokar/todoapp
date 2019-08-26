package com.example.todolist.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.example.todolist.R
import com.example.todolist.Services.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import layout.CategoryItemAdapter
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.drawable.AnimationDrawable
import android.widget.EditText
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var categoryItemAdapter: CategoryItemAdapter
    private lateinit var animationDrawable: AnimationDrawable

    companion object {
        val REQUEST_FOR_ACTIVITY_CODE = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref =
            getSharedPreferences(getString(R.string.my_shared_preferences), Context.MODE_PRIVATE)
        val id = sharedPref.getString(getString(R.string.last_logged_user_id), "")
        FirebaseService.userID = id!!
        sharedPref.edit().remove(getString(R.string.last_logged_user_id)).apply()

        startBackgroundAnimation()
        userLogIn()
        setSupportActionBar(findViewById<Toolbar>(R.id.main_toolbar))

        val actionBar = supportActionBar
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.setDisplayShowHomeEnabled(false)
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(false)

        viewPager = findViewById(R.id.view_pager_categories)
        viewPager.pageMargin = 32
    }


    private fun userLogIn() {

        if (FirebaseService.userID == "") {
            startActivity(Intent(this@MainActivity, SignInActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseService.userID != "") {
            animationDrawable.start()
            val user = FirebaseAuth.getInstance().currentUser
            var text = "Hello " + user?.displayName
            tv_name.text = text
            Picasso.get().load(user?.photoUrl)
                .into(cv_avatar_image)
            val calendar = Calendar.getInstance()
            text = SimpleDateFormat("MMMM d,  Y", Locale.ENGLISH).format(calendar.time)
            tv_date.text = text

            FirebaseService.loadCategories {
                categoryItemAdapter = CategoryItemAdapter(it, this)
                viewPager.adapter = categoryItemAdapter
                setCouts(categoryItemAdapter.getCounts())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_screen_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item.itemId
        when (id) {
            R.id.add_category -> {
                showDialog()
            }
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, SignInActivity::class.java))
            }
        }
        return true
    }

    private fun showDialog() {
        val title = TextView(this)
        title.text = "Create new category"
        title.setPadding(0, 10, 0, 0);
        title.textSize = 22F
        title.gravity = Gravity.CENTER

        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_category, null)
        dialog.setView(dialogView)
        dialog.setCustomTitle(title)
        dialog.setPositiveButton("Create", { dialogInterface, i -> })
        dialog.setNegativeButton("Cancel", { dialogInterface, i -> })
        val customDialog = dialog.create()
        customDialog.show()
        customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            FirebaseService.createCategory(arrayListOf(dialogView.findViewById<EditText>(R.id.et_category_name).text.toString())) {
                if (it) {
                    customDialog.dismiss()
                } else {
                    Toast.makeText(this, "Error while adding new category.", Toast.LENGTH_SHORT)
                        .show()
                    customDialog.dismiss()
                }
            }
        }
    }

    private fun setCouts(counts: Pair<Long, Long>) {
        var taskText = findViewById<TextView>(R.id.tv_reminder_note)
        when (counts.second - counts.first) {
            0L -> taskText.setText(resources.getString(R.string.reminder_note_any))
            in 1..3 -> taskText.setText(resources.getString(R.string.reminder_note_few))
            else -> taskText.setText(resources.getString(R.string.reminder_note_lot))
        }
    }

    private fun startBackgroundAnimation() {
        animationDrawable = main_layout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(1000)
        animationDrawable.setExitFadeDuration(4000)
    }

    override fun onStop() {
        super.onStop()
        animationDrawable.stop()
        val sharedPref =
            getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE)
        sharedPref.edit().putString(getString(R.string.last_logged_user_id), FirebaseService.userID)
            .apply()
    }
}