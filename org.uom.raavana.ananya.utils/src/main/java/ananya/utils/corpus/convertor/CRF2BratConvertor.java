package ananya.utils.corpus.convertor;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CRF2BratConvertor {

    // Map to store contents of each line of the CRF train file
    public static Map<Integer,CRFEntry> lineMap = new HashMap<>();
    // Map to store the brat annotations created
    public static Map<Integer, BratAnnotation> bratAnnotationMap = new HashMap<>();


    public static final String B_NE = "B-NE";
    public static final String I_NE = "I-NE";
    public static final String OTHER = "O";
    public static final String NE = "NE";

    public static int countNE = 0;


    /**
     *  Create the brat annotatation Map for the specified portion of the CRF file
     * @param startLine
     * @param endLine
     */
    private static void buildBratAnnotations(int startLine, int endLine){

        if (lineMap.isEmpty()){
            System.err.println("No CRF Entries to write as a corpus");
            System.exit(2);
        }

        int tagID = 0;
        int head = 0;

        for (int i=startLine; i<=endLine; i++){

            CRFEntry crfEntry = lineMap.get(i);

            if (crfEntry == null){
                continue;
            }

            String word = crfEntry.getWord();
            String tag = crfEntry.getTag().trim();

            int tail = head + word.length();

            if (!".".equals(word) || !",".equals(word)){

                if (B_NE.equals(tag) || I_NE.equals(tag)){
                    tag = NE;
                    countNE++;
                }
                bratAnnotationMap.put(
                        i, new BratAnnotation("T"+tagID++, tag, head, tail, word)
                );

                tail++;
            }
            head = tail;
        }


   }


    public static void writeBratAnnFile(String filePath){
        writeBratAnnFile(filePath,1,lineMap.size());
    }

    /**
     * Create a brat annotation for the portion specified in the CRF train File
     * @param filePath File Path of the CRF train file
     * @param startLine line to start considering to convert to brat annotations
     * @param endLine line to stop
     */
    public static void writeBratAnnFile(String filePath,int startLine, int endLine){

        buildBratAnnotations(startLine,endLine);

        StringBuilder builder = new StringBuilder();

        for (int i=startLine ; i<=endLine; i++){
            BratAnnotation annotation = bratAnnotationMap.containsKey(i) ? bratAnnotationMap.get(i) : null;

            if (annotation == null){
                continue;
            }

            // if the annotation is not OTHER type add entry to .ann file
            if (!StringUtils.equals(annotation.getNERTag(),OTHER)){
                builder.append(annotation.toString()).append("\n");
            }
        }

        writeFile(filePath,builder.toString());
    }


    public static String getCorpusContent(){
        return getCorpusContent(1,lineMap.size());
    }

    /**
     *  Get the corpus text based on the CRF training data
     */
    public static String getCorpusContent(int startLine, int endLine){
        StringBuilder builder = new StringBuilder();

        if (lineMap.isEmpty()){
            System.err.println("No CRF Entries to write as a corpus");
            System.exit(2);
        }

        for (int i=startLine;i<=endLine;i++){
           CRFEntry crfEntry = lineMap.get(i);

            if (crfEntry == null){
                continue;
            }

            String word = crfEntry.getWord();

            if (word != null){
                builder.append(word);
                if (!".".equals(word) || !",".equals(word)){
                    builder.append(" ");
                }
            }

        }

//        for (CRFEntry crfEntry : crfEntryList){
//
//            String word = crfEntry != null ? crfEntry.getWord() : null;
//
//            if (word != null){
//                builder.append(word);
//                if (!".".equals(word) || !",".equals(word)){
//                    builder.append(" ");
//                }
//            }
//        }

       return builder.toString();
    }


    /**
     *  Utils method to write string content to a file
     * @param filePath
     * @param content
     */
    private static void writeFile(String filePath, String content){

        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(content);
            writer.flush();
            writer.close();
            System.out.println("Corpus Content Written to "+filePath);
        } catch (IOException e) {
            System.err.println("Exception When writing the corpus file "+e.getMessage());
        }
    }

    /**
     * Initializes the COnvertor by reading the CRF train file line by line
     *
     * @param crfFile
     * @throws IOException
     */
    public static void initConverter(File crfFile) throws IOException {
        if (!crfFile.exists()){
            throw new FileNotFoundException(crfFile.getAbsolutePath()+" not fount");
        }

        BufferedReader reader = new BufferedReader(new FileReader(crfFile));

        int count = 0;
        int readLines = 1;
        String line;
        while((line = reader.readLine()) != null){

                if (StringUtils.isNotEmpty(line) && !StringUtils.equals(line,"\t")){
                    String row[] = line.split("\\t");
                    String word = row[0];
                    String tag = row[1];

                    CRFEntry crfEntry = new CRFEntry(word,tag);
                    lineMap.put(readLines, crfEntry);
                  //  crfEntryList.add(crfEntry);
                    count++;
                }else{
                    lineMap.put(readLines, null);
                   // crfEntryList.add(null);
                }

                readLines++;
        }

        System.out.println("Lines Read : "+readLines);
        System.out.println("CRF Entries Read : "+count);
    }

    public static void main(String[] args) throws IOException {

        String PATH_TO_CRF_FILE = "/home/farazath/Ananya/input/tagged-corpus/train_utf8.txt";
        String PATH_TO_CORPUS_FILE = "/home/farazath/Ananya/input/tagged-corpus/train_corpus_1.txt";
        String PATH_TO_BRAT_FILE = "/home/farazath/Ananya/input/tagged-corpus/train_corpus_1.ann";


        String OUTPUT_BASE = "/home/farazath/Ananya/input/tagged-corpus/parts";
        String baseCorpusName = "train_corpus";

        /** WHOLE CRF FILE CONVERT
        // init the convertor
        initConverter(new File(PATH_TO_CRF_FILE));
        // get the whole corpus from crf file
        String corpus = getCorpusContent();
        // write corpus to a file
        writeFile(PATH_TO_CORPUS_FILE, corpus);
        // generate brat annotations and write to a file
        writeBratAnnFile(PATH_TO_BRAT_FILE);

         **/

        // init convertor
        initConverter(new File(PATH_TO_CRF_FILE));

        // line numbers to limit each output file to
        // eg: first file --> line 1 to line 4022 etc.
        int breaks[] =
                {
                        4022, 8024, 12026, 16019, 20005,
                        24027, 28029, 32012,36000, 40009,
                        44018, 48002, 52009, 56003, 60009,
                        64004, 68035, 71998, 75993, 77513
                };

        int count = 1;
        int start = 1;

        for (int end : breaks){
              String corpusFileName = OUTPUT_BASE+"/"+baseCorpusName+"_"+ (count) +".txt";
              String bratFileName =   OUTPUT_BASE+"/"+baseCorpusName+"_"+count + ".ann";

              writeFile(corpusFileName, getCorpusContent(start,end));
              writeBratAnnFile(bratFileName, start, end);

              start = end + 1;
              count++;
        }

        int annotations = bratAnnotationMap.size();
        int nerTags = countNE;

        System.out.println("Total Lines : "+lineMap.size());
        System.out.println("Total Annotations : "+annotations);
        System.out.println("Total NE Tags : " + nerTags);
        System.out.println("NER percentage : "+(((nerTags*1.0)/annotations)*100));
    }


    public static class CRFEntry{
        private String word;
        private String tag;

        public CRFEntry(String word, String tag) {
            this.word = word;
            this.tag = tag;
        }

        public String getWord() {
            return word;
        }

        public String getTag() {
            return tag;
        }
    }

    public static class BratAnnotation{
        private String tagID;
        private String NERTag;
        private int begin;
        private int end;
        private String text;

        public BratAnnotation(String tagID, String NERTag, int begin, int end, String text) {
            this.tagID = tagID;
            this.NERTag = NERTag;
            this.begin = begin;
            this.end = end;
            this.text = text;
        }

        public String getNERTag() {
            return NERTag;
        }

        @Override
        public String toString() {
            String bratLine = new StringBuilder()
                    .append(tagID).append("\t")
                    .append(NERTag).append(" ")
                    .append(begin).append(" ")
                    .append(end).append("\t")
                    .append(text)
                    .toString();

            return bratLine;
        }
    }
}


