package com.example.mobiussqlite.index_range

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.mobiussqlite.R
import java.util.concurrent.Executors

class IndexRangeTestActivity : Activity() {

    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.index_range_test)

        findViewById<View>(R.id.launch).setOnClickListener {
            executor.submit {
                try {
                    val test = IndexRangeTestImpl(this)
                    test.prepare()
                    val noIndexDuration = test.runNoIndex()
                    val withIndexDuration = test.runWithIndex()
                    Log.e("QQQQ", "No: ${noIndexDuration}ms. With: ${withIndexDuration}ms.")
                    test.dispose()
                } catch (th: Throwable) {
                    Log.e("QQQQ", "$th")
                }
            }
        }
    }

}