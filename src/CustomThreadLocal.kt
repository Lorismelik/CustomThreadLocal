import java.lang.Thread.currentThread
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class CustomThreadLocal<T>(private val value: T) {
    private companion object CustomThreadLocalTable {
        val table: ConcurrentMap<Thread, MutableMap<CustomThreadLocal<*>, Any?>> = ConcurrentHashMap()
    }


    fun get(): T? {
        val threadLocalTable = table.getOrPut(currentThread(), {WeakHashMap()})
        return threadLocalTable.getOrPut(this, {value}) as T?
    }

    fun set(newVal: T) {
        val threadLocalTable = table.getOrPut(currentThread(), { WeakHashMap()})
        threadLocalTable[this] = newVal
    }

    fun remove(): T? {
        val threadLocalTable = table.getOrPut(currentThread(), { WeakHashMap()})
        return threadLocalTable.remove(this) as T?
    }
}