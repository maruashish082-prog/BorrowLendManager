package com.borrowlend.manager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.borrowlend.manager.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private val onEdit: (Transaction) -> Unit,
    private val onDelete: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private val items = mutableListOf<Transaction>()
    private val currencyFmt = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    private val inFmt  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val outFmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    inner class ViewHolder(val b: ItemTransactionBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val b = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(b)
    }

    override fun onBindViewHolder(h: ViewHolder, position: Int) {
        val t = items[position]
        val ctx = h.itemView.context
        val isBorrow = t.type == "BORROW"

        val accentColor   = ctx.getColor(if (isBorrow) R.color.borrow_color else R.color.lend_color)
        val accentBg      = ctx.getColor(if (isBorrow) R.color.borrow_bg    else R.color.lend_bg)

        with(h.b) {
            // Avatar letter
            tvAvatar.text = t.name.first().uppercaseChar().toString()
            tvAvatar.backgroundTintList = android.content.res.ColorStateList.valueOf(accentBg)
            tvAvatar.setTextColor(accentColor)

            // Card left stripe
            viewStripe.setBackgroundColor(accentColor)

            // Name & amount
            tvName.text = t.name
            tvAmount.text = (if (isBorrow) "- " else "+ ") + currencyFmt.format(t.amount)
            tvAmount.setTextColor(accentColor)

            // Type badge
            tvType.text  = if (isBorrow) "BORROWED" else "LENT"
            tvType.backgroundTintList = android.content.res.ColorStateList.valueOf(accentBg)
            tvType.setTextColor(accentColor)

            // Date
            tvDate.text = try { outFmt.format(inFmt.parse(t.date)!!) } catch (e: Exception) { t.date }

            // Remarks
            if (t.remarks.isNotBlank()) {
                tvRemarks.text = t.remarks
                tvRemarks.visibility = android.view.View.VISIBLE
            } else {
                tvRemarks.visibility = android.view.View.GONE
            }

            btnEdit.setOnClickListener { onEdit(t) }
            btnDelete.setOnClickListener { onDelete(t) }
        }
    }

    override fun getItemCount() = items.size

    fun submitList(newList: List<Transaction>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newList.size
            override fun areItemsTheSame(o: Int, n: Int) = items[o].id == newList[n].id
            override fun areContentsTheSame(o: Int, n: Int) = items[o] == newList[n]
        })
        items.clear()
        items.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }
}
