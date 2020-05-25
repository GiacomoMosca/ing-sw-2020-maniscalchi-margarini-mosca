package it.polimi.ingsw.network.server;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Logger {

    private final FileWriter fileWriter;

    public Logger() throws IOException {
        fileWriter = new FileWriter("log.txt", true);
    }

    public void close() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            //
        }
    }

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
