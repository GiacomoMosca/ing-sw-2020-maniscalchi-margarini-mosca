package it.polimi.ingsw.network.server;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * The Logger class allows writing information on a log.txt file.
 */
public class Logger {

    private final FileWriter fileWriter;

    /**
     * Logger constructor.
     * Creates a new FileWriter given the "log.txt" File Object.
     *
     * @throws IOException if the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason
     */
    public Logger() throws IOException {
        fileWriter = new FileWriter("log.txt", true);
    }

    /**
     * Closes the FileWriter.
     */
    public void close() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            //
        }
    }

    /**
     * Allows writing on the log.txt file.
     * Takes the string received as an argument and writes it on the log.txt file, adding LocalDateTime information.
     *
     * @param s the String to write
     */
    public void log(String s) {
        StringBuilder string = new StringBuilder();
        string.append("[");
        string.append(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        string.append("] ");
        string.append(s);
        string.append("\n");
        try {
            fileWriter.write(string.toString());
            fileWriter.flush();
        } catch (IOException e) {
            //
        }
    }

    /**
     * Allows writing on the log.txt file.
     * Takes the string received as an argument and writes it on the log.txt file, adding LocalDateTime information and the ERROR message.
     *
     * @param s
     */
    public void logError(String s) {
        StringBuilder string = new StringBuilder();
        string.append("[");
        string.append(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        string.append("] ERROR: ");
        string.append(s);
        string.append("\n");
        try {
            fileWriter.write(string.toString());
            fileWriter.flush();
        } catch (IOException e) {
            //
        }
    }

}
