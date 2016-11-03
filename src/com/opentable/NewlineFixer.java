package com.opentable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

public class NewlineFixer {

    public static final Charset UTF8 = Charset.forName("UTF-8");
    public static final String separator = System.getProperty("line.separator");


    public static String openLine = "";
    public static Path newFile;

    /**
     * @param args First argument is name of input file, second arg is name of output file.
     */
    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        String newFileName = args[1];

        newFile = createNewFile(newFileName);

        try (Stream<String> stream = Files.lines(Paths.get(fileName), UTF8)) {

            stream.forEach(NewlineFixer::consumeLine);
        }
    }

    private static void consumeLine(String line) {
        if (skipLine(line))
            return;

        if (abnormalStartOfLine(line)) {
            openLine += line;
            line = openLine;
        }

        if (abnormalEndOfLine(line)) {
            openLine = line;
            openLine += "\\n";
        } else {
            line += separator;
            try {
                Files.write(newFile, line.getBytes(UTF8), StandardOpenOption.APPEND, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            } catch (IOException e) {
                System.out.println("Failure encountered.");
                e.printStackTrace();
            }

            //System.out.println(line);
            openLine = "";
        }
    }

    private static Path createNewFile(String fileName) throws IOException {
        Path filePath = Paths.get(fileName);
        Files.deleteIfExists(filePath);
        filePath = Files.createFile(filePath);
        return filePath;
    }

    private static boolean abnormalStartOfLine(String line) {
        return (line.length() > 0) && (line.charAt(0) != '"');
    }

    private static boolean abnormalEndOfLine(String line) {
        return (line.length() > 0) && (line.charAt(line.length() - 1) != '"');
    }

    private static boolean skipLine(String line) {
        return (line == null || line.length() == 0);
    }
}
