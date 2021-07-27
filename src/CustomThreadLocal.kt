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

    private val uuid: UUID = UUID.randomUUID()

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


    private class CustomThreadLocalSavedValue(k : CustomThreadLocal<*>, public var v: Any?) : WeakReference<CustomThreadLocal<*>>(k) {
    }

    private class CustomThreadLocalHashMap  {
        private val table: MutableMap<UUID, CustomThreadLocalSavedValue> = HashMap()


        fun getOrPut(key: UUID, value: Any?, customThreadLocal: CustomThreadLocal<*>): Any? {
            return table[key]?.v ?: value.also { x -> put(key, x, customThreadLocal) }
        }

        fun put(key: UUID, value: Any?, customThreadLocal: CustomThreadLocal<*>) {
            if (table.size % 2 == 0) {
                deleteUnusedValues()
            }
            val savedValue =  table[key]
            if (savedValue == null) {
                table[key] = CustomThreadLocalSavedValue(customThreadLocal, value)
            }
            savedValue?.v = value
        }

         fun remove(key: UUID): Any? {
            return table.remove(key)?.v
        }

        private fun deleteUnusedValues() {
            val deletedKeys = HashSet<UUID>()
            table.forEach { (x, y) -> if (y.get() == null) deletedKeys.add(x)}
            table.keys.removeAll(deletedKeys)
        }
    }
}