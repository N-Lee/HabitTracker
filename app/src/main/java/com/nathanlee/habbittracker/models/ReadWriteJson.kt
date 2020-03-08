import com.google.gson.Gson
import com.google.gson.GsonBuilder
import android.widget.Toast
import android.content.Context.MODE_PRIVATE
import com.google.gson.reflect.TypeToken
import java.io.*


class ReadWriteJson(){

    val GSON = GsonBuilder().setPrettyPrinting().create()
    val FILE_NAME = "Habits.json"
    val DIR = "src/main/java/com/nathanlee/habittracker/files"

    inline fun <reified T> genericType() = object: TypeToken<T>() {}.type

    fun write(habits: MutableList<Habit>){

        var jsonString = GSON.toJson(habits)
        var file = FileWriter(File(DIR, FILE_NAME))

        file.write(jsonString)
        file.close()


    }

    fun read(): MutableList<Habit>{
        val file = File(DIR + "/" + FILE_NAME)
        val jsonString: String = file.readText()

        val habitType = genericType<MutableList<Habit>>()
        return GSON.fromJson(jsonString, habitType)
    }

    fun toJson(habit: Habit): String{
        return GSON.toJson(habit)
    }

}
