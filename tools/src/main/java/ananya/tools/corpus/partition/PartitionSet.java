package ananya.tools.corpus.partition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PartitionSet {

    private List<File> trainFiles;
    private List<File> testFiles;


    public PartitionSet() {
    }

    public List<File> getTrainFiles() {
        return trainFiles;
    }

    public void setTrainFiles(List<File> trainFiles) {
        this.trainFiles = trainFiles;
    }

    public List<File> getTestFiles() {
        return testFiles;
    }

    public void setTestFiles(List<File> testFiles) {
        this.testFiles = testFiles;
    }

    public void addTrainFile(File file){
        if (this.trainFiles == null){
            this.trainFiles = new ArrayList<>();
        }

        this.trainFiles.add(file);
    }

    public void addTestFile(File file){
        if (this.testFiles == null) {
            this.testFiles = new ArrayList<>();
        }
        this.testFiles.add(file);
    }
}
