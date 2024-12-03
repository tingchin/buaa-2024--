package utils;


import java.io.*;

public class IOUtils {
    public static String readFile(String path) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public static void writeFile(String path, String content) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path, true));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void clear(String path) {
        delete(path);
        writeFile(path, "");
    }
}
