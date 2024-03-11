package org.example
import kotlinx.coroutines.*
import kotlin.concurrent.thread

class Order(private val user: User, val dishes: MutableMap<Dish, Int>) : Subject, Comparable<Order> {
    val id: Int = orderIdCounter++
    var status: OrderStatus = OrderStatus.ACCEPTED
        private set
    var isCompleted: Boolean = false
        private set
    private val observers = mutableListOf<Observer>()
    var totalPrice: Double = 0.0
        private set

    // Рассчитываем приоритет на основе общей стоимости заказа
    val priority: Int
        get() = dishes.entries.sumOf { it.key.price * it.value }.toInt()

    init {
        processOrder()
    }

    private fun calculateTotalPrice() {
        totalPrice = dishes.entries.sumOf { it.key.price * it.value }
    }

    private fun processOrder() {
        status = OrderStatus.PREPARING
        thread {
            val totalTime = dishes.entries.sumOf { it.key.preparationTime * it.value }
            Thread.sleep(totalTime.toLong())
            status = OrderStatus.READY
            println("Order $id is ready.")
            isCompleted = true
            notifyObservers()
        }
    }

    override fun compareTo(other: Order): Int {
        return other.priority - this.priority
    }

    override fun attach(observer: Observer) {
        observers.add(observer)
    }

    override fun detach(observer: Observer) {
        observers.remove(observer)
    }

    override fun notifyObservers() {
        observers.forEach { it.update(this) }
    }

    fun pay() {
        if (status == OrderStatus.READY) {
            status = OrderStatus.PAID
            println("Order $id has been paid.")
            notifyObservers()
        } else {
            println("Order $id is not ready to be paid.")
        }
    }

    fun addDish(dish: Dish, quantity: Int) {
        if (dishes.containsKey(dish)) {
            dishes[dish] = dishes[dish]!! + quantity
        } else {
            dishes[dish] = quantity
        }
    }

    fun cancel() {
        dishes.clear()
        isCompleted = true
        println("Order $id was cancelled.")
    }

    companion object {
        private var orderIdCounter = 1
    }
}
