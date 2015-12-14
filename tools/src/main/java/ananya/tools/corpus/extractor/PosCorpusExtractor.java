package ananya.tools.corpus.extractor;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.IOUtils;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Iterator;

public class PosCorpusExtractor extends CorpusTextExtractor{

    private static String inputSrc = "/home/farazath/Desktop/FYP/UCSC Sinhala Tagged Corpus V1/UCSC Sinhala Tagged Corpus V1";
    private static String outputFileName = "output/sinhalaPosTrain.txt";
    private static final String ART = "ART";

    static int totalArticles = 0;
    static int totalWords = 0;

    public static String processFileContent(File file) throws IOException{

        String newFileName = outputFileName;
        int articleCountPerFile = 0;
        int wordCountPerFile = 0;

        // read the xml file content
        String fileContent = IOUtils.toString(new InputStreamReader(new FileInputStream(file)));

        fileContent = "<root>" + fileContent +"</root>";
   //     System.out.println(fileContent);
        // convert to an OM element
        OMElement root = null;
        try {
            root = AXIOMUtil.stringToOM(fileContent);
        } catch (XMLStreamException e) {
            System.err.print(file.getAbsolutePath());
        }catch (Exception ex){
            System.err.println(file.getName());
        }

        // get all posts
        Iterator<OMElement> posts = root.getChildrenWithLocalName(ART);


        StringBuilder contentBuilder = new StringBuilder();
        // iterate through the posts and get the contents
        while (posts.hasNext()){
            articleCountPerFile++;

            OMElement postElement = posts.next();
            String content = postElement.getText();
            wordCountPerFile += content.trim().split("\\s+").length;

            if(!content.isEmpty()) {
                contentBuilder.append(content).append("\n");

            } else {
                System.out.println("Empty Content : "+newFileName);
            }
        }

        totalArticles += articleCountPerFile;
        totalWords += wordCountPerFile;
        System.out.println(file.getName()+"\ttotalArticles: "+ articleCountPerFile +"\ttotalWords: "+ wordCountPerFile);
        return contentBuilder.toString();
    }


    public static void main(String[] args) throws IOException {

        File inputDir = new File(inputSrc);

        StringBuilder contentBuilder = new StringBuilder();
        int count = 0;
        for (File file : inputDir.listFiles()){
            try {
                contentBuilder.append(processFileContent(file));
            } catch (IOException e) {
                System.out.println("exception when handling : "+file.getName());
                System.exit(2);
            } catch (Exception ex){
                System.out.println(ex.getMessage()+"\t"+file.getName());
            }
            contentBuilder.append("\n");
            count++;

        }

        System.out.println(count);
        System.out.println("Posts "+ totalArticles);
        System.out.println("Words "+ totalWords);
        writeFile(outputFileName,contentBuilder.toString());
    }




}
