package com.example.mobiussqlite

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.mobiussqlite.index_equal.IndexEqualTestActivity
import com.example.mobiussqlite.index_range.IndexRangeTestActivity
import com.example.mobiussqlite.placeholder.PlaceholderTestActivity
import com.example.mobiussqlite.wal_commit.WalCommitTestActivity
import com.example.mobiussqlite.wal_write.WalWriteTestActivity

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        findViewById<View>(R.id.index_equal_test).setOnClickListener {
            startActivity(IndexEqualTestActivity::class.java)
        }
        findViewById<View>(R.id.index_range_test).setOnClickListener {
            startActivity(IndexRangeTestActivity::class.java)
        }
        findViewById<View>(R.id.placeholder_test).setOnClickListener {
            startActivity(PlaceholderTestActivity::class.java)
        }
        findViewById<View>(R.id.wal_write_test).setOnClickListener {
            startActivity(WalWriteTestActivity::class.java)
        }
        findViewById<View>(R.id.wal_commit_test).setOnClickListener {
            startActivity(WalCommitTestActivity::class.java)
        }
    }

    private fun startActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

}