package org.uom.raavana.ananya.service.endpoints;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ananya.utils.gazetteer.Gazetteer;
import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import corpus.sinhala.SinhalaTokenizer;
import corpus.sinhala.SinhalaVowelLetterFixer;


public class AnnotationService {
	
	private String fixedText;
	private Gazetteer gazetteer;
	SinhalaVowelLetterFixer vowelFixer;
	private Map<String, TagEntry> entityObjectMap;
	private static final String PUNCTUATIONS = ",./-@#$ \n\t";

	public AnnotationService(String gazetteerPath) throws InstantiationException, IllegalAccessException {

		gazetteer = Gazetteer.getInstance(gazetteerPath);
		entityObjectMap = new HashMap<>();
		vowelFixer = new SinhalaVowelLetterFixer();

	}

	private ArrayList<int[]> getCharacterOffsetArray(String checkText){
		int Stringlength = checkText.trim().length();

		ArrayList<int[]> offsetList = new ArrayList<>();

		int [] offsetPair ;
		int count = 0;

		for (int i = -1; (i = fixedText.indexOf(checkText.trim(), i + 1)) != -1; ) {
			int start = i;
			int end = i+Stringlength;

			// check the previous letter
			if (start -1 > 0 && !PUNCTUATIONS.contains(fixedText.charAt(start-1)+"")){
				continue;
			}else if (end< fixedText.length() && !PUNCTUATIONS.contains(fixedText.charAt(end)+"")){
				continue;
			}else{
				offsetPair = new int[]{start,end};
				offsetList.add(offsetPair);
				count++;
			}

		}

	//	System.out.println(checkText+"------------->"+count);
		return offsetList;
	}

	private List<int[]> getOffSetArray(String checkText){
		int stringLength = checkText.length();
		List<int[]> offsetList = new ArrayList<>();

		Pattern pattern = Pattern.compile("\\b"+ checkText +"\\b");
		Matcher matcher = pattern.matcher(fixedText);

		int count = 0;
		while(matcher.find()){
			int start = matcher.start();
			int end = start + stringLength;

			offsetList.add(new int[]{start,end});
			count++;
		}

		System.out.println(checkText+"------------->"+count+"--------------------> "+pattern.toString());
		return offsetList;

	}


	private List<String> tokenizeText(){
		
		SinhalaTokenizer tokenizer = new SinhalaTokenizer();
	    return tokenizer.splitWords(fixedText);
	}

	/**
	 *
	 * @param originText
	 */
	private void cleanText(String originText){
		//SinhalaVowelLetterFixer vowelFixer = new SinhalaVowelLetterFixer();
	    //fixedText = vowelFixer.fixText(originText,false);
		fixedText = originText;
	}
	
	public String getFixedText(){
		return fixedText;
	}

	private void generateEntities() {

		List<String> tokenizedWords = tokenizeText();

		// get unique words from the list of tokenized words
		Set<String> uniqueWordsSet = new HashSet<>(tokenizedWords);
		List<int[]> offSetList;

		String currentToken;
		String[] arrayOfTexts;
		int entityCount = 0;
		int countOffSets = 0;

		Iterator<String> uniqStringIterator = uniqueWordsSet.iterator();
		while (uniqStringIterator.hasNext()) {
			currentToken = uniqStringIterator.next();
			//  get the offsets of texts occurrences
			offSetList = getCharacterOffsetArray(currentToken);
		//	characterOffsetArray = getOffSetArray(currentToken);
			countOffSets += offSetList.size();

			// tag for the text
			String tag = gazetteer.findNamedEntityTag(currentToken);

			if (tag != null){
				// text for tag
				arrayOfTexts = new String[]{currentToken};

				for (int[] offset : offSetList){
					List<int[]> offSetListForTag = new ArrayList<>();
					offSetListForTag.add(offset);
					TagEntry entity = new TagEntry(tag, arrayOfTexts,offSetListForTag);
					entityObjectMap.put("T" + Integer.toString(entityCount), entity);
					entityCount++;
				}

				System.out.println(currentToken+"\t"+tag);
			}
		}

		System.out.println("Words : "+tokenizedWords.size());
		System.out.println("tags : "+entityCount);
		System.out.println("Offsets : "+countOffSets);
	}

	
	private String generateJSON(){
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();;
		String jsonOutput = gson.toJson(entityObjectMap);
		
		return jsonOutput;
				
	}


	/**
	 *
	 * @param text
	 * @return a json string tags
     */
	public String tagNamedEntities(String text){

		// clean and set fixed texts
		cleanText(text);
		// generate the NE tags
		generateEntities();
		// convert to json and return
		return generateJSON();
	}

	public static void main(String args []){

		AnnotationService service = null;
		try {
			service = new AnnotationService("/home/farazath/.ananya/input/gazetteer");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		String response =
				service.tagNamedEntities(
                        "යූ.එස්. සුනිල් සරත් කුමාර මහතා පැවැසීය. විද්\u200Dයාලයේ “පැහැසර මාධ්\u200Dය ඒකකය” මෙය සංවිධාන කරයි. පළමු ශ්\u200Dරේණියේ සිට දහතුන්වැනි ශ්\u200Dරේණිය දක්වා පන්ති පැවැත්වෙන මෙම විදුහලේ ශිෂ්\u200Dය සංඛ්\u200Dයාව හත්සියයකි."
                );

        System.out.println(response);
    }
	
	
}
