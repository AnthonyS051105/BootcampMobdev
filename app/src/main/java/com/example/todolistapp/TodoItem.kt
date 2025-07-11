package com.example.todolistapp

data class TodoItem(
    val id: Int,
    val task: String,
    var isCompleted: Boolean = false
)