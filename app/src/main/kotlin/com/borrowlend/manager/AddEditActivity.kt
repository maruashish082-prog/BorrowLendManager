package com.borrowlend.manager

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.borrowlend.manager.databinding.ActivityAddEditBinding
import java.text.SimpleDateFormat
import java.util.*

class AddEditActivity : AppCompatActivity() {

    private lateinit var b: ActivityAddEditBinding
    private lateinit var db: DatabaseHelper
    private var editTx: Transaction? = null
    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddEditBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.hide()

        db = DatabaseHelper(this)

        // Default date = today
        val cal = Calendar.getInstance()
        selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
        b.etDate.setText(SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(cal.time))

        // Load existing if editing
        val id = intent.getLongExtra("transaction_id", -1L)
        if (id != -1L) {
            editTx = db.getAll().find { it.id == id }
            editTx?.let { populateForm(it) }
            b.tvTitle.text = "Edit Transaction"
            b.btnSave.text = "Update"
        }

        setupListeners()
    }

    private fun populateForm(t: Transaction) {
        b.etName.setText(t.name)
        b.etAmount.setText(t.amount.toBigDecimal().toPlainString())
        b.etRemarks.setText(t.remarks)
        selectedDate = t.date
        try {
            val d = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(t.date)!!
            b.etDate.setText(SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(d))
        } catch (_: Exception) { b.etDate.setText(t.date) }

        if (t.type == "LEND") b.rgType.check(R.id.rbLend)
        else b.rgType.check(R.id.rbBorrow)
    }

    private fun setupListeners() {
        // Back
        b.btnBack.setOnClickListener { finish() }

        // Date picker
        b.etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            if (selectedDate.isNotEmpty()) {
                try {
                    val d = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate)!!
                    cal.time = d
                } catch (_: Exception) {}
            }
            DatePickerDialog(this, R.style.DatePickerTheme,
                { _, y, m, d ->
                    val picked = Calendar.getInstance().apply { set(y, m, d) }
                    selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(picked.time)
                    b.etDate.setText(SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(picked.time))
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Save
        b.btnSave.setOnClickListener { save() }
    }

    private fun save() {
        val name    = b.etName.text.toString().trim()
        val amtStr  = b.etAmount.text.toString().trim()
        val remarks = b.etRemarks.text.toString().trim()
        val type    = if (b.rbLend.isChecked) "LEND" else "BORROW"

        if (name.isEmpty()) { b.etName.error = "Required"; return }
        if (amtStr.isEmpty()) { b.etAmount.error = "Required"; return }
        val amount = amtStr.toDoubleOrNull()
        if (amount == null || amount <= 0) { b.etAmount.error = "Enter valid amount"; return }
        if (selectedDate.isEmpty()) { Toast.makeText(this, "Select a date", Toast.LENGTH_SHORT).show(); return }

        val tx = Transaction(
            id      = editTx?.id ?: 0,
            name    = name,
            type    = type,
            amount  = amount,
            date    = selectedDate,
            remarks = remarks
        )

        if (editTx != null) {
            db.update(tx)
            Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show()
        } else {
            db.insert(tx)
            Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show()
        }
        finish()
    }
}
