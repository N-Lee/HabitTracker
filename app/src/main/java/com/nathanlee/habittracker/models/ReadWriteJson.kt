import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.nathanlee.habittracker.models.Habit
import java.io.*


class ReadWriteJson() {
    val GSON = GsonBuilder().setPrettyPrinting().create()
    val FILE_NAME = "Habits.json"
    val DIR = "src/main/java/com/nathanlee/habittracker/files"

    inline fun <reified T> genericType() = object : TypeToken<T>() {}.type

    /*
    Save all habits to json
     */
    fun write(habits: MutableList<Habit>) {
        var jsonString = GSON.toJson(habits)
        var file = FileWriter(File(DIR, FILE_NAME))

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
}
