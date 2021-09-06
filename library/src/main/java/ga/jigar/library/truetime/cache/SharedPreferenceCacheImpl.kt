package ga.jigar.library.truetime.cache

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

internal class SharedPreferenceCacheImpl(context: Context) : CacheInterface {

    companion object {
        private const val KEY_CACHED_SHARED_PREFS = "ga.jigar.library.truetime.shared_preferences"
    }

    private val _sharedPreferences: SharedPreferences

    init {
        _sharedPreferences = context.getSharedPreferences(KEY_CACHED_SHARED_PREFS, MODE_PRIVATE)
    }

    override fun put(key: String, value: Long) {
        _sharedPreferences.edit().putLong(key, value).apply()
    }

    override fun put(key: String, value: String) {
        _sharedPreferences.edit().putString(key, value).apply()
    }

    override fun get(key: String, defaultValue: Long): Long {
        return _sharedPreferences.getLong(key, defaultValue)
    }

    override fun get(key: String, defaultValue: String): String {
        return _sharedPreferences.getString(key, defaultValue) ?: ""
    }

    override fun clear() {
        remove(CacheInterface.KEY_CACHED_BOOT_TIME)
        remove(CacheInterface.KEY_CACHED_DEVICE_UPTIME)
        remove(CacheInterface.KEY_CACHED_SNTP_TIME)
    }

    private fun remove(keyCachedBootTime: String) {
        _sharedPreferences.edit().remove(keyCachedBootTime).apply()
    }
}
