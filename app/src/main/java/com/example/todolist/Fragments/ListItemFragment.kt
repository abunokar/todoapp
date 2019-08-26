package com.example.todolist.Fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.Adapter.ListItemAdapter
import com.example.todolist.Model.ListItem
import com.example.todolist.R
import com.example.todolist.Services.FirebaseService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class ListItemFragment(name: String) : Fragment(), ListItemAdapter.ReloadList {

    private val categoryName: String = name.toLowerCase(Locale.ENGLISH)
    private lateinit var listView: ListView
    private lateinit var adapter: ListItemAdapter
    private lateinit var fabCreateTask: FloatingActionButton
    var items = mutableListOf<ListItem>()

    private lateinit var onProgressChangeListener: OnProgressChangedListener

    interface OnProgressChangedListener {
        fun onProgressChanged(progress: Pair<Long, Long>)
        fun onDoneChanged(value: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_item, container, false)

        loadListView()

        fabCreateTask = view.findViewById(R.id.fab_create_task) as FloatingActionButton
        fabCreateTask.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                switchFragments()
            }
        })
        return view
    }

    override fun onResume() {
        super.onResume()
        loadListView()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            onProgressChangeListener = context as OnProgressChangedListener
        } catch (e: ClassCastException) {
            throw java.lang.ClassCastException(context.toString() + "must Override methodds")
        }
    }

    fun switchFragments() {
        val itemDetailFragment = ListItemDetailFragment("", categoryName)
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
            .replace(R.id.container, itemDetailFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setProgress(items: MutableList<ListItem>) {
        var done: Long = 0
        for (item in items) {
            if (item.checked)
                done++
        }
        onProgressChangeListener.onProgressChanged(Pair(done, items.size.toLong()))
    }

    fun loadListView() {
        FirebaseService.loadItems(categoryName) {
            items = it
            listView = view!!.findViewById(R.id.item_list_fragment_list_view)
            adapter = ListItemAdapter(categoryName, it, context!!, this)
            listView.adapter = adapter
            setProgress(it)
        }
    }

    override fun reloadList() {
        loadListView()
    }

    override fun reloadProgress(value: Boolean) {
        if (value) {
            onProgressChangeListener.onDoneChanged(1)
        } else {
            onProgressChangeListener.onDoneChanged(-1)
        }
    }

    override fun switchFragmentWithItem(position: Int) {
        val item = items.get(position)
        val itemDetailFragment = ListItemDetailFragment(item.id,item.checked,item.subject,item.date,item.text, categoryName)
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
            .replace(R.id.container, itemDetailFragment)
            .addToBackStack(null)
            .commit()
    }
}
