package totemus.space.logs

/* imports */
// local
// java
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

/* end */

var path: String = System.getProperty("user.dir") + File.separator + "installer.log"
var runs: Int = 0

fun removeLog(): Boolean {
    val file = File(path)
    try {
        if (file.exists()) {
            file.delete()
            file.createNewFile()
        } else if (!file.exists()) {
            file.createNewFile()
        }
        return true
    } catch (e: Exception) {
        println("Error creating and removing file, ${e.message}")
        return false
    }
}

fun log(message: String): Boolean {
    if (runs == 0) {
        removeLog()
        runs++
    }
    try {
        FileOutputStream(File(path), true).use { fileOutputStream ->
            OutputStreamWriter(fileOutputStream, Charsets.UTF_8).use { outputStreamWriter ->
                BufferedWriter(outputStreamWriter).use { bufferedWriter ->
                    bufferedWriter.write("$message\n")
                }
            }
        }

        return true
    } catch (e: Exception) {
        println("Error writing to the log: ${e.message}")
        return false
    }
}
