package org.uom.raavana.ananya.service.endpoints.gazetteer;

import java.util.*;

import ananya.utils.gazetteer.Gazetteer;
import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import corpus.sinhala.SinhalaTokenizer;
import corpus.sinhala.SinhalaVowelLetterFixer;


public class AnnotationService {
	
	private String fixedText;
	private Gazetteer gazetteer;
	private Map<String, EntityObject> entityObjectMap;


	public AnnotationService(String gazetteerPath){

		gazetteer = Gazetteer.getInstance(gazetteerPath);
		entityObjectMap = new HashMap<>();

	}

	private ArrayList<int[]> getCharacterOffsetArray(String checkText){
		int Stringlength = checkText.length();

		ArrayList<int[]> offsetList = new ArrayList<int[]>();

		int [] offsetPair ;

		for (int i = -1; (i = fixedText.indexOf(checkText, i + 1)) != -1; ) {
			offsetPair = new int[2];
			offsetPair[0] = i;
			offsetPair[1] = i+Stringlength;
			offsetList.add(offsetPair);
		}

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
		SinhalaVowelLetterFixer vowelFixer = new SinhalaVowelLetterFixer();
	    fixedText = vowelFixer.fixText(originText, false);
	}
	
	public String getFixedText(){
		return fixedText;
	}

	private void generateEntities() {

		List<String> tokenizedWords = tokenizeText();
		// get unique words from the list of tokenized words
		Set<String> uniqueWordsSet = new HashSet<>(tokenizedWords);
		ArrayList<int[]> characterOffsetArray;

		String currentToken;
		String[] arrayOfTexts;
		int entityCount = 0;
		int countOffSets = 0;

		Iterator<String> uniqStringIterator = uniqueWordsSet.iterator();
		while (uniqStringIterator.hasNext()) {
			currentToken = uniqStringIterator.next();
			//  get the offsets of texts occurrences
			characterOffsetArray = getCharacterOffsetArray(currentToken);
			countOffSets += characterOffsetArray.size();
			arrayOfTexts = new String[]{currentToken};
			EntityObject entity = new EntityObject(gazetteer.findNamedEntityTag(currentToken), arrayOfTexts, characterOffsetArray);
			entityObjectMap.put("T" + Integer.toString(entityCount), entity);
			entityCount++;
		}
	}

	
	private String generateJSON(){
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();;
		String jsonOutput = gson.toJson(entityObjectMap);
		
		return jsonOutput;
				
	}


	public String tagNamedEntities(String text){

		// clean and set fixed texts
		cleanText(text);
		// generate the NE tags
		generateEntities();
		// convert to json and return
		return generateJSON();
	}

	public static void main(String args []){

		AnnotationService service = new AnnotationService("/home/farazath/Ananya/input/gazetteer");

		String response =
				service.tagNamedEntities("මාතර දෙස සිට මාතර දෙස සිට");

		System.out.println(response);
	
	}
	
	
}
