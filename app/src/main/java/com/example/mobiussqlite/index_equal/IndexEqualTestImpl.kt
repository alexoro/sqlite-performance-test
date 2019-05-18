package com.example.mobiussqlite.index_equal

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.SystemClock

class IndexEqualTestImpl(context: Context) {

    private val openHelper by lazy { OpenHelper(context, "index_equal_test_impl") }

    fun prepare() {
        val db = openHelper.writableDatabase
        recreateTable(db)
        insertData(db)
    }

    fun runNoIndex(): Long {
        return runImpl()
    }

    fun runWithIndex(): Long {
        createIndex()
        val duration = runImpl()
        dropIndex()
        return duration
    }

    /**
     * @return duration of test in milliseconds
     */
    private fun runImpl(): Long {
        val warmUpCount = 200
        val runCount = 5_000
        queryData(warmUpCount)
        val durationNs = queryData(runCount)
        return (durationNs / 1_000_000)
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
            """
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

    private fun queryData(count: Int): Long {
        val db = openHelper.readableDatabase

        var durationNs = 0L
        val query = "SELECT id FROM messages WHERE time = ?"
        val args = arrayOf("")
        db.beginTransactionNonExclusive()
        for (i in 0 until count) {
            args[0] = "$i"
            val start = time()
            val cursor = db.rawQuery(query, args)
            if (cursor.moveToFirst()) {
                cursor.getInt(0)
            }
            val end = time()
            cursor.close()
            durationNs += (end - start)
        }
        db.setTransactionSuccessful()
        db.endTransaction()

        return durationNs
    }

    private fun createIndex() {
        openHelper.readableDatabase.execSQL("CREATE INDEX idx_time ON messages(time)")
    }

    private fun dropIndex() {
        openHelper.readableDatabase.execSQL("DROP INDEX idx_time")
    }

    private fun time(): Long {
        return SystemClock.elapsedRealtimeNanos()
    }

    class OpenHelper(context: Context, name: String) : SQLiteOpenHelper(context, name, null, 1) {
        override fun onCreate(db: SQLiteDatabase) = Unit
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
    }

}