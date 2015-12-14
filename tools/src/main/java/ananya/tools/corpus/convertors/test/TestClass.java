package ananya.tools.corpus.convertors.test;

import ananya.tools.corpus.convertors.BratToCRFConverter;
import ananya.tools.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class TestClass {

    public static void main(String[] args) throws IOException {

        File inputDir = new File("/home/farazath/Documents/FYP-Tagged-Corpus-20Nov/completed");
        File output = new File("/home/farazath/Ananya-NER-Tools/output/train_21_Nov.tsv");

        bratToCRF(inputDir,output);
    }




    public static void bratToCRF(File inputDir, File outputFile) throws IOException {

        if (!inputDir.exists()){
            throw new RuntimeException("Input Directory "+inputDir.getAbsolutePath()+" does not exist");
        }

        StringBuilder trainFileBuilder = new StringBuilder();
        for (File file : inputDir.listFiles()){

            if (file.getName().endsWith("txt")){
                String path = file.getAbsolutePath();
                String bratFileName = path.substring(0,path.length()-3)+"ann";
                File bratFile = new File(bratFileName);

                if (!bratFile.exists()){
                    throw new RuntimeException("Brat file missing for "+file.getName());
                }

                trainFileBuilder.append(BratToCRFConverter.convertToCRF(file,bratFile));
            }
        }

        FileUtils.writeFile(outputFile,trainFileBuilder.toString());

    }
}
