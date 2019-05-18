package com.example.mobiussqlite.wal_commit

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.mobiussqlite.R
import java.util.concurrent.Executors

class WalCommitTestActivity : Activity() {

    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wal_commit_test)
        findViewById<View>(R.id.launch).setOnClickListener {
            executor.submit {
                try {
                    val test = WalCommitTestImpl(this)
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