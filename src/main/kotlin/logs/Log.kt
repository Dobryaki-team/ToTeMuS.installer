package totemus.space.logs

/* imports */
// java
import java.io.BufferedWriter
import java.io.File
import java.io.OutputStreamWriter
import java.io.FileOutputStream
/* end */

var path: String = "${System.getProperty("user.dir")}\\installer.log"
var runs: Int = 0

fun log(message: String) {
    try {
        val logFile = File(path)
        if (!logFile.exists()) {
            logFile.createNewFile()
        }
        if (runs == 0) {
            if (logFile.exists()) {
                logFile.delete()
                logFile.createNewFile()
            }
            runs++
        }

        FileOutputStream(logFile, true).use { fileOutputStream ->
            OutputStreamWriter(fileOutputStream, Charsets.UTF_8).use { outputStreamWriter ->
                BufferedWriter(outputStreamWriter).use { bufferedWriter ->
                    bufferedWriter.write("$message\n")
                }
            }
        }
    } catch (e: Exception) {
        println("Error writing to the log: ${e.message}")
    }
}
