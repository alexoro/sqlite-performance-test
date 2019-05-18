package com.example.mobiussqlite.placeholder

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.SystemClock

class PlaceholderTestImpl(context: Context) {

    private val openHelper by lazy { OpenHelper(context, "placeholder_test_impl") }

    fun prepare() {
        val db = openHelper.writableDatabase
        recreateTable(db)
        insertData(db)
    }

    fun runNoPlaceholder(): Long {
        return runImpl(usePlaceholder = false)
    }

    fun runWithPlaceholder(): Long {
        return runImpl(usePlaceholder = true)
    }

    /**
     * @return duration of test in milliseconds
     */
    private fun runImpl(usePlaceholder: Boolean): Long {
        val warmUpCount = 200
        val runCount = 5_000
        queryData(warmUpCount, usePlaceholder)
        return queryData(runCount, usePlaceholder)
    }

    fun dispose() {
        openHelper.close()
    }

    private fun recreateTable(db: SQLiteDatabase) {
        val sqls = listOf(
            "DROP TABLE IF EXISTS messages",
            """
            CREATE TABLE messages (
                id INT NOT NULL PRIMARY KEY,
                text TEXT NOT NULL,
                time INT NOT NULL
            )
            """,
            "CREATE INDEX idx_time ON messages(time, id)"
        )
        sqls.forEach {
            db.execSQL(it)
        }
    }

    private fun insertData(db: SQLiteDatabase) {
        val size = 10_000
        val values = (MutableList(size) { it }).shuffled()

        val sql = "INSERT INTO messages (id, text, time) VALUES (?,?,?)"
        val stmt = db.compileStatement(sql)
        db.beginTransactionNonExclusive()
        for (i in 0 until size) {
            stmt.bindLong(1, i.toLong())
            stmt.bindString(2, "")
            stmt.bindLong(3, values[i].toLong())
            stmt.executeInsert()
        }
        db.setTransactionSuccessful()
        db.endTransaction()
        stmt.close()
    }

    private fun queryData(count: Int, usePlaceholder: Boolean): Long {
        val db = openHelper.readableDatabase

        val queries = ArrayList<String>()
        val args = ArrayList<Array<String>?>()
        for (i in 0 until count) {
            if (usePlaceholder) {
                queries.add("SELECT id FROM messages WHERE time = ?")
                args.add(arrayOf("$i"))
            } else {
                queries.add("SELECT id FROM messages WHERE time = $i")
                args.add(null)
            }
        }

        var durationNs = 0L
        db.beginTransactionNonExclusive()
        for (i in 0 until count) {
            val start = time()
            val cursor = db.rawQuery(queries[i], args[i])
            if (cursor.moveToFirst()) {
                cursor.getInt(0)
            }
            val end = time()
            cursor.close()
            durationNs += (end - start)
        }
        db.setTransactionSuccessful()
        db.endTransaction()
        return durationNs / 1_000_000
    }

    private fun time(): Long {
        return SystemClock.elapsedRealtimeNanos()
    }

    class OpenHelper(context: Context, name: String) : SQLiteOpenHelper(context, name, null, 1) {
        override fun onCreate(db: SQLiteDatabase) = Unit
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
    }

}