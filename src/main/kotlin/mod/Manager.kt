@file:Suppress("SENSELESS_COMPARISON", "UNNECESSARY_SAFE_CALL", "ConstPropertyName")

package totemus.space.mod

/* imports */
// json
// okhttp3
// local
// java*
import com.google.gson.Gson
import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import okhttp3.Request
import totemus.space.Root.scaledMarskefont
import totemus.space.logs.log
import totemus.space.logs.path
import totemus.space.logs.removeLog
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.swing.*

/* end */

class Manager {
    companion object {
        private val client = OkHttpClient()
        private const val githubLink: String = "https://raw.githubusercontent.com/Dobryaki-team/ToTeMuS.installer/main/src/main/resources/links/api.link"
        private var apiLink: String = ((client.newCall(Request.Builder().url(githubLink).build()).execute().body.string())
            .replace(
                Regex("['\"]"),
                "")
            .trim()) + "/get"

        private fun downloadMod(url: String, destination: File) {
            try {
                log("The download of the mod has started: $url")
                val request = Request.Builder()
                    .url(url)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw RuntimeException("The mod could not be installed: $response")

                    FileOutputStream(destination).use { outputStream ->
                        response.body?.byteStream()?.use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
                log("The mod is loaded on the way: ${destination.absolutePath}")
            } catch (e: Exception) {
                log("Error loading the mod: ${e.message}")
                JOptionPane.showMessageDialog(null, "Error loading the mod: ${e.message}")
                throw e
            }
        }

        private fun downloadMods(progressBar: JProgressBar, destinationFolder: String, versions: Versions) {
            val modrinthMods = mapOf(
                "cit_resewn" to versions.citResewn,
                "totemus" to versions.totemus,
                "fabric_api" to versions.fabricApi
            )

            progressBar.maximum = modrinthMods.size
            var currentProgress = 0

            try {
                for ((modName, modVersion) in modrinthMods) {
                    val modLogName: String =
                        if (modName == "cit_resewn") {
                            "cit_resewn.jar"
                        } else (if (modName == "fabric_api") {
                            "fabric_api.jar"
                        } else {
                            "totemus.jar"
                        }).toString()
                    if (modVersion != null) {
                        val modUrl = when {
                            modVersion.startsWith("/") -> apiLink + modVersion
                            else -> getModLink(modVersion, modLogName).toString()
                        }
                        val modFile = File(destinationFolder, modLogName)
                        downloadMod(modUrl, modFile)
                        log("The $modLogName mod has been successfully loaded.")
                        currentProgress++
                        progressBar.value = currentProgress
                    } else {
                        log("The version of the mod $modLogName is null, skip the download.")
                    }
                }
                log("All mods have been successfully uploaded.")
                JOptionPane.showMessageDialog(
                    null,
                    "Mods have been downloaded successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                )
            } catch (e: Exception) {
                log("Error loading mods: ${e.message}")
                JOptionPane.showMessageDialog(
                    null,
                    "Error loading mods. Check the logs!",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE
                )
                return
            }
        }

        private fun getModLink(versionId: String, modName: String): String? {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.modrinth.com/v2/version/$versionId")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return null

                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val jsonResponse = Gson().fromJson(responseBody, JsonElement::class.java).asJsonObject
                        val filesArray = jsonResponse.getAsJsonArray("files")
                        if (filesArray.size() > 0) {
                            val firstFile = filesArray[0].asJsonObject
                            return firstFile.get("url").asString
                        }
                    }
                }
            } catch (e: IOException) {
                log("Error when getting a link to the mod ($modName): ${e.message}")
            }
            return null
        }

        fun chooseDownloadFolder(progressBar: JProgressBar) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("$apiLink/mod_versions")
                .build()

            @Suppress("RemoveCurlyBracesFromTemplate")
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        log("Couldn't get the mod versions, code: ${response.code}")
                        JOptionPane.showMessageDialog(null, "Couldn't get the mod versions, code: ${response.code}")
                        return
                    }

                    val modVersionsJson = response.body?.string()
                    if (modVersionsJson.isNullOrEmpty()) {
                        log("Failed to get the mods versions: an empty response from the server.")
                        JOptionPane.showMessageDialog(null, "Failed to get the mods versions: an empty response from the server.")
                        return
                    }

                    val modVersions = Gson().fromJson(modVersionsJson, Versions::class.java)
                    log("Received versions of mods: $modVersions")

                    val fileChooser = JFileChooser()
                    fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    val result = fileChooser.showOpenDialog(null)
                    if (result == JFileChooser.APPROVE_OPTION) {
                        val selectedFolder = fileChooser.selectedFile
                        val folder: String = if (!selectedFolder.absolutePath.endsWith("mods")) {
                            selectedFolder.absolutePath + File.separator + "mods"
                        } else {
                            (selectedFolder.absolutePath)
                        }
                        if (!File(folder).exists()) File(folder).mkdir()
                        log("The folder for downloading mods is selected: ${folder}")
                        downloadMods(progressBar, folder, modVersions)
                    }
                }
            } catch (e: Exception) {
                log("Error when selecting a folder to download mods: ${e.message}")
                JOptionPane.showMessageDialog(null,
                    "An error occurred when selecting a folder for downloading mods:" +
                        "\n---------------------------------------\n" +
                        "${e.message}" +
                        "\n---------------------------------------"
                )
            }
        }

        fun showLogs() {
            UIManager.put("OptionPane.buttonFont", scaledMarskefont)

            val logFile = File(path)
            if (!logFile.exists()) {
                if (!removeLog()) {
                    log("The log file was not found.")
                    JOptionPane.showMessageDialog(null, "The logs are empty.")
                    return
                } else {
                    log("Started.\nlog file path: $path")
                }
            }

            try {
                val reader = logFile.bufferedReader(Charsets.UTF_8)
                val logText = reader.use { it.readText() }
                val area = JTextArea(logText)
                area.apply {
                    rows = 20
                    columns = 50
                    isEditable = false
                }
                JOptionPane.showMessageDialog(
                    null,
                    JScrollPane(area),
                    "Logs",
                    JOptionPane.INFORMATION_MESSAGE

                )
            } catch (e: IOException) {
                log("Error opening the log file: ${e.message}")
                JOptionPane.showMessageDialog(null, "Error opening logs: ${e.message}")
            }
        }
    }
}