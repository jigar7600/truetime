package com.jigar.library.sample

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jigar.library.sample.databinding.ActivitySampleBinding
import ga.jigar.library.truetime.legacy.TrueTime
import ga.jigar.library.truetime.legacy.TrueTimeRx
import ga.jigar.library.truetime.log.Logger
import ga.jigar.library.truetime.time.TrueTime2
import ga.jigar.library.truetime.time.TrueTimeImpl
import ga.jigar.library.truetime.time.TrueTimeParameters
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class GuideActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySampleBinding
    private val disposables = CompositeDisposable()

    private lateinit var trueTime: TrueTime2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "True Time Demo"

        binding.btnRefresh.setOnClickListener {
            refreshTime()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun refreshTime() {
        binding.deviceTime.text = "Device Time: (loading...)"

        kickOffTruetimeCoroutines()
        kickOffTrueTimeRx()
        kickOffTrueTimeOffline()

        binding.deviceTime.text = "Device Time: ${formatDate(Date())}"
    }

    private fun kickOffTruetimeCoroutines() {
        binding.truetimeNew.text = "TrueTime (Coroutines): (loading...)"

        val mainDispatcherScope = CoroutineScope(Dispatchers.Main.immediate)

        if (!::trueTime.isInitialized) {
            trueTime = TrueTimeImpl(logger = AndroidLogger)
        }

        val with = TrueTimeParameters(
            connectionTimeoutInMillis = 31428,
            retryCountAgainstSingleIp = 3,
            ntpHostPool = "pool.ntp.org",
            syncIntervalInMillis = 1_000
        )

        mainDispatcherScope.launch {
            trueTime.sync(with)
        }

        binding.truetimeNew.text = "TrueTime (Coroutines): ${formatDate(trueTime.nowSafely())}"

//        Timer("Kill Sync Job", false).schedule(12_000) {
//            job.cancel()
//        }
    }

    private fun kickOffTrueTimeRx() {
        binding.truetimeLegacy.text = "TrueTime (Rx) : (loading...)"

        val d = TrueTimeRx()
            .withConnectionTimeout(31428)
            .withRetryCount(100)
            .withOffline(true)
            .withSharedPreferencesCache(this)
            .withLoggingEnabled(false)
            .initializeRx("pool.ntp.org")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ date ->
                binding.truetimeLegacy.text = "TrueTime (Rx) : ${formatDate(date)}"
            }, {
                Log.e("Demo", "something went wrong when trying to initializeRx TrueTime", it)
            })

        disposables.add(d)
    }

    private fun kickOffTrueTimeOffline() {
        binding.deviceOffline.text = "TrueTime (offline) : (loading...)"

        val lastOfflineTime = TrueTime.getSystemTime(this)
        Log.i("TrueTimeOffline: ", lastOfflineTime.toString())
        val date = TrueTime.Offline()
        val dateUp = TrueTime.OfflineUp()
        binding.deviceOffline.text =
            "TrueTime (offline) : ${formatDate(date)}  \n JP: ${formatDate(dateUp)}"
    }

    private fun formatDate(date: Date): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant
                .ofEpochMilli(date.time)
                .atZone(ZoneId.of("America/Los_Angeles"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        } else {
            ""
        }
    }

    object AndroidLogger : Logger {
        override fun v(tag: String, msg: String) {
            Log.v(tag, msg)
        }

        override fun d(tag: String, msg: String) {
            Log.d(tag, msg)
        }

        override fun i(tag: String, msg: String) {
            Log.i(tag, msg)
        }

        override fun w(tag: String, msg: String) {
            Log.w(tag, msg)
        }

        override fun e(tag: String, msg: String, t: Throwable?) {
            Log.e(tag, "$msg ${t?.message}", t)
        }
    }

}