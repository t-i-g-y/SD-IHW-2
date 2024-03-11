package org.example
import java.io.File

// Хранение данных
object DataStorage {
    private const val FILENAME: String = "data.txt"

    fun saveData(data: String) {
        File(FILENAME).appendText(data + "\n")
    }

    fun loadData(): List<String> {
        if (!File(FILENAME).exists()) {
            return emptyList()
        }
        return File(FILENAME).readLines()
    }
}
