package ga.jigar.library.truetime.cache

interface TrueTimeCacheProvider {
    fun clear()

    fun update(
        bootTime: Long? = null,
        deviceUptime: Long? = null,
        sntpTime: Long? = null
    )
}