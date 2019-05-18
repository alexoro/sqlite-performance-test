package com.example.mobiussqlite.wal_write

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.SystemClock

class WalWriteTestImpl(context: Context) {

    private val journalOpenHelper by lazy { JournalOpenHelper(context, context.getDatabasePath("wal_write_test_journal_impl").path) }
    private val walOpenHelper by lazy { WalOpenHelper(context, context.getDatabasePath("wal_write_test_wal_impl").path) }

    fun runJournal(): Long {
        return runImpl(journalOpenHelper.writableDatabase)
    }

    fun runWal(): Long {
        return runImpl(walOpenHelper.writableDatabase)
    }

    /**
     * @return duration of test in milliseconds
     */
    private fun runImpl(db: SQLiteDatabase): Long {
        val warmUpCount = 200
        val runCount = 10_000
        recreateTable(db)
        insertData(db, warmUpCount)
        return insertData(db, runCount)
    }

    fun dispose() {
        journalOpenHelper.close()
        walOpenHelper.close()
    }

    private fun recreateTable(db: SQLiteDatabase) {
        val sqls = listOf(
            "DROP TABLE IF EXISTS messages",
            """
            CREATE TABLE messages (
                id INT NOT NULL,
                text TEXT NOT NULL,
                time INT NOT NULL
            )
            """
        )
        sqls.forEach {
            db.execSQL(it)
        }
    }

    private fun insertData(db: SQLiteDatabase, count: Int): Long {
        val sql = "INSERT INTO messages (id, text, time) VALUES (?,?,?)"
        val stmt = db.compileStatement(sql)
        val startNs = time()
        db.beginTransactionNonExclusive()
        for (i in 0 until count) {
            stmt.bindLong(1, i.toLong())
            stmt.bindString(2, "")
            stmt.bindLong(3, 1)
            stmt.executeInsert()
        }
        db.setTransactionSuccessful()
        db.endTransaction()
        val endNs = time()
        stmt.close()

        return (endNs - startNs) / 1_000_000
    }

    private fun time(): Long {
        return SystemClock.elapsedRealtimeNanos()
    }

    class JournalOpenHelper(context: Context, name: String) : SQLiteOpenHelper(context, name, null, 1) {
        override fun onCreate(db: SQLiteDatabase) = Unit
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
        override fun onConfigure(db: SQLiteDatabase) {
            db.disableWriteAheadLogging()
        }
    }

    class WalOpenHelper(context: Context, name: String) : SQLiteOpenHelper(context, name, null, 1) {
        override fun onCreate(db: SQLiteDatabase) = Unit
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
        override fun onConfigure(db: SQLiteDatabase) {
            db.enableWriteAheadLogging()
            db.execSQL("PRAGMA synchronous=NORMAL")
        }
    }

}