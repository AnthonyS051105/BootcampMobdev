package com.example.todolistapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(
    private val todoList: MutableList<TodoItem>,
    private val onItemChecked: (TodoItem, Boolean) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbTask: CheckBox = itemView.findViewById(R.id.cb_task)
        val tvTask: TextView = itemView.findViewById(R.id.tv_task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val item = todoList[position]
        holder.tvTask.text = item.task
        holder.cbTask.isChecked = item.isCompleted

        // Clear previous listener to avoid conflicts
        holder.cbTask.setOnCheckedChangeListener(null)
        holder.cbTask.isChecked = item.isCompleted

        holder.cbTask.setOnCheckedChangeListener { _, isChecked ->
            item.isCompleted = isChecked
            onItemChecked(item, isChecked)
        }
    }

    override fun getItemCount(): Int = todoList.size

    // Remove this method - we'll handle adding in the Fragment
    // fun addTodo(task: String) {
    //     todoList.add(TodoItem(todoList.size + 1, task))
    //     notifyDataSetChanged()
    // }
}