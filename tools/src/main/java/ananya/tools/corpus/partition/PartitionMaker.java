package ananya.tools.corpus.partition;

import ananya.tools.utils.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PartitionMaker {



    public static void createPartitions(File input, int numberOfPartitions) throws IOException {
        int numOfLines;

        // check whether input file/folder exists
        if (!input.exists()){
            throw  new RuntimeException("Input "+input.getAbsolutePath()+" does not exist");
        }

        File inputFile;
        // if the input is a directory create a single file from all the files in the directory
        if (input.isDirectory()){
                inputFile = new File("temp");
                FileUtils.mergeFiles(input.listFiles(),inputFile);
        }else{
            inputFile = input;
        }

        numOfLines = FileUtils.countLines(inputFile);
        int interval = numOfLines/numberOfPartitions;

        BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));

        String line;

        while ( (line = bufferedReader.readLine()) != null){

        }

        System.out.println(numOfLines);

    }


    public static void main(String[] args) throws IOException {

        File inputFile = new File("/home/farazath/Ananya-NER-Tools/output/train_21_Nov.tsv");
        createPartitions(inputFile,10);


    }
}
