package ananya.utils.corpus.convertor;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CRF2BratConvertor {

    public static List<CRFEntry> crfEntryList = new ArrayList<>();
    public static List<BratAnnotation> bratAnnotationList = new ArrayList<>();

    public static final String B_NE = "B-NE";
    public static final String I_NE = "I-NE";
    public static final String OTHER = "O";
    public static final String NE = "NE";



    public static void buildBratAnnotations(){
        if (crfEntryList.isEmpty()){
            System.err.println("No CRF Entries to write as a corpus");
            System.exit(2);
        }
        int tagID = 0;
        int head = 0;
        for (CRFEntry crfEntry : crfEntryList){

            String word = crfEntry.getWord();
            String tag = crfEntry.getTag().trim();

            int tail = head + word.length();

            if (!".".equals(word) || !",".equals(word)){

                if (B_NE.equals(tag) || I_NE.equals(tag)){
                    tag = NE;
                }
                bratAnnotationList.add(
                        new BratAnnotation(
                                "T"+tagID++,
                               tag,
                                head,
                                tail,
                                word
                                )
                );

                tail++;
            }

            head = tail;
        }
    }


    public static void writeBratAnnFile(String filePath){

        buildBratAnnotations();

        StringBuilder builder = new StringBuilder();
        for (BratAnnotation annotation : bratAnnotationList){
            if (!StringUtils.equals(annotation.getNERTag(),OTHER)){
                builder.append(annotation.toString()).append("\n");
            }
        }

        writeFile(filePath,builder.toString());
    }

    /**
     *  Get the corpus text based on the CRF training data
     */
    public static String getCorpusContent(String filePath){
        StringBuilder builder = new StringBuilder();

        if (crfEntryList.isEmpty()){
            System.err.println("No CRF Entries to write as a corpus");
            System.exit(2);
        }

        for (CRFEntry crfEntry : crfEntryList){

            String word = crfEntry.getWord();

            builder.append(word);
            if (!".".equals(word) || !",".equals(word)){
                builder.append(" ");
            }
        }

       return builder.toString();
    }


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
     * Read the CRF training file and build the word list
     * @param crfFile File with each row containing a word/punctuation mark and its corresponsing NE tag
     *                seperated by a tab(/t) character
     *                eg: නගරවල	O
     * @throws IOException
     */
    public static void initConverter(File crfFile) throws IOException {

        if (!crfFile.exists()){
            throw new FileNotFoundException(crfFile.getAbsolutePath()+" not fount");
        }

        BufferedReader reader = new BufferedReader(new FileReader(crfFile));

        int count = 0;
        int countLines = 1;
        String line;
        while((line = reader.readLine()) != null){

            if (StringUtils.isNotEmpty(line) && !StringUtils.equals(line,"\t")){
                String row[] = line.split("\\t");
                String word = row[0];
                String tag = row[1];

                crfEntryList.add(new CRFEntry(word,tag));
                count++;
            }

            countLines++;
        }

        System.out.println("Lines Read : "+countLines);
        System.out.println("CRF Entries Read : "+count);

    }

    public static void main(String[] args) throws IOException {

        String PATH_TO_CRF_FILE = "/home/farazath/Ananya/input/tagged-corpus/train_utf8.txt";
        String PATH_TO_CORPUS_FILE = "/home/farazath/Ananya/input/tagged-corpus/train_corpus.txt";
        String PATH_TO_BRAT_FILE = "/home/farazath/Ananya/input/tagged-corpus/train_corpus.ann";


        initConverter(new File(PATH_TO_CRF_FILE));

        String corpus = getCorpusContent(PATH_TO_CORPUS_FILE);
        writeFile(PATH_TO_CORPUS_FILE, corpus);

        writeBratAnnFile(PATH_TO_BRAT_FILE);

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


