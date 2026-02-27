package com.borrowlend.manager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME    = "borrow_lend.db"
        private const val DB_VERSION = 1
        const val TABLE      = "transactions"
        const val COL_ID      = "id"
        const val COL_NAME    = "name"
        const val COL_TYPE    = "type"
        const val COL_AMOUNT  = "amount"
        const val COL_DATE    = "date"
        const val COL_REMARKS = "remarks"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE (
                $COL_ID      INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME    TEXT    NOT NULL,
                $COL_TYPE    TEXT    NOT NULL,
                $COL_AMOUNT  REAL    NOT NULL,
                $COL_DATE    TEXT    NOT NULL,
                $COL_REMARKS TEXT    DEFAULT ''
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    /* ── INSERT ── */
    fun insert(t: Transaction): Long {
        return writableDatabase.insert(TABLE, null, t.toValues())
    }

    /* ── UPDATE ── */
    fun update(t: Transaction): Int {
        return writableDatabase.update(
            TABLE, t.toValues(), "$COL_ID=?", arrayOf(t.id.toString())
        )
    }

    /* ── DELETE ── */
    fun delete(id: Long): Int {
        return writableDatabase.delete(TABLE, "$COL_ID=?", arrayOf(id.toString()))
    }

    /* ── READ ALL ── */
    fun getAll(filter: String = "ALL"): List<Transaction> {
        val where = when (filter) {
            "BORROW" -> "$COL_TYPE='BORROW'"
            "LEND"   -> "$COL_TYPE='LEND'"
            else     -> null
        }
        val cursor = readableDatabase.query(
            TABLE, null, where, null, null, null, "$COL_DATE DESC, $COL_ID DESC"
        )
        val list = mutableListOf<Transaction>()
        cursor.use {
            while (it.moveToNext()) {
                list += Transaction(
                    id      = it.getLong(it.getColumnIndexOrThrow(COL_ID)),
                    name    = it.getString(it.getColumnIndexOrThrow(COL_NAME)),
                    type    = it.getString(it.getColumnIndexOrThrow(COL_TYPE)),
                    amount  = it.getDouble(it.getColumnIndexOrThrow(COL_AMOUNT)),
                    date    = it.getString(it.getColumnIndexOrThrow(COL_DATE)),
                    remarks = it.getString(it.getColumnIndexOrThrow(COL_REMARKS))
                )
            }
        }
        return list
    }

    /* ── SUMMARY ── */
    fun getTotalByType(type: String): Double {
        val cursor = readableDatabase.rawQuery(
            "SELECT SUM($COL_AMOUNT) FROM $TABLE WHERE $COL_TYPE=?", arrayOf(type)
        )
        return cursor.use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }
    }

    private fun Transaction.toValues() = ContentValues().apply {
        put(COL_NAME, name)
        put(COL_TYPE, type)
        put(COL_AMOUNT, amount)
        put(COL_DATE, date)
        put(COL_REMARKS, remarks)
    }
}
