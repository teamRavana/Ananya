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
	
	private LinkedList<String> tokenizeText(){
		
		SinhalaTokenizer tokenizer = new SinhalaTokenizer();
	    LinkedList<String> tokenizedwords = tokenizer.splitWords(fixedText);
	    
	    return tokenizedwords;		
			
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
		LinkedList<String> tokenizedWords = tokenizeText();
		ArrayList<int[]> characterOffsetArray;

		String currentToken;
		String[] arrayOfTexts;
		int entityCount = 1;

		ListIterator<String> tokenWordsIter = tokenizedWords.listIterator();
		while (tokenWordsIter.hasNext()) {
			currentToken = tokenWordsIter.next();
			//  get the offsets of text occurrences
			characterOffsetArray = getCharacterOffsetArray(currentToken);
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

		// clean and set fixed text
		cleanText(text);
		// generate the NE tags
		generateEntities();
		// convert to json and return
		return generateJSON();
	}

	public static void main(String args []){

		AnnotationService service = new AnnotationService("/home/farazath/Ananya/input/gazetteer");

		String response =
				service.tagNamedEntities("මාතර දෙස සිට අධිකවේගයෙන් පැමිණ ඇති ලොරියේ තිබූ විදුලි කණු හදිසියේම මහා මාර්ගයට ඇද වැටීම නිසා රියදුරුට ලොරිය පාලනය කර ගැනීමට නොහැකි වීමෙන් මෙම අනතුර සිදුවී ඇතැයි මූලික විමර්ශන වලින් හෙළිව තිබෙනවා");

		System.out.println(response);
	
	}
	
	
}
