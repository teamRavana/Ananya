package ananya.tools.gazetteer.handlers;

import java.util.Arrays;
import java.util.List;

public class NICLocationHandler extends DefaultGazetteerHandler{

    String[] wordsToRemove = {"හරහා","අසල","හා"};
    List<String> wordsToRemoveList = Arrays.asList(wordsToRemove);

    @Override
    public List<String> handleGazetteerLine(String line) {
        List<String> wordList = finalLastPartOfAddress(line);

        for(String removeWord : wordsToRemoveList){
            wordList.remove(removeWord);
        }

        int size = wordList.size();

        if (size == 0 || size ==1 ){
            return wordList;
        }else{
            return wordList.subList(size - 1, size);
        }

    }

    private List<String> finalLastPartOfAddress(String line){

        List<String> addressParts = Arrays.asList(line.trim().split(","));

        // if the list has only part, that means there are no commas separating the parts
        // then we use the tokenizer
        if (addressParts.size() == 1){
            addressParts = sinhalaTokenizer.splitWords(line);
        }else{
            int size = addressParts.size();
            addressParts = sinhalaTokenizer.splitWords(addressParts.get(size-1));
        }

        return addressParts;
    }
}
