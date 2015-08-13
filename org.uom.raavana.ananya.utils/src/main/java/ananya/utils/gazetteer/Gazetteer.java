package ananya.utils.gazetteer;

import ananya.utils.gazetteer.handlers.DefaultGazetteerHandler;
import ananya.utils.gazetteer.handlers.GazetteerLineHandler;
import corpus.sinhala.SinhalaTokenizer;
import corpus.sinhala.SinhalaVowelLetterFixer;

import java.io.*;
import java.util.*;

public class Gazetteer {

    private static Gazetteer instance;

    private static final String NE_PERSON = "Person";
    private static final String NE_LOCATION = "Location";
    private static final String NE_ORGANIZATION = "Organization";
    private static final String NE_OTHER = "Other";


    private Map<String,Integer> personFreqMap = new HashMap<>();
    private Map<String,Integer> locationFreqMap = new HashMap<>();
    private Map<String,Integer> organizationFreqMap = new HashMap<>();

    private Set<String> personNamesSet;
    private Set<String> locationNamesSet;
    private Set<String> organizationNamedSet;

    private final String[] shortForms = new String[]{"ඒ", "බී", "සී", "ඩී", "ඊ", "එෆ්", "ජී", "එච්", "අයි", "ජේ", "කේ", "එල්", "එම්", "එන්", "ඕ", "පී", "කිව්", "ආර්", "එස්", "ටී", "යූ", "ඩබ්", "ඩබ්ලිව්", "එක්ස්", "වයි", "ඉසෙඩ්", "පෙ", "ව", "ප", "රු", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private final List<String> shortFormsList = Arrays.asList(shortForms);

    // mapping between the file and the gazetter line handler
    private Map<String,Class> fileHandlerMap = new HashMap<>();

    SinhalaVowelLetterFixer vowelFixer;
    SinhalaTokenizer tokenizer;

    protected Gazetteer() {

        personNamesSet = new HashSet<>();
        locationNamesSet = new HashSet<>();
        organizationNamedSet = new HashSet<>();

        vowelFixer = new SinhalaVowelLetterFixer();
        tokenizer = new SinhalaTokenizer();
    }

    /**
     *
     * @param rootDirPath path to the root directory where the gazetteer files are present
     * @return
     */
    public static Gazetteer getInstance(String rootDirPath) throws IllegalAccessException, InstantiationException {

        if (instance == null) {
            synchronized (Gazetteer.class) {
                if (instance == null) {
                    instance = new Gazetteer();
                    instance.init(rootDirPath);
                }
            }
        }
     //   instance.init(rootDirPath);
        return instance;
    }

    /**
     * Initialize the Gazetteer
     */
    private void init(String rootDirPath) throws InstantiationException, IllegalAccessException {

        File rootDir = new File(rootDirPath);

        if (!rootDir.exists()){
            System.err.println("Root Gazetteer Directory does not exist : "+rootDirPath);
            throw new RuntimeException("Unable to find the gazetteer root directory " + rootDir.getAbsolutePath());
        }

        System.out.println("Loading files from : "+rootDirPath);

        for (File gazetteer : rootDir.listFiles()) {
            // for now we only deal with files placed in the root directory and do not go into sub directories
            if (gazetteer.isFile()) {
                processGazetteerFile(gazetteer);
            }
        }

        System.out.println("Files Processed : "+rootDir.listFiles().length); System.out.println("Person Names : "+personNamesSet.size());
        System.out.println("Location Names : " + locationNamesSet.size());
        System.out.println("Organization Names : "+organizationNamedSet.size());

    }


    /**
     *
     * @param word
     * @return
     */
    public String findNamedEntityTag(String word) {

        String tag = null;
        String fixedText = vowelFixer.fixText(word,true);


        int freq[] = {
                personFreqMap.containsKey(word) ? personFreqMap.get(word) : 0,
                locationFreqMap.containsKey(word) ? locationFreqMap.get(word) : 0,
                organizationFreqMap.containsKey(word) ? organizationFreqMap.get(word) : 0
        };

        // if all three frequencies are not empty it belongs to one of the three classes
        if (!(freq[0] == 0 && freq[1] == 0 && freq[2] == 0)){
            int largeIndex = freq[0] < freq[1] ? 1 : 0;
            largeIndex = freq[largeIndex] < freq[2] ? 2 : largeIndex;

            if (largeIndex == 0){
                tag = NE_PERSON;
            }else if (largeIndex == 1){
                tag = NE_LOCATION;
            }else if (largeIndex == 2){
                tag = NE_ORGANIZATION;
            }
        }

        return tag;
    }


    private void processGazetteerFile(File file) throws IllegalAccessException, InstantiationException {
        BufferedReader bufferedReader = null;
        Set<String> entitySet;

        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String[] firstLine = bufferedReader.readLine().trim().split(":");


            // we only process if the first line contains the NE type
            String namedEntityType = firstLine[0].trim();
            entitySet = getMapType(namedEntityType);
            GazetteerLineHandler handler;

           if (firstLine.length == 2){
                handler = (GazetteerLineHandler)Class.forName(firstLine[1].trim()).newInstance();
           }else{
               handler = new DefaultGazetteerHandler();
           }

            if (entitySet != null) {
                String line;
                // read the file line by line populate the gazetteer
                while ((line = bufferedReader.readLine()) != null) {
                  //  System.out.println(line);
                    List<String> wordsList = handler.handleGazetteerLine(line);
                    for (String word : wordsList){
                        if (!entitySet.contains(word)){
                            entitySet.add(word);
                        }

                    updateFrequency(namedEntityType,word);
                    }
                }
            }

            System.out.println(file.getAbsolutePath() +"  processed");

        } catch (FileNotFoundException e) {
            System.err.println("Exception When trying to process " + file.getName() + " : " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO Error : " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Gazetteer Handler class not found : ",e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    System.err.println("Error when closing bufferedReader : " + e.getMessage());
                }
            }
        }
    }

    // select the correct set to populate based on the named entity type
    private Set<String> getMapType(String namedEntityType) {
        Set<String> set = null;
        if (NE_PERSON.equalsIgnoreCase(namedEntityType)) {
            set = personNamesSet;
        } else if (NE_LOCATION.equalsIgnoreCase(namedEntityType)) {
            set = locationNamesSet;
        } else if (NE_ORGANIZATION.equalsIgnoreCase(namedEntityType)) {
            set = organizationNamedSet;
        }
        return set;
    }


    private void updateFrequency(String type, String word){

        Map<String,Integer> mapToUpdate = null;

        // pick the correct map to update
        if (NE_PERSON.equalsIgnoreCase(type)) {
            mapToUpdate = personFreqMap;
        } else if (NE_LOCATION.equalsIgnoreCase(type)) {
            mapToUpdate = locationFreqMap;
        } else if (NE_ORGANIZATION.equalsIgnoreCase(type)) {
            mapToUpdate = organizationFreqMap;
        }

        if (mapToUpdate != null){
            if (!mapToUpdate.containsKey(word)){
                mapToUpdate.put(word,1);
            }else{
                int newCount = mapToUpdate.get(word)+1;
                mapToUpdate.put(word,newCount);
            }
        }

    }

    /**
     *  Test Main Method
     * @param args
     */
    public static void main(String[] args) {

        // path to the root folder where the gazetteers are present just like input/gazetteer
        String path = "/home/farazath/.ananya/input/gazetteer";

        Gazetteer gazetteer = null;
        try {
            gazetteer = Gazetteer.getInstance(path);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        String tag = gazetteer.findNamedEntityTag("හ");
        System.out.println("Tag for අතපත්තු : "+tag);


        gazetteer.printStatReport();

    }

    private void printStatReport(){

        System.out.println("Gazetteer Files");
        int count = 0;

        for (Map.Entry<String,Integer> entry : personFreqMap.entrySet()){
            count += entry.getValue();
        }

        System.out.println("Names Count : "+count+"\n");

        System.out.println("Gazetter Lists");
        System.out.println("Person Names : "+personNamesSet.size());
        System.out.println("Location Names : " + locationNamesSet.size());
        System.out.println("Organization Names : "+organizationNamedSet.size());
    }
}
