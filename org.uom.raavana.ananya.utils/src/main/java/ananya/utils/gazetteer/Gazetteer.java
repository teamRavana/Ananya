package ananya.utils.gazetteer;

import corpus.sinhala.SinhalaVowelLetterFixer;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Gazetteer {

    private static Gazetteer instance;

    // hardcoded path to test
    private static final String DEFAULT_INPUT_PATH = "/home/farazath/Ananya/input/gazetteer";

    private static final String NE_PERSON = "Person";
    private static final String NE_LOCATION = "Location";
    private static final String NE_ORGANIZATION = "Organization";
    private static final String NE_OTHER = "Other";



    private Map<String, String> personNamesMap;
    private Map<String, String> locationNamesMap;
    private Map<String, String> organizationNamedMap;

    SinhalaVowelLetterFixer vowelFixer;

    protected Gazetteer() {

        personNamesMap = new HashMap<>();
        locationNamesMap = new HashMap<>();
        organizationNamedMap = new HashMap<>();

        vowelFixer = new SinhalaVowelLetterFixer();
    }

    /**
     *
     * @param rootDirPath path to the root directory where the gazetteer files are present
     * @return
     */
    public static Gazetteer getInstance(String rootDirPath) {

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
    private void init(String rootDirPath) {

        File rootDir = new File(rootDirPath);

        if (!rootDir.exists()){
            System.err.println("Root Gazetteer Directory does not exist : "+rootDirPath);
            throw new RuntimeException("Unable to find the gazetteer root directory " + rootDir.getAbsolutePath());
        }


        for (File gazetteer : rootDir.listFiles()) {
            // for now we only deal with files placed in the root directory and do not go into sub directories
            if (gazetteer.isFile()) {
                processGazetteerFile(gazetteer);
            }
        }

    }


    /**
     *
     * @param word
     * @return
     */
    public String findNamedEntityTag(String word) {

        String tag = null;
        String fixedText = vowelFixer.fixText(word,true);

        if (personNamesMap.containsKey(fixedText)){
            tag = NE_PERSON;
        }else if (locationNamesMap.containsKey(fixedText)){
            tag = NE_LOCATION;
        }else if (organizationNamedMap.containsKey(fixedText)){
            tag = NE_ORGANIZATION;
        }

        return tag;
    }


    private void processGazetteerFile(File file) {
        BufferedReader bufferedReader = null;
        Map<String, String> mapToUse;

        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String namedEntityType = bufferedReader.readLine().trim();

            // we only process if the first line contains the NE type
            mapToUse = getMapType(namedEntityType);
            if (mapToUse != null) {

                String line = null;
                // read the file line by line populate the gazetteer
                while ((line = bufferedReader.readLine()) != null) {
                    line = line.trim();
                    if (StringUtils.isNotEmpty(line)) {
                        String text = vowelFixer.fixText(line, true);
                        if (!mapToUse.containsValue(text)){
                            mapToUse.put(text,namedEntityType);
                        }
                    }
                }
            }

            System.out.println(file.getName() +"  processed");

        } catch (FileNotFoundException e) {
            System.err.println("Exception When trying to process " + file.getName() + " : " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO Error : " + e.getMessage());
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


    private Map<String, String> getMapType(String namedEntityType) {
        Map<String, String> map = null;

        if (NE_PERSON.equalsIgnoreCase(namedEntityType)) {
            map = personNamesMap;
        } else if (NE_LOCATION.equalsIgnoreCase(namedEntityType)) {
            map = locationNamesMap;
        } else if (NE_ORGANIZATION.equalsIgnoreCase(namedEntityType)) {
            map = organizationNamedMap;
        }

        return map;
    }

    /**
     *  Test Main Method
     * @param args
     */
    public static void main(String[] args) {

        // path to the root folder where the gazetteers are present just like input/gazetteer
        String path = "";

        Gazetteer gazetteer = Gazetteer.getInstance(path);
        String tag = gazetteer.findNamedEntityTag("අතපත්තු");
        System.out.println("Tag for අතපත්තු : "+tag);

    }
}
