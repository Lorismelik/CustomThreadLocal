import kotlin.concurrent.thread

fun main() {
    val threadLocal: CustomThreadLocal<Int> = CustomThreadLocal(1)
    thread(start = true) {
        println("thread(): ${Thread.currentThread()} threadLocal: ${threadLocal.get()}")
        threadLocal.set(3)
        println("thread(): ${Thread.currentThread()} threadLocal: ${threadLocal.get()}")
        Thread.sleep(3000)
        println("thread(): ${Thread.currentThread()} threadLocal: ${threadLocal.get()}")
    }
    println("thread(): ${Thread.currentThread()} threadLocal: ${threadLocal.get()}")
    Thread.sleep(500)
    println("thread(): ${Thread.currentThread()} threadLocal: ${threadLocal.get()}")
    threadLocal.set(2)
    println("thread(): ${Thread.currentThread()} threadLocal: ${threadLocal.get()}")
}


