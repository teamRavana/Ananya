package ananya.tools.corpus.partition;

import ananya.tools.utils.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PartitionMaker {

    private List<File> paritionedFiles = new ArrayList<>();

    /**
     * Method to create file partitions to do cross validation
     *
     * @param input              Input File / Input directory containing the test and train data
     * @param numberOfPartitions number of partitions to create
     * @throws IOException
     */
    private void createPartitions(File input, int numberOfPartitions) throws IOException {

        // check whether input file/folder exists
        if (!input.exists()) {
            throw new RuntimeException("Input " + input.getAbsolutePath() + " does not exist");
        }

        File inputFile;
        // if the input is a directory create a single file from all the files in the directory
        if (input.isDirectory()) {
            inputFile = new File("temp");
            FileUtils.mergeFiles(input.listFiles(), inputFile);
        } else {
            inputFile = input;
        }

        int totalNumberOfLines = FileUtils.countLines(inputFile);
        int interval = totalNumberOfLines / numberOfPartitions; // average lines in a single partition

        // Partition the files
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));

        int lineCount = 0;
        int intervalCount = interval;
        StringBuilder currentFileBuilder = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            lineCount++;

            if (intervalCount != 0){
                currentFileBuilder.append(line.trim()).append("\n");
                intervalCount--;

                // this means we have appended the last line for the current file
                if (intervalCount == 0) {
                    // check the last line we appended is a full stop if not need to go for another line
                    if (!StringUtils.equals(".",line.trim())){
                        intervalCount++;
                    }
                }

            }else {
                // TODO create a file from the current builder and push it to a list/array

                // clear the currentFile string and reset the intervalCount
                currentFileBuilder.setLength(0);
                intervalCount = interval;
            }

            // this means we have reached the end of the file, so create the last file
            if (lineCount == totalNumberOfLines){
                // check the builder size to assure that the last file is not already created
                if (currentFileBuilder.length() != 0){
                    // TODO create the file from builder and push to the list or array
                }
            }

        }

        System.out.println(totalNumberOfLines);


    }


    public PartitionSet getParitionedFiles(File input, int numberOfPartitions) {

        PartitionSet partitionSet = null;
        // break the files into partitions
        try {
            this.createPartitions(input,numberOfPartitions);

            // create test and train file partitions from the created partition files
            partitionSet = new PartitionSet();
            for (int i=0; i<numberOfPartitions; i++){
                // TODO create train and test files and push to partition set
            }

        } catch (IOException e) {
            System.err.println("Error creating partitions : "+e.getMessage());
        }

        return partitionSet;
    }

    public static void main(String[] args) throws IOException {

        File inputFile = new File("/home/farazath/Ananya-NER-Tools/output/train_21_Nov.tsv");
        new PartitionMaker().createPartitions(inputFile, 10);


    }
}
