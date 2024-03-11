package org.example

class Admin(username: String, password: String) : User(username, password, "admin") {
    fun addDish(menu: Menu, dish: Dish) {
        menu.addDish(dish)
    }

    fun removeDish(menu: Menu, dishName: String) {
        menu.removeDish(dishName)
    }

    fun setDishDetails(menu: Menu, dishName: String, quantity: Int, price: Double, preparationTime: Int) {
        menu.setDishDetails(dishName, quantity, price, preparationTime)
    }
}