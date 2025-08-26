package com.epic.documentmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epic.documentmanager.R
import com.epic.documentmanager.utils.DateUtils

class DocumentAdapter<T : Any>(
    private var documents: List<T>,
    private val onItemClick: (T) -> Unit,
    private val onEditClick: (T) -> Unit,
    private val onDeleteClick: (T) -> Unit,
    private val canDelete: Boolean = true
) : RecyclerView.Adapter<DocumentAdapter<T>.DocumentViewHolder>() {

    inner class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvCode: TextView = itemView.findViewById(R.id.tvCode)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        val btnEdit: View = itemView.findViewById(R.id.btnEdit)
        val btnDelete: View = itemView.findViewById(R.id.btnDelete)

        init {
            itemView.setOnClickListener {
                onItemClick(documents[adapterPosition])
            }

            btnEdit.setOnClickListener {
                onEditClick(documents[adapterPosition])
            }

            btnDelete.setOnClickListener {
                onDeleteClick(documents[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document, parent, false)
        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = documents[position]

        // Use reflection to get common properties
        try {
            val nameField = document::class.java.getDeclaredField("nama")
            nameField.isAccessible = true
            val name = nameField.get(document) as? String ?: "Unknown"
            holder.tvName.text = name

            val codeField = document::class.java.getDeclaredField("uniqueCode")
            codeField.isAccessible = true
            val code = codeField.get(document) as? String ?: "Unknown"
            holder.tvCode.text = code

            val dateField = document::class.java.getDeclaredField("createdAt")
            dateField.isAccessible = true
            val date = dateField.get(document) as? Long ?: 0L
            holder.tvDate.text = DateUtils.formatDateTime(date)

            val phoneField = document::class.java.getDeclaredField("noTelepon")
            phoneField.isAccessible = true
            val phone = phoneField.get(document) as? String ?: "Unknown"
            holder.tvPhone.text = phone

        } catch (e: Exception) {
            holder.tvName.text = "Unknown"
            holder.tvCode.text = "Unknown"
            holder.tvDate.text = "Unknown"
            holder.tvPhone.text = "Unknown"
        }

        // Show/hide delete button based on permission
        holder.btnDelete.visibility = if (canDelete) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = documents.size

    fun updateDocuments(newDocuments: List<T>) {
        documents = newDocuments
        notifyDataSetChanged()
    }

    fun getDocument(position: Int): T = documents[position]
}