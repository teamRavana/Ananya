package ananya.tools.corpus.extractor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class AbstractCorpusTextExtractor {
    private static final Log log = LogFactory.getLog(CorpusTextExtractor.class);

    public static final String PROPERTY_BASE_URL = "baseURL";
    public static final String PROPERTY_DESTINATION_URL = "destinationURL";

    private String BASE_URL;
    private String DESTINATION_URL;

    public static int total = 0;


    public void run() throws IOException, XMLStreamException {
        if (System.getProperty(PROPERTY_BASE_URL)== null || System.getProperty(PROPERTY_DESTINATION_URL) == null){
            new RuntimeException("Set BASE_URL and DESTINATION_URL");
        }

        File baseDir = new File(BASE_URL);

        // create the output directory
        new File(DESTINATION_URL).mkdir();

        System.out.println(baseDir.getAbsolutePath());
        System.out.println(baseDir.getPath());

        if (baseDir.exists() && baseDir.isDirectory()){
            convertFiles(baseDir,DESTINATION_URL);
        }

        System.out.println("Total Files : "+total);
    }


    protected void convertFiles(File file, String outputBaseDir) throws IOException, XMLStreamException {

        for (File fileToProcess : file.listFiles()){

            if (fileToProcess.isDirectory()){
                System.out.println("Directory ---> "+fileToProcess.getName());

                String outputDir = outputBaseDir+File.separator+fileToProcess.getName();

                // make a new directory in the destination
                new File(outputDir).mkdir();
                // recursive call
                convertFiles(fileToProcess, outputDir);
            }else{
                // extract the content from the file
                String[] output = processFile(fileToProcess, outputBaseDir);

                // write to the output file
                writeFile(output[0], output[1]);
            }

        }

    }

    // parse the xml file extract the content and create a corresponding text file in the output destination.
    protected abstract String[] processFile(File file, String outputBaseDir) throws IOException;


    protected void writeFile(String path, String content) throws IOException {

        File fileToWrite = new File(path);
        FileWriter fileWriter = new FileWriter(fileToWrite);
        // write the content on to a file
        fileWriter.write(content);
        // flush and close the writer
        fileWriter.flush();
        fileWriter.close();
    }




}
