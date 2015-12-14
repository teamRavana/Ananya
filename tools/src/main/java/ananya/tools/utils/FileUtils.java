package ananya.tools.utils;

import java.io.*;

public class FileUtils {

    /**
     *  ananya.tools.utils.FileUtils method to write string content to a file
     * @param filePath
     * @param content
     */
    public static void writeFile(String filePath, String content){

        writeFile(new File(filePath),content);
    }


    public static void writeFile(File file, String content){
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.flush();
            writer.close();
            System.out.println("Content Written to "+file.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Exception When writing the corpus file "+e.getMessage());
        }
    }


    public static void mergeFiles(File[] files, File mergedFile) {
        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            fstream = new FileWriter(mergedFile, true);
            out = new BufferedWriter(fstream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        for (File f : files) {
            System.out.println("merging: " + f.getName());
            FileInputStream fis;
            try {
                fis = new FileInputStream(f);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));

                String aLine;
                while ((aLine = in.readLine()) != null) {
                    out.write(aLine);
                    out.newLine();
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int countLines(File aFile) throws IOException {
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader(new FileReader(aFile));
            while ((reader.readLine()) != null);
            return reader.getLineNumber();
        } catch (Exception ex) {
            return -1;
        } finally {
            if(reader != null)
                reader.close();
        }
    }
}
