package com.example.startup

import androidx.benchmark.macro.MacrobenchmarkScope

/**
 * Make actions for this user flow reusable by both benchmark and profile generation.
 */
fun MacrobenchmarkScope.justStartTheActivity() {
    // Start default activity for your app
    startActivityAndWait()
}