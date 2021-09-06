package ga.jigar.library.truetime.cache

interface CacheInterface {

    companion object {
        const val KEY_PREFERENCES = "ga.jigar.library.truetime.shared_preferences"
        const val KEY_CACHED_BOOT_TIME = "ga.jigar.library.truetime.cached_boot_time"
        const val KEY_CACHED_DEVICE_UPTIME = "ga.jigar.library.truetime.cached_device_uptime"
        const val KEY_CACHED_SNTP_TIME = "ga.jigar.library.truetime.cached_sntp_time"
        const val KEY_CACHED_SYSTEM_TIME = "ga.jigar.library.truetime.cached_system_time"
        const val KEY_CACHED_BOOT_ID = "ga.jigar.library.truetime.cached_boot_id"
    }

    fun put(key: String, value: Long)
    fun put(key: String, value: String)

    fun get(key: String, defaultValue: Long): Long
    fun get(key: String, defaultValue: String): String

    fun clear()
}
