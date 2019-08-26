package com.example.todolist.Activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.todolist.Fragments.ListItemFragment
import com.example.todolist.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.category_header.*

class CategoryActivity : AppCompatActivity(),
    ListItemFragment.OnProgressChangedListener {

    private lateinit var titleView: TextView
    private lateinit var taskView: TextView
    private lateinit var progressBarView: ProgressBar
    private lateinit var percentageView: TextView
    private lateinit var imageView: CircleImageView
    private var done: Long = 0
    private var taskCount: Long = 0

    private lateinit var listItemFragment: ListItemFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        setSupportActionBar(findViewById<Toolbar>(R.id.category_toolbar))
        val actionBar = supportActionBar
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.setDisplayShowHomeEnabled(false)
        actionBar?.setDisplayShowCustomEnabled(false)
        actionBar?.setDisplayShowTitleEnabled(false)


        titleView = tv_category_name
        taskView = tv_remaining_tasks_count
        imageView = cv_avatar_image
        progressBarView = progress_bar
        percentageView = tv_percentage

        val intent = intent
        titleView.text = intent.getStringExtra("title")
        val imageIdentifier = intent.getIntExtra("imageIdentifier", 0)
        if (imageIdentifier != 0)
            imageView.setImageResource(imageIdentifier)


        done = intent.getLongExtra("done", 0)
        taskCount = intent.getLongExtra("tasks", 0)
        showProgress()

        listItemFragment = ListItemFragment(titleView.text.toString())

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, listItemFragment)
            .commit()
    }


    override fun onProgressChanged(progress: Pair<Long, Long>) {
        done = progress.first
        taskCount = progress.second
        showProgress()
    }

    private fun showProgress() {
        var text = "${done.toString()}/${taskCount.toString()}"
        taskView.setText(text)

        if (taskCount == 0L) {
            text = "100%"
            percentageView.text = text
            progressBarView.progress = 100
        } else {
            progressBarView.progress = (done * 100 / taskCount).toInt()
            text = "${(done * 100 / taskCount)}%"
            percentageView.setText(text)
        }
    }

    override fun onDoneChanged(value: Int) {
        done += value
        showProgress()
    }

    override fun onStop() {
        super.onStop()
        val intent = Intent()
        intent.putExtra("done", done)
        intent.putExtra("taskCount", taskCount)
        setResult(Activity.RESULT_OK, intent)
    }
}
