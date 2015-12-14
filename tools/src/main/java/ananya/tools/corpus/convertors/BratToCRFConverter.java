package ananya.tools.corpus.convertors;



import ananya.tools.Constants;
import ananya.tools.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BratToCRFConverter {

    // brat annotation map key --> start index , value --> bratAnnotation starting from the index
    private static Map<Integer, BratAnnotation> bratAnnotationMap = new HashMap<>();

    // map to maintain tag mapping between brat to CRF key--> bratTag value --> crfTag
    private static Map<String, String> annotationMapping = new HashMap<>();

    private static List<String> abbreviations = new ArrayList<>();
    private static List<String> endingLetters = new ArrayList<>();


    static {
        annotationMapping.put(Constants.BRAT_PERSON_ENTITY, Constants.STANFORD_PERSON_ENTITY);
        annotationMapping.put(Constants.BRAT_LOCATION_ENTITY, Constants.STANFORD_LOCATION_ENTITY);
        annotationMapping.put(Constants.BRAT_ORGANIZATION_ENTITY, Constants.STANFORD_ORGANIZATION_ENTITY);
        annotationMapping.put(Constants.BRAT_NE, Constants.STANFORD_NE);

        Collections.addAll(abbreviations, Constants.shortForms);
        Collections.addAll(abbreviations, Constants.sinhalaVowels);
        Collections.addAll(endingLetters, Constants.sentenceEndingLetters);
    }


    private static void buildBratAnnotationMap(File bratFile) throws IOException {

        // check whether the .ann file exists
        if (bratFile == null || !bratFile.exists()) {
            System.err.println("Brat Annotation File does not exist : " + bratFile.getAbsolutePath());
        } else {
            BufferedReader reader = new BufferedReader(new FileReader(bratFile));

            String line;
            while ((line = reader.readLine()) != null) {
                List<BratAnnotation> annotations = createBratAnnotation(line);
                for (BratAnnotation annotation : annotations) {
                    bratAnnotationMap.put(annotation.getBegin(), annotation);
                }
            }

        }

    }

    /**
     * Create brat annotation objects from a line of the .ann files
     * more than one annotation object may be produced if the .ann line is tagged for multiple words
     *
     * @param bratLine
     * @return
     */
    private static List<BratAnnotation> createBratAnnotation(String bratLine) {
        List<BratAnnotation> annotationList = new ArrayList<>();

        String bratLineArgs[] = bratLine.trim().split("\\s+");

        String tagId = bratLineArgs[0].trim();
        int start = Integer.parseInt(bratLineArgs[2]);
        int end = Integer.parseInt(bratLineArgs[3]);

        String tag = bratLineArgs[1];
        String words[] = Arrays.copyOfRange(bratLineArgs, 4, bratLineArgs.length);

        int count = 0;
        for (String word : words) {
            int endIndex = start + word.trim().length();
            BratAnnotation annotation = new BratAnnotation(tagId + "." + count, tag, start, endIndex, word.trim());

            annotationList.add(annotation);
            start = endIndex + 1;
            count++;
        }

        return annotationList;
    }

    private static String createCRFLine(String wordToTag, int startIndex, boolean useBratAnnotationMap) {

        StringBuilder builder = new StringBuilder();
        builder.append(wordToTag).append("\t");

        // annotation file was present so we proceeding with the map created using the file
        if (useBratAnnotationMap) {
            BratAnnotation annotation = bratAnnotationMap.get(startIndex);

            if (annotation != null) {
                String nerTag = annotation.getNERTag().trim();
                // get the mapped tag for CRF
                builder.append(annotationMapping.get(nerTag));
            } else {
                // since no annotation object was in the map it wasn't tagged a named entity
                builder.append(Constants.STANFORD_OTHER_ENTITY);
            }
        } else {
            builder.append(Constants.STANFORD_OTHER_ENTITY);
        }

        return builder.toString();
    }


    /**
     * Method to create a CRF train file from a brat annotation file and a raw text file
     *
     * @param corpusFile raw corpus text file
     * @param bratFile   .ann file
     * @return converted train file as a string
     * @throws IOException
     */
    public static String convertToCRF(File corpusFile, File bratFile) throws IOException {
        boolean annotationFilePresent = true;

        // check whether the corpus text file exists
        if (!corpusFile.exists()) {
            throw new RuntimeException("Corpus Content File does not exist : " + corpusFile.getAbsolutePath());
        }

        // check whether the annotation file exists, if it doesn't we simply tag everything as 'O'
        if (bratFile == null || !bratFile.exists()) {
            annotationFilePresent = false;
            System.err.println("Brat Annotation File does not exist : " + bratFile.getAbsolutePath());
            System.out.println("Building the train file without an annotation file");
        }

        // build up the brat annotations map
        buildBratAnnotationMap(bratFile);


        BufferedReader bufferedReader = new BufferedReader(new FileReader(corpusFile));

        int start = 0;
        int count = -1;
        int r;
        StringBuilder builder = new StringBuilder(0);
        StringBuilder crfStringBuilder = new StringBuilder();
        String punctuations = ",.'\"";

        while ((r = bufferedReader.read()) != -1) {
            char ch = (char) r;
            count++;

            if (" ".equals(ch + "")) {
                if (builder.length() != 0) {
                    String crfLine = createCRFLine(builder.toString(), start, annotationFilePresent);
                    crfStringBuilder.append(crfLine).append("\n");

                    System.out.println(crfLine);
                    builder.setLength(0);
                }
                start = count + 1;
                continue;
            } else if (punctuations.contains(ch + "")) {
                if (builder.length() != 0) {
                    // check whether this could be an abbreviation of not set to zero
                    boolean abbreviation = abbreviations.contains(builder.toString());
                    boolean singleLetterUnCommon = (builder.length() == 1) && !endingLetters.contains(builder.toString());

                    if (!abbreviation || singleLetterUnCommon) {
                        System.out.println(builder.toString().trim());
                        builder.setLength(0);
                    }

                }
                start = count + 1;
            }
            builder.append(ch + "");
        }

        bratAnnotationMap.clear();
        return crfStringBuilder.toString();
    }


    public static void main(String[] args) {

        File textFile = new File("/home/farazath/Ananya-NER-Tools/input/train_corpus_mod_1.txt");
        File bratAnnFile = new File("/home/farazath/Ananya-NER-Tools/input/train_corpus_mod_1.ann");
        File outputFile = new File("/home/farazath/Ananya-NER-Tools/output/train_mod_1.tsv");


        try {
            String content = convertToCRF(textFile, bratAnnFile);
            FileUtils.writeFile(outputFile, content);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }


    public static class BratAnnotation {
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

    public static class CRFEntry {
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

}

