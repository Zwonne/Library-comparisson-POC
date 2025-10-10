package com.example.startup

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.BaselineProfileMetrics
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class benchmarks the speed of app startup.
 * Run this benchmark to verify how effective a Baseline Profile is.
 * It does this by comparing [androidx.benchmark.macro.CompilationMode.None], which represents the app with no Baseline
 * Profiles optimizations, and [androidx.benchmark.macro.CompilationMode.Partial], which uses Baseline Profiles.
 *
 * Run this benchmark to see startup measurements and captured system traces for verifying
 * the effectiveness of your Baseline Profiles. You can run it directly from Android
 * Studio as an instrumentation test, or run all benchmarks for a variant, for example benchmarkRelease,
 * with this Gradle task:
 * ```
 * ./gradlew :benchmarks:connectedBenchmarkReleaseAndroidTest, or if you want per build variant, i.e.
 * ./gradlew :benchmarks:connectedRoomKoinBenchmarkReleaseAndroidTest
 * ```
 *
 * You should run the benchmarks on a physical device, not an Android emulator, because the
 * emulator doesn't represent real world performance and shares system resources with its host.
 *
 * For more information, see the [Macrobenchmark documentation](https://d.android.com/macrobenchmark#create-macrobenchmark)
 * and the [instrumentation arguments documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args).
 *
 * At the end of benchmarking, a report is generated in the form of a json file. It can be found in this module's
 * build/outputs/connected_android_test_additional_output folder. That can be used by the CI/CD process.
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class StartupBenchmarks {

    @get:Rule
    val rule = MacrobenchmarkRule()

    /**
     * Baseline profiles aren't used, and nothing is precompiled beforehand. When the measurements start, everything needs to be JIT-compiled.
     */
    @Test
    fun startupWithoutPreCompilation() = startup(CompilationMode.None())

    /**
     * Baseline profiles aren't used. Before the measurements start, the test runs the app once to let the runtime JIT-compile "hot" methods.
     * Then when the measurements start, the hot methods are precompiled.
     */
//    @Test
//    fun startupWithPartialCompilationAndDisabledBaselineProfile() = startup(
//        CompilationMode.Partial(baselineProfileMode = Disable, warmupIterations = 1),
//    )

    /**
     * Only methods that appear in the baseline profile will be precompiled (AOT).
     */
    @Test
    fun startupPrecompiledWithBaselineProfile() = startup(CompilationMode.Partial(BaselineProfileMode.Require))

    /**
     *  This is the default mode when running benchmarks on android 6 (API level 23)
     */
//    @Test
//    fun startupFullyPrecompiled() = startup(CompilationMode.Full())

    private fun startup(compilationMode: CompilationMode) {
        // The application id for the running build variant is read from the instrumentation arguments.
        rule.measureRepeated(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId") ?: throw Exception("targetAppId not passed as instrumentation runner arg"),
            metrics = BaselineProfileMetrics.allMetrics,
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            // More iterations result in higher statistical significance.
            iterations = 20,
            setupBlock = {
                pressHome()
            },
            measureBlock = {
                justStartTheActivity()

                // TODO Add interactions to wait for when your app is fully drawn.
                // The app is fully drawn when Activity.reportFullyDrawn is called.
                // For Jetpack Compose, you can use ReportDrawn, ReportDrawnWhen and ReportDrawnAfter
                // from the AndroidX Activity library.

                // Check the UiAutomator documentation for more information on how to
                // interact with the app.
                // https://d.android.com/training/testing/other-components/ui-automator
            }
        )
    }
}