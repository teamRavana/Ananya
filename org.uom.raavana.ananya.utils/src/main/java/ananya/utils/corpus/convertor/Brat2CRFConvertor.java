package ananya.utils.corpus.convertor;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;

public class Brat2CRFConvertor extends AbstractConverter{

    // brat annotation map key --> start index , value --> bratAnnotation starting from the index
    private  static Map<Integer,BratAnnotation> bratAnnotationMap = new HashMap<>();


    private static void buildBratAnnotationMap(File bratFile) throws IOException {
        if (bratFile == null || !bratFile.exists()){
            System.err.println("Brat Annotation File does not exist : "+bratFile.getAbsolutePath());
        }else{

            BufferedReader reader = new BufferedReader(new FileReader(bratFile));

            String line = null;

            while ( (line = reader.readLine()) != null){

                List<BratAnnotation> annotations = getBratAnnotations(line);
                for (BratAnnotation annotation : annotations){
                    bratAnnotationMap.put(annotation.getBegin(), annotation);
                }
            }

        }

    }


    private static List<BratAnnotation> getBratAnnotations(String bratLine){
        List<BratAnnotation> annotationList = new ArrayList<>();

        String bratLineArgs[] = bratLine.trim().split("\\s+");

        String tagId = bratLineArgs[0].trim();
        int start = Integer.parseInt(bratLineArgs[2]);
        int end = Integer.parseInt(bratLineArgs[3]);

        String tag = bratLineArgs[1];
        String words[] = Arrays.copyOfRange(bratLineArgs,4,bratLineArgs.length);

        int count = 0;
        for (String word : words){
            int endIndex = start + word.trim().length();
            BratAnnotation annotation = new BratAnnotation(tagId+"."+count,tag,start,endIndex, word.trim());

            annotationList.add(annotation);
            start = endIndex + 1;
            count++;
        }


        return annotationList;
    }


    public static void convertToCRF(File corpusFile, File bratFile) throws IOException {
        boolean annotationFilePresent = true;

        if (!corpusFile.exists()){
            throw new RuntimeException("Corpus Content File does not exist : "+corpusFile.getAbsolutePath());
        }

        if (bratFile == null || !bratFile.exists()){
            annotationFilePresent = false;
            System.err.println("Brat Annotation File does not exist : "+bratFile.getAbsolutePath());
            System.out.println("Building the train file without an annotation file");
        }

        BufferedReader bufferedReader = new BufferedReader(new FileReader(corpusFile));

        int start = 0;
        int count = -1;
        int r;
        StringBuilder builder = new StringBuilder(0);
        String punctutations = ",.'\"";

        while ((r = bufferedReader.read()) != -1) {
            char ch = (char) r;
            count++;

            if (" ".equals(ch+"") ){
                if (builder.length() != 0){
                    System.out.println(getCRFLine(builder.toString(),start,annotationFilePresent));
                    builder.setLength(0);
                }
                start = count+1;
                continue;
            }else if (punctutations.contains(ch+"")){
                if (builder.length() != 0){
                    System.out.println(builder.toString().trim());
                    builder.setLength(0);
                }
                start = count+1;
            }
            builder.append(ch+"");
        }

    }


    private static String getCRFLine(String wordToTag,int startIndex, boolean useBratAnnotationMap){

        StringBuilder builder = new StringBuilder();
        builder.append(wordToTag).append("\t");

        if (useBratAnnotationMap){
            BratAnnotation annotation = bratAnnotationMap.get(startIndex);
            if ( annotation != null){
                String nerTag = annotation.getNERTag();

                if (StringUtils.equals(BRAT_PERSON_ENTITY,nerTag.trim())){
                    builder.append(STANFORD_PERSON_ENTITY);
                }else if (StringUtils.equals(BRAT_LOCATION_ENTITY,nerTag.trim())){
                    builder.append(STANFORD_LOCATION_ENTITY);
                }else if (StringUtils.equals(BRAT_ORGANIZATION_ENTITY, nerTag.trim())){
                    builder.append(STANFORD_ORGANIZATION_ENTITY);
                }else{
                    builder.append(STANFORD_OTHER_ENTITY);
                }
            }else{
                builder.append(STANFORD_OTHER_ENTITY);
            }
        }else{
            builder.append(STANFORD_OTHER_ENTITY);
        }

        return builder.toString();
    }


    public static void main(String[] args) throws IOException {
        buildBratAnnotationMap(new File("/home/farazath/Ananya/input/tagged-corpus/parts/test.ann"));
        convertToCRF(new File("/home/farazath/Ananya/input/tagged-corpus/parts/train_corpus_1.txt"), new File("sdasd"));
    }

}
