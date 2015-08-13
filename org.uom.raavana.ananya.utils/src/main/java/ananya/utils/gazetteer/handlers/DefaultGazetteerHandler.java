package ananya.utils.gazetteer.handlers;

import corpus.sinhala.SinhalaTokenizer;
import corpus.sinhala.SinhalaVowelLetterFixer;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultGazetteerHandler implements GazetteerLineHandler {

    protected SinhalaTokenizer sinhalaTokenizer = new SinhalaTokenizer();
    protected SinhalaVowelLetterFixer vowelLetterFixer = new SinhalaVowelLetterFixer();

    protected final String[] shortForms = new String[]{"ඒ", "බී", "සී", "ඩී", "ඊ", "එෆ්", "ජී", "එච්", "අයි", "ජේ", "කේ", "එල්", "එම්", "එන්", "ඕ", "පී", "කිව්", "ආර්", "එස්", "ටී", "යූ", "ඩබ්", "ඩබ්ලිව්", "එක්ස්", "වයි", "ඉසෙඩ්", "පෙ", "ව", "ප", "රු", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    protected final List<String> shortFormsList = Arrays.asList(shortForms);

    @Override
    public List<String> handleGazetteerLine(String line) {
        //     String words[] = line.trim().split("\\s+");
        List<String> words = sinhalaTokenizer.splitWords(line.trim());
        List<String> cleanedWordsList = new ArrayList<>();

        for (String word : words){
            if (StringUtils.isNotEmpty(word) && !shortFormsList.contains(word)){
                String fixedText = vowelLetterFixer.fixText(word,true);
                cleanedWordsList.add(fixedText);
            }
        }

        return cleanedWordsList;
    }
}
