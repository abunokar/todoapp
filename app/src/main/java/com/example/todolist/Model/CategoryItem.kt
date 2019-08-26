package com.example.todolist.Model


data class CategoryItem(
    var title: String,
    var image: String,
    var tasks: Long,
    var done: Long
) {
    constructor() : this("", "", 0, 0)

}