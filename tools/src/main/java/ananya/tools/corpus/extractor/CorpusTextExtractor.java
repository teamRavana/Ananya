package ananya.tools.corpus.extractor;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.IOUtils;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CorpusTextExtractor {


    public static final String BASE_URL = "/home/farazath/Desktop/FYP_Resources/Corpus/sinmin";
    public static final String DESTINATION_URL = "/home/farazath/Desktop/FYP_Resources/Corpus/sinmin_extract_22Nov";

    public static final String DOCUMENT_POST = "post";
    public static final String POST_CONTENT = "content";

    public static final int MINIMUM_WORDS_PER_FILE = 400;

    public static int total = 0;
    public static int totalWords = 0;
    public static int fileCreated = 0;
    public static int emptyFiles = 0;
    public static int totalPosts = 0;

   static Set uniqueWords = new HashSet();

    public static void main(String[] args) throws IOException, XMLStreamException {

        File baseDir = new File(BASE_URL);

        // create the output directory
        new File(DESTINATION_URL).mkdir();

        System.out.println(baseDir.getAbsolutePath());
        System.out.println(baseDir.getPath());

        if (baseDir.exists() && baseDir.isDirectory()){
            convertFiles(baseDir,DESTINATION_URL);
        }else{
            throw new RuntimeException(baseDir.getAbsolutePath()+" does not exist");
        }

        System.out.println("Total Files Processed : "+total);
        System.out.println("Total Files Created : "+fileCreated);
        System.out.println("Total Posts Processed : "+totalPosts);
        System.out.println("Empty Posts : " + emptyFiles);
        System.out.println();
        System.out.println("Total Words : "+totalWords);
        System.out.println("Total Unique Words : "+uniqueWords.size());

    }


    public static void convertFiles(File file, String outputBaseDir) throws IOException, XMLStreamException {

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
                processFile(fileToProcess, outputBaseDir);
            }
        }

    }

    // parse the xml file extract the content and create a corresponding text file in the output destination.
    public static void processFile(File file, String outputBaseDir) throws IOException{
        total++;

        String rootFileName = file.getName();
        String newFileName = rootFileName.substring(0,rootFileName.length()-4);

        String outputDir = outputBaseDir+File.separator+newFileName;
        int postCount = 0;

        // read the xml file content
        String fileContent = IOUtils.toString(new InputStreamReader(new FileInputStream(file)));

        // convert to an OM element
        OMElement root = null;
        try {
            root = AXIOMUtil.stringToOM(fileContent);
        } catch (XMLStreamException e) {
            System.err.print(file.getAbsolutePath());
        }

        // get all posts
        Iterator<OMElement> posts = root.getChildrenWithLocalName(DOCUMENT_POST);


        StringBuilder contentBuilder = new StringBuilder();
        // iterate through the posts and get the contents
        int currentFileWordCount = 0;
        while (posts.hasNext()){
            totalPosts++;

            OMElement postElement = posts.next();
            // get the content of the post
            OMElement contentElement = (OMElement)postElement.getChildrenWithLocalName(POST_CONTENT).next();
            String content = contentElement.getText();


            if(!content.isEmpty()) {
                postCount++;
                String postWords[] = content.trim().split("\\s+");
                int postWordCount = postWords.length;
                totalWords += postWordCount;
                currentFileWordCount += postWordCount;
                contentBuilder.append(content).append("\n\n");

                Collections.addAll(uniqueWords, postWords);

                // if the number of totalWords exceeds the minimum word per file
                // then print and restart

                if( !posts.hasNext() || currentFileWordCount>MINIMUM_WORDS_PER_FILE){
                    if (currentFileWordCount > 100){
                        writeFile(outputDir + "_" + postCount + ".txt", contentBuilder.toString());
                    }

                    currentFileWordCount = 0;
                    contentBuilder.setLength(0);
                }


            } else {
                emptyFiles++;
                System.out.println("Empty Content : "+newFileName);
            }
        }


    }


    public static void writeFile(String path, String content) throws IOException {

        File fileToWrite = new File(path);
        FileWriter fileWriter = new FileWriter(fileToWrite);
        // write the content on to a file
        fileWriter.write(content);
        // flush and close the writer
        fileWriter.flush();
        fileWriter.close();

        fileCreated++;
        System.out.println(fileCreated+"\t"+path+"\t\ttotalWords: "+content.split("\\s+").length);
    }


    private static int getWordCount(StringBuilder builder){
        return builder.toString().split("\\s+").length;
    }

}
