package com.example.todolist.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.todolist.Model.ListItem
import com.example.todolist.R
import com.example.todolist.Services.FirebaseService
import java.text.DateFormat
import java.util.*

class ListItemAdapter(
    catName: String,
    items: MutableList<ListItem>?,
    context: Context,
    callback: ReloadList
) : BaseAdapter() {
    private var items = mutableListOf<ListItem>()
    private var context: Context
    private var inflater: LayoutInflater
    private val categoryName: String
    private val mCallback: ReloadList

    init {
        this.items = items!!
        this.context = context
        inflater = LayoutInflater.from(this.context)
        categoryName = catName
        mCallback = callback
    }

    interface ReloadList {
        fun switchFragmentWithItem(position: Int)
        fun reloadList()
        fun reloadProgress(value: Boolean)
    }

    private class ViewHolder(row: View?) {
        var subjectTextView: TextView
        var checkboxView: CheckBox
        var deleteImage: ImageView
        var dateView: TextView

        init {
            this.subjectTextView = row?.findViewById(R.id.tv_subject) as TextView
            this.checkboxView = row.findViewById(R.id.checkbox) as CheckBox
            this.deleteImage = row.findViewById(R.id.iv_delete_item) as ImageView
            this.dateView = row.findViewById(R.id.tv_date) as TextView
        }
    }

    override fun getView(position: Int, parView: View?, viewGroup: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder

        if (parView == null) {
            view = inflater.inflate(R.layout.list_item, viewGroup, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = parView
            viewHolder = view.tag as ViewHolder
        }

        val item: ListItem = getItem(position)

        viewHolder.checkboxView.isChecked = item.checked
        viewHolder.subjectTextView.text = item.subject
        viewHolder.dateView.text =
            DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(item.date)

        if (item.checked) {
            viewHolder.deleteImage.visibility = View.VISIBLE
        } else {
            viewHolder.deleteImage.visibility = View.GONE
        }
        viewHolder.deleteImage.setOnClickListener {
            FirebaseService.deleteItem(item.id, categoryName) {
                if (it) {
                    mCallback.reloadList()
                    Toast.makeText(context.applicationContext, "Item deleted", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        context.applicationContext,
                        "Error while deleting item",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewHolder.checkboxView.setOnClickListener {
            if (viewHolder.checkboxView.isChecked) {
                mCallback.reloadProgress(true)
                viewHolder.deleteImage.visibility = View.VISIBLE
            } else {
                mCallback.reloadProgress(false)
                viewHolder.deleteImage.visibility = View.GONE
            }
            FirebaseService.itemCompletionSwitch(
                categoryName,
                item.id,
                viewHolder.checkboxView.isChecked
            )

        }
        view?.setOnClickListener {
            mCallback.switchFragmentWithItem(position)
        }
        return view as View
    }

    override fun getItem(position: Int) = items.get(position)

    override fun getItemId(p0: Int) = p0.toLong()

    override fun getCount() = items.size
}