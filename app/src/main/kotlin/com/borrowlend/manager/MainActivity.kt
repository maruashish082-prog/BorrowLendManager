package com.borrowlend.manager

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.borrowlend.manager.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: TransactionAdapter
    private var currentFilter = "ALL"
    private val currency = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = DatabaseHelper(this)
        setupRecyclerView()
        setupTabs()
        setupFab()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(
            onEdit   = { tx -> openAddEdit(tx) },
            onDelete = { tx -> confirmDelete(tx) }
        )
        b.recyclerView.adapter = adapter
        b.recyclerView.addItemDecoration(
            androidx.recyclerview.widget.DividerItemDecoration(this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL).also {
                it.setDrawable(getDrawable(R.drawable.item_divider)!!)
            }
        )
    }

    private fun setupTabs() {
        b.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentFilter = when (tab.position) {
                    1    -> "BORROW"
                    2    -> "LEND"
                    else -> "ALL"
                }
                loadData()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupFab() {
        b.fabAdd.setOnClickListener { openAddEdit(null) }
    }

    private fun loadData() {
        val list = db.getAll(currentFilter)
        adapter.submitList(list)

        // Summary
        val totalBorrow = db.getTotalByType("BORROW")
        val totalLend   = db.getTotalByType("LEND")
        val net         = totalLend - totalBorrow

        b.tvTotalBorrow.text = currency.format(totalBorrow)
        b.tvTotalLend.text   = currency.format(totalLend)
        b.tvNetBalance.text  = (if (net >= 0) "+" else "") + currency.format(net)
        b.tvNetBalance.setTextColor(
            getColor(if (net >= 0) R.color.lend_color else R.color.borrow_color)
        )

        // Footer total
        val filteredTotal = list.sumOf { if (it.type == "LEND") it.amount else -it.amount }
        if (list.isEmpty()) {
            b.layoutFooter.visibility = View.GONE
            b.layoutEmpty.visibility  = View.VISIBLE
        } else {
            b.layoutFooter.visibility = View.VISIBLE
            b.layoutEmpty.visibility  = View.GONE
            val label = when (currentFilter) {
                "BORROW" -> "Total Borrowed"
                "LEND"   -> "Total Lent"
                else     -> "Net Balance"
            }
            b.tvFooterLabel.text = label
            b.tvFooterAmount.text = (if (filteredTotal >= 0) "+" else "") + currency.format(filteredTotal)
            b.tvFooterAmount.setTextColor(
                getColor(if (filteredTotal >= 0) R.color.lend_color else R.color.borrow_color)
            )
        }
    }

    private fun openAddEdit(tx: Transaction?) {
        val intent = Intent(this, AddEditActivity::class.java)
        if (tx != null) intent.putExtra("transaction_id", tx.id)
        startActivity(intent)
    }

    private fun confirmDelete(tx: Transaction) {
        AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setTitle("Delete Transaction")
            .setMessage("Delete entry for \"${tx.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                db.delete(tx.id)
                loadData()
                showSnack("Deleted!")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSnack(msg: String) {
        com.google.android.material.snackbar.Snackbar
            .make(b.root, msg, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT)
            .show()
    }
}
