import java.lang.Thread.currentThread
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.collections.HashMap

class CustomThreadLocal<T>(private var value: T) {
    private companion object CustomThreadLocalTable {
        val table: ConcurrentMap<Thread, CustomThreadLocalHashMap> = ConcurrentHashMap()
    }

    val uuid = UUID.randomUUID()

    fun get(): T? {
        val threadLocalTable = table.getOrPut(currentThread(), {CustomThreadLocalHashMap()})
        return threadLocalTable.getOrPut(uuid, value, this) as T?
    }

    fun set(newVal: T) {
        val threadLocalTable = table.getOrPut(currentThread(), { CustomThreadLocalHashMap()})
        threadLocalTable.put(uuid, newVal, this)
    }

    fun remove(): T? {
        val threadLocalTable = table.getOrPut(currentThread(), { CustomThreadLocalHashMap()})
        return threadLocalTable.remove(uuid) as T?
    }


    private class CustomThreadLocalSavedValue(k : CustomThreadLocal<*>, private val v: Any?) : WeakReference<CustomThreadLocal<*>>(k) {
    }

    private class CustomThreadLocalHashMap  {
        private val loadFactor = 2/3
        private var threshold = 16 * loadFactor
        private val table: MutableMap<UUID, CustomThreadLocalSavedValue> = HashMap()


        fun getOrPut(key: UUID, value: Any?, customThreadLocal: CustomThreadLocal<*>): Any? {
            return table[key] ?: value.also { x -> put(key, x, customThreadLocal) }
        }

        fun put(key: UUID, value: Any?, customThreadLocal: CustomThreadLocal<*>): CustomThreadLocalSavedValue? {
            if (table.size + 1 == threshold) {
                deleteUnusedValues()
                threshold *= loadFactor * 2
            }
            return table.put(key, CustomThreadLocalSavedValue(customThreadLocal, value))
        }

         fun remove(key: UUID): Any? {
            return table.remove(key)?.get()
        }

        private fun deleteUnusedValues() {
            val deletedKeys = HashSet<UUID>()
            table.forEach { (x, y) -> if (y.get() == null) deletedKeys.add(x)}
            table.keys.removeAll(deletedKeys)
        }
    }
}