package handlers;

import totemus.space.Root;
import totemus.space.logs.LogKt;
import javax.swing.*;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Exit extends WindowAdapter {
    private final Font scaledMarskefont = Root.INSTANCE.getScaledMarskefont();

    @Override
    public void windowClosing(WindowEvent e) {
        UIManager.put("OptionPane.buttonFont", scaledMarskefont);

        int option = JOptionPane.showConfirmDialog(
                null,
                "Do you really want to get out? This will delete the logs.",
                "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            if (clearLogsAndExit()) {
                System.exit(0);
            }
        }
    }

    private boolean clearLogsAndExit() {
        UIManager.put("OptionPane.buttonFont", scaledMarskefont);
        final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        try {
            //noinspection IfStatementWithIdenticalBranches
            if (Files.exists(Paths.get(LogKt.getPath()))) {
                Files.delete(Paths.get(LogKt.getPath()));

                JOptionPane.showMessageDialog(
                        null,
                        "The file has been removed successfully. Goodbye!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return true;
            } else {
                UIManager.put("OptionPane.messageFont", scaledMarskefont);
                UIManager.put("OptionPane.buttonFont", scaledMarskefont);

                JOptionPane.showMessageDialog(
                        null,
                        "The log file does not exist. The program is shutting down..",
                        "Success. Information",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return true;
            }
        } catch (IOException e) {
            UIManager.put("OptionPane.messageFont", scaledMarskefont);
            UIManager.put("OptionPane.buttonFont", scaledMarskefont);

            JOptionPane.showMessageDialog(
                    null,
                    "The log file could not be deleted. Please delete the " + LogKt.getPath() + " yourself",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            LOGGER.log(Level.SEVERE, "Error deleting the log file", e);
            return false;
        }
    }
}
