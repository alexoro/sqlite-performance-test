package com.example.mobiussqlite.placeholder

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.mobiussqlite.R
import java.util.concurrent.Executors

class PlaceholderTestActivity : Activity() {

    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.placeholder_test)

        findViewById<View>(R.id.launch).setOnClickListener {
            executor.submit {
                try {
                    val test = PlaceholderTestImpl(this)
                    test.prepare()
                    val noPlaceholderDuration = test.runNoPlaceholder()
                    val withPlaceholderDuration = test.runWithPlaceholder()
                    Log.e("QQQQ", "No: ${noPlaceholderDuration}ms. With: ${withPlaceholderDuration}ms.")
                    test.dispose()
                } catch (th: Throwable) {
                    Log.e("QQQQ", "$th")
                }
            }
        }
    }

}