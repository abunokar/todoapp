package com.example.todolist.Fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.todolist.Model.ListItem
import com.example.todolist.R
import com.example.todolist.Services.FirebaseService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_list_item_detail.*
import kotlinx.android.synthetic.main.fragment_list_item_detail.view.*
import java.text.DateFormat
import java.util.*

class ListItemDetailFragment(itemID: String, categoryName: String) : Fragment() {

    companion object{
        val TAG = "ListItemDetailFragment"
    }

    private var itemID: String
    private var categoryName: String
    private lateinit var fabDone: FloatingActionButton
    private lateinit var dateEditText: EditText

    private var checked: Boolean = false
    private var subject: String = ""
    private var date: Date = Date()
    private var text: String = ""

    private val formate = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH)

    init {
        this.itemID = itemID
        this.categoryName = categoryName
    }

    constructor(
        id: String,
        checked: Boolean,
        subject: String,
        date: Date,
        text: String,
        categoryName: String
    ) : this(id, categoryName) {
        this.subject = subject
        this.text = text
        this.date = date
        this.checked = checked
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_item_detail, container, false)

        dateEditText = view.et_date
        dateEditText.inputType = InputType.TYPE_NULL

        view.et_date.setText(formate.format(date))
        view.et_subject.setText(subject)
        view.et_text.setText(text)

        dateEditText.setOnClickListener {
            val now = Calendar.getInstance()
            val year = now.get(Calendar.YEAR)
            val month = now.get(Calendar.MONTH)
            val dayOfMonth = now.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                context!!,
                DatePickerDialog.OnDateSetListener { view, y, m, d ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, y)
                    selectedDate.set(Calendar.MONTH, m)
                    selectedDate.set(Calendar.DAY_OF_MONTH, d)
                    dateEditText.setText(formate.format(selectedDate.time))

                },
                year,
                month,
                dayOfMonth
            )
            datePicker.show()
        }


        fabDone = view.fab_done
        fabDone.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                showAcceptDialog()
            }
        })
        return view
    }
    private fun createItem(): Boolean {

        val subject = et_subject.text.toString()
        val text = et_text.editableText.toString()
        val itemDate =formate.parse(et_date.text.toString())

        if(subject.isEmpty().or(text.isEmpty())){
            val builder = AlertDialog.Builder(context)
            builder.setMessage(getString(R.string.dialog_empty_fields_message))
            builder.setNeutralButton(android.R.string.ok,null)
            builder.show()
            return false
        }

        if (itemID.isEmpty()) {
            val item = ListItem(itemID, checked, itemDate!!, subject, text)
            FirebaseService.createItem(
                categoryName,item
            ) {
                if (it)
                    Log.i(TAG,"Updating item was successful")
                else
                    Log.i(TAG,"Updating item was unsucessful")
            }
        } else {
            FirebaseService.updateItem(
                categoryName,
                ListItem(itemID, checked, itemDate, subject, text)
            ) {
                if (it)
                    Log.i(TAG,"Updating item was successful")
                else
                    Log.i(TAG,"Updating item was unsucessful")
            }
        }
        return true
    }

    private fun showAcceptDialog() {
        val builder = AlertDialog.Builder(context)
        val title: String
        val message: String
        if (itemID.isEmpty()) {
            title = getString(R.string.dialog_create_item_title)
            message = getString(R.string.dialog_create_item_message)
        } else {
            title = getString(R.string.dialog_edit_item_title)
            message = getString(R.string.dialog_edit_item_message)
        }
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            if(createItem())
                activity?.supportFragmentManager?.popBackStack()
        }
        builder.setNegativeButton(android.R.string.no) {dialog,Unit->
            dialog.dismiss()
        }
        builder.show()
    }
}
