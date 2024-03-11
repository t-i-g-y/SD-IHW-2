package org.example

class Visitor(username: String, password: String) : User(username, password, "visitor") {
    fun placeOrder(menu: Menu, orderDetails: Map<String, Int>): Order {
        return Order(this, orderDetails.map { menu.getDish(it.key)!! to it.value }.toMap() as MutableMap<Dish, Int>)
    }

    fun addToOrder(order: Order, dish: Dish, quantity: Int) {
        if (!order.isCompleted) {
            order.addDish(dish, quantity)
        }
    }

    fun cancelOrder(order: Order) {
        if (!order.isCompleted) {
            order.cancel()
        }
    }

    fun payForOrder(order: Order) {
        if (order.isCompleted) {
            println("Paying for order: ${order.id}")
        }
    }

    // Метод для оставления отзыва
    fun leaveReview(dish: Dish, rating: Int, comment: String) {
        val review = Review(rating, comment)
        dish.addReview(review)
    }
}