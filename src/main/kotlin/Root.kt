package totemus.space

/* imports */
// local
import handlers.Exit
import handlers.SetIcon
import totemus.space.logs.log
import totemus.space.mod.Manager
// java*
// java awt
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.GraphicsEnvironment
// java io
import java.io.File
import java.io.InputStream
// swing
import javax.swing.*
import javax.swing.plaf.FontUIResource
/* end */

@Suppress("ConstPropertyName", "MemberVisibilityCanBePrivate")
object Root {
    private const val fontSize = 15
    private const val NowGroteskFontSize: Float = 48.5F

    val FiraCodefontInputStream: InputStream? = javaClass.getResourceAsStream("/fonts/FiraCode-Regular.ttf")
    val FiraCode: Font = Font.createFont(Font.TRUETYPE_FONT, FiraCodefontInputStream)

    val MarskefontInputStream: InputStream? = javaClass.getResourceAsStream("/fonts/Marske.ttf")
    val Marskefont: Font = Font.createFont(Font.TRUETYPE_FONT, MarskefontInputStream)

    val NowGroteskFontInputStream: InputStream? = javaClass.getResourceAsStream("/fonts/NowGrotesk.otf")
    val NowGroteskFont: Font = Font.createFont(Font.TRUETYPE_FONT, NowGroteskFontInputStream)

    val scaledFiraCode: Font = FiraCode.deriveFont(fontSize.toFloat())
    val scaledMarskefont: Font = Marskefont.deriveFont(fontSize.toFloat())
    val scaledNowGroteskFont: Font = NowGroteskFont.deriveFont(NowGroteskFontSize)

    @JvmStatic
    val frame = JFrame("ToTeMuS installer")

    fun remove(path: String): Boolean {
        return File(path).delete()
    }

    fun start() {
        log("Started")
        val fontGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
        fontGraphicsEnvironment.registerFont(scaledFiraCode)
        setUIFont(FontUIResource(scaledFiraCode))

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val progressBar = JProgressBar(0, 100)
        progressBar.isIndeterminate = false
        panel.add(progressBar)

        val nameLabe = JTextArea("ToTeMuS.SPaCe", 10, 10)
        nameLabe.apply {
            isEditable = false
            caretPosition = SwingConstants.CENTER
            font = scaledNowGroteskFont
        }
        panel.add(nameLabe)

        val downloadButton = JButton("Download mods")
        downloadButton.addActionListener {
            progressBar.isIndeterminate = false
            progressBar.value = 0
            Manager.chooseDownloadFolder(progressBar)
            log("\n")
        }
        panel.add(downloadButton)

        val logButton = JButton("See logs")
        logButton.addActionListener {
            Manager.showLogs()
        }
        panel.add(logButton)

        SetIcon.set(frame)
        frame.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        frame.addWindowListener(Exit())
        frame.contentPane.add(panel, BorderLayout.CENTER)
        frame.size = Dimension(400, 200)
        frame.isVisible = true
    }

    private fun setUIFont(font: FontUIResource) {
        val keys: Array<Any> = UIManager.getDefaults().keys.toTypedArray()
        for (key in keys) {
            val value = UIManager.get(key)
            if (value is FontUIResource) {
                UIManager.put(key, font)
            }
        }
    }
}
