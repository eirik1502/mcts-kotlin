package utils

object Timing {
    fun <T> measureTimeMillis(block: () -> T): Pair<T, Long> {
        val start = System.currentTimeMillis()
        val retVal = block()
        return retVal to System.currentTimeMillis() - start
    }

    fun <T> measureNanoTime(block: () -> T): Pair<T, Long> {
        val start = System.nanoTime()
        val retVal = block()
        return retVal to System.nanoTime() - start
    }
}