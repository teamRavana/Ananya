package ananya.tools.corpus.convertors;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CRF2BratConvertor extends AbstractConverter{

    // Map to store contents of each line of the CRF train file
    public static Map<Integer,CRFEntry> lineMap = new HashMap<>();
    // Map to store the brat annotations created
    public static Map<Integer, BratAnnotation> bratAnnotationMap = new HashMap<>();


    public static int countNE = 0;
    static int personCount = 0;


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

                if (B_NE.equals(tag) || I_NE.equals(tag) || !STANFORD_OTHER_ENTITY.equalsIgnoreCase(tag)){
                    if (STANFORD_PERSON_ENTITY.equalsIgnoreCase(tag)){
                        tag = BRAT_PERSON_ENTITY;
                        personCount++;
                    }else if (NE.equalsIgnoreCase(tag)){
                        tag = NE;
                    }
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
        writeBratAnnFile(filePath, 1, lineMap.size());
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

       return builder.toString();
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
                    String row[] = line.split("\\s+|_");
                    if (row.length != 2){
                        System.out.println("Error in "+count);
                        break;
                    }

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

        String PATH_TO_CRF_FILE = "/home/farazath/PoSTag/out/NER_train_2.txt";
        String PATH_TO_CORPUS_FILE = "/home/farazath/Ananya/input/tagged-corpus/train_corpus_1.txt";
        String PATH_TO_BRAT_FILE = "/home/farazath/Ananya/input/tagged-corpus/train_corpus_1.ann";


        String OUTPUT_BASE = "/home/farazath/Ananya/input/tagged-corpus-mod";
        String baseCorpusName = "train_corpus_mod";

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
                        4010, 8035, 12030, 16017, 20009,
                        24017, 28016, 32016,36007, 40001,
                        44009, 48028, 52012, 56001, 60011,
                        64012, 68045, 72004, 76019, 80000,
                        84034, 86580
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
        System.out.println("Person Tags : " +personCount);
        System.out.println("Total NE Tags : " + nerTags);
        System.out.println("NER percentage : "+(((nerTags*1.0)/annotations)*100));
    }
}


