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
        FileWriter fileWriter;
        BufferedWriter bufferedWriter = null;

        try {
            fileWriter = new FileWriter(mergedFile, true);
            bufferedWriter = new BufferedWriter(fileWriter);

            for (File file : files) {

                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

                String line;
                while( (line = bufferedReader.readLine()) != null) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
                bufferedReader.close();
            }
            bufferedWriter.close();
        } catch (IOException ex) {
            System.err.println("IO Exception : "+ex.getMessage());
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
