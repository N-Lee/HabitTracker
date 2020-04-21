import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.nathanlee.habittracker.models.Habit
import java.io.File
import java.io.FileWriter


class ReadWriteJson(dir: String) {
    val GSON = GsonBuilder().setPrettyPrinting().create()
    val FILE_NAME = "Habits.json"
    val DIR = dir

    inline fun <reified T> genericType() = object : TypeToken<T>() {}.type

    /*
    Save all habits to json
     */
    @Synchronized
    fun write(habits: MutableList<Habit>) {
        val jsonString = GSON.toJson(habits)
        val file = FileWriter(File(DIR, FILE_NAME))

        file.write(jsonString)
        file.close()
    }

    /*
    Load all habits from json to string
     */
    fun read(): MutableList<Habit> {
        val file = File(DIR + "/" + FILE_NAME)
        val jsonString: String = file.readText()

        val habitType = genericType<MutableList<Habit>>()
        return GSON.fromJson(jsonString, habitType)
    }

    fun exists(): Boolean {
        val file = File(DIR + "/" + FILE_NAME)
        return file.exists()
    }

    fun display(): String {
        val file = File(DIR + "/" + FILE_NAME)
        return file.readText()
    }
}
