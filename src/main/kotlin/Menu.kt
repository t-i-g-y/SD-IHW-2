package org.example

// Меню
class Menu(val dishes: MutableList<Dish> = mutableListOf()) {
    fun addDish(dish: Dish) {
        dishes.add(dish)
    }

    fun removeDish(dishName: String) {
        dishes.removeIf { it.name == dishName }
    }

    fun getDish(dishName: String): Dish? {
        return dishes.find { it.name == dishName }
    }

    fun setDishDetails(dishName: String, quantity: Int, price: Double, preparationTime: Int) {
        getDish(dishName)?.apply {
            this.quantity = quantity
            this.price = price
            this.preparationTime = preparationTime
        }
    }

    fun listDishes() {
        dishes.forEach {
            println("Dish: ${it.name}, Quantity: ${it.quantity}, Price: ${it.price}, Preparation Time: ${it.preparationTime}")
        }
    }
}