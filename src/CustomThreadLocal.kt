import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class CustomThreadLocal<T>(private var value: T) {
    private companion object CustomThreadLocalTable{
        val table: ConcurrentMap<Long, ConcurrentMap<UUID, Any?>> = ConcurrentHashMap()
    }

    val uuid = UUID.randomUUID()

    fun get(): T? {
            val threadLocalTable = table.getOrPut(Thread.currentThread().id, { ConcurrentHashMap<UUID, Any?>()})
            return threadLocalTable.getOrPut(uuid, {value}) as T?
        }

    fun set(newVal: T) {
        val threadLocalTable = table.getOrPut(Thread.currentThread().id, { ConcurrentHashMap<UUID, Any?>()})
        threadLocalTable[uuid] = newVal
    }

    fun remove(): T? {
        val threadLocalTable = table.getOrPut(Thread.currentThread().id, { ConcurrentHashMap<UUID, Any?>()})
        return threadLocalTable.remove(uuid) as T?
    }
}




}