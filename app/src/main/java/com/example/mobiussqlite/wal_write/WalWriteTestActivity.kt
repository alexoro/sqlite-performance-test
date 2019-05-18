package com.example.mobiussqlite.wal_write

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.mobiussqlite.R
import java.util.concurrent.Executors

class WalWriteTestActivity : Activity() {

    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wal_write_test)

        findViewById<View>(R.id.launch).setOnClickListener {
            executor.submit {
                try {
                    val test = WalWriteTestImpl(this)
                    val journalCommitDurationMs = test.runJournal()
                    val walCommitDurationMs = test.runWal()
                    Log.e("QQQQ", "Journal: ${journalCommitDurationMs}ms. Wal: ${walCommitDurationMs}ms.")
                    test.dispose()
                } catch (th: Throwable) {
                    Log.e("QQQQ", "$th")
                }
            }
        }
    }

}