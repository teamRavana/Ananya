package ananya.utils.corpus.convertor;

import java.io.FileWriter;
import java.io.IOException;

public abstract class AbstractConverter {


    public static final String B_NE = "B-NE";
    public static final String I_NE = "I-NE";
    public static final String OTHER = "O";
    public static final String NE = "NE";

    public static final String BRAT_PERSON_ENTITY = "Person";
    public static final String BRAT_LOCATION_ENTITY = "Location";
    public static final String BRAT_ORGANIZATION_ENTITY = "Organization";


    public static final String STANFORD_PERSON_ENTITY = "PER";
    public static final String STANFORD_LOCATION_ENTITY = "LOC";
    public static final String STANFORD_ORGANIZATION_ENTITY = "ORG";
    public static final String STANFORD_OTHER_ENTITY = "O";




    /**
     *  Utils method to write string content to a file
     * @param filePath
     * @param content
     */
    protected static void writeFile(String filePath, String content){

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

        public int getBegin() {
            return begin;
        }

        public int getEnd() {
            return end;
        }

        public String getText() {
            return text;
        }
    }
}
