package com.example.todolist.Model

import java.util.*

data class ListItem(
    var id: String
) {
    var checked: Boolean = false
    var date: Date = Date()
    var subject: String = ""
    var text: String = ""

    init {

    }

    constructor() : this("")
    constructor(
        id: String,
        checked: Boolean,
        date: Date,
        subject: String,
        text: String
    ) : this(id) {
        this.checked = checked
        this.date = date
        this.subject = subject
        this.text = text
    }
}


