package com.example.todolistapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.todolistapp.databinding.FragmentTodoListBinding
import com.google.android.material.snackbar.Snackbar

class TodoListFragment : Fragment() {
    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!
    private val todoList = mutableListOf<TodoItem>()
    private val completedList = mutableListOf<TodoItem>()
    private lateinit var toDoAdapter: TodoAdapter
    private lateinit var completedAdapter: TodoAdapter
    private var nextId = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("TodoListFragment", "onViewCreated called")

        // Initialize Adapters
        toDoAdapter = TodoAdapter(todoList) { item, isChecked ->
            if (isChecked) moveToCompleted(item)
        }
        completedAdapter = TodoAdapter(completedList) { item, isChecked ->
            if (!isChecked) moveToToDo(item)
        }

        // Set Adapters to RecyclerViews
        binding.rvToDoList.adapter = toDoAdapter
        binding.rvCompletedList.adapter = completedAdapter

        Log.d("TodoListFragment", "Adapters set to RecyclerViews")
        Log.d("TodoListFragment", "RecyclerView visibility: ${binding.rvToDoList.visibility}")
        Log.d("TodoListFragment", "RecyclerView height: ${binding.rvToDoList.height}")

        // Set click listener for Add button
        binding.btnAddTask.setOnClickListener {
            Log.d("TodoListFragment", "Button Add clicked")
            val task = binding.etNewTask.text?.toString()?.trim() ?: ""
            if (task.isNotEmpty()) {
                Log.d("TodoListFragment", "Adding task: $task")

                // Add to the fragment's todoList
                val newTodoItem = TodoItem(nextId++, task, false)
                todoList.add(newTodoItem)

                Log.d("TodoListFragment", "Todo List size after adding: ${todoList.size}")
                Log.d("TodoListFragment", "Todo items: ${todoList.map { it.task }}")

                // Notify adapter that data has changed
                toDoAdapter.notifyItemInserted(todoList.size - 1)

                // Also try notifyDataSetChanged as backup
                toDoAdapter.notifyDataSetChanged()

                Log.d("TodoListFragment", "Adapter notified")
                Log.d("TodoListFragment", "Adapter item count: ${toDoAdapter.itemCount}")

                // Clear input and show success message
                binding.etNewTask.text?.clear()
                binding.etNewTask.requestFocus()

                Snackbar.make(binding.root, "Tugas berhasil ditambahkan", Snackbar.LENGTH_SHORT).show()

                // Force layout update
                binding.rvToDoList.post {
                    Log.d("TodoListFragment", "RecyclerView height after update: ${binding.rvToDoList.height}")
                    Log.d("TodoListFragment", "RecyclerView child count: ${binding.rvToDoList.childCount}")
                }
            } else {
                Snackbar.make(binding.root, "Masukkan tugas terlebih dahulu", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Handle Enter key press in EditText
        binding.etNewTask.setOnEditorActionListener { _, _, _ ->
            binding.btnAddTask.performClick()
            true
        }

        // Memastikan EditText bisa fokus dan menerima input
        binding.etNewTask.setOnClickListener {
            binding.etNewTask.requestFocus()
        }

        // Add some test data to verify RecyclerView is working
        binding.root.post {
            Log.d("TodoListFragment", "Adding test data")
            todoList.add(TodoItem(999, "Test Item", false))
            toDoAdapter.notifyDataSetChanged()
            Log.d("TodoListFragment", "Test data added, list size: ${todoList.size}")
        }
    }

    private fun moveToCompleted(item: TodoItem) {
        val position = todoList.indexOf(item)
        if (position != -1) {
            todoList.removeAt(position)
            completedList.add(item)
            toDoAdapter.notifyItemRemoved(position)
            completedAdapter.notifyItemInserted(completedList.size - 1)
        }
    }

    private fun moveToToDo(item: TodoItem) {
        val position = completedList.indexOf(item)
        if (position != -1) {
            completedList.removeAt(position)
            item.isCompleted = false
            todoList.add(item)
            completedAdapter.notifyItemRemoved(position)
            toDoAdapter.notifyItemInserted(todoList.size - 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}