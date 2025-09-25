package com.example.vhodnoycontrol2

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShoppingAdapter
    private val items: MutableList<ShoppingItem> = mutableListOf()
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("shopping_list", MODE_PRIVATE)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ShoppingAdapter(items, ::deleteItem, ::editItem)
        recyclerView.adapter = adapter

        loadItems()

        val addButton: Button = findViewById(R.id.add_button)
        addButton.setOnClickListener {
            showAddDialog()
        }

        // Swipe to delete
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                deleteItem(position)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onStop() {
        super.onStop()
        saveItems()
    }

    private fun showAddDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit, null)
        val nameEdit = dialogView.findViewById<TextView>(R.id.edit_name)
        val quantityEdit = dialogView.findViewById<TextView>(R.id.edit_quantity)

        AlertDialog.Builder(this)
            .setTitle("Add Item")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameEdit.text.toString()
                val quantity = quantityEdit.text.toString().toIntOrNull() ?: 1
                if (name.isNotBlank()) {
                    val item = ShoppingItem(name, quantity)
                    adapter.addItem(item)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun editItem(position: Int) {
        val item = items[position]
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit, null)
        val nameEdit = dialogView.findViewById<TextView>(R.id.edit_name)
        val quantityEdit = dialogView.findViewById<TextView>(R.id.edit_quantity)
        nameEdit.text = item.name
        quantityEdit.text = item.quantity.toString()

        AlertDialog.Builder(this)
            .setTitle("Edit Item")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val name = nameEdit.text.toString()
                val quantity = quantityEdit.text.toString().toIntOrNull() ?: 1
                if (name.isNotBlank()) {
                    val updatedItem = ShoppingItem(name, quantity)
                    adapter.updateItem(position, updatedItem)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteItem(position: Int) {
        adapter.removeItem(position)
    }

    private fun saveItems() {
        val json = gson.toJson(items)
        sharedPreferences.edit().putString("items", json).apply()
    }

    private fun loadItems() {
        val json = sharedPreferences.getString("items", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<ShoppingItem>>() {}.type
            val loadedItems: MutableList<ShoppingItem> = gson.fromJson(json, type)
            items.addAll(loadedItems)
            adapter.notifyDataSetChanged()
        }
    }
}