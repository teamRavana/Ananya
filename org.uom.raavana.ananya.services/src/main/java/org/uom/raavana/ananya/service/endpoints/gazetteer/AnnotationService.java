package org.uom.raavana.ananya.service.endpoints.gazetteer;

import java.util.*;

import ananya.utils.gazetteer.Gazetteer;
import com.google.gson.Gson;

import corpus.sinhala.SinhalaTokenizer;
import corpus.sinhala.SinhalaVowelLetterFixer;
import org.uom.raavana.ananya.service.endpoints.gazetteer.EntityObject;


public class AnnotationService {
	
	private static String fixedText;
	private static HashMap<String, String> Gazzatters = new HashMap<String, String>();
	private static Map<String, EntityObject> entityObjectMap = new HashMap<String, EntityObject>();

	public static ArrayList<int[]> getCharacterOffsetArray(String checkText){

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
	
	public static LinkedList<String> tokenizeText(){
		
		SinhalaTokenizer tokenizer = new SinhalaTokenizer();
	    LinkedList<String> tokenizedwords = tokenizer.splitWords(fixedText);
	    
	    return tokenizedwords;		
			
	}
	
	public static void cleanText(String originText){
		
		SinhalaVowelLetterFixer vowelFixer = new SinhalaVowelLetterFixer();
	    fixedText = vowelFixer.fixText(originText, false);
			
	}
	
	public String getFixedText(){
		return fixedText;
	}
	
	public static void generateEtities(){

//		Gazetteer gazetteer = Gazetteer.getInstance("");


		LinkedList<String> tokenizedWords = tokenizeText();
		ArrayList<int[]> characterOffsetArray = new ArrayList<int[]>();
		String currentIteration;
		String tag;
		String [] arrayOfTexts = new String[1];
		int entityCount = 0;
		
		ListIterator<String> listIterator = tokenizedWords.listIterator();
        while (listIterator.hasNext()) {
        	currentIteration = listIterator.next();
        	if(Gazzatters.containsKey(currentIteration)){            	
        		        		
        		characterOffsetArray = getCharacterOffsetArray(currentIteration);

//				tag= gazetteer.findNamedEntityTag("අතපත්තු");
//				EntityObject entity = new EntityObject(tag, currentIteration, charOffset);

				arrayOfTexts[0] = currentIteration;
        		EntityObject entity = new EntityObject(Gazzatters.get(currentIteration), arrayOfTexts, characterOffsetArray);
        		entityObjectMap.put("T"+Integer.toString(entityCount), entity);
        		entityCount++;
        	}
        	
        }
        
	}
	
	public static String generateJSON(){
		
		Gson gson = new Gson();
		String jsonOutput = gson.toJson(entityObjectMap);
		
		return jsonOutput;
				
	}
	
	public static void main(String args []){
		Gazzatters.put("මාතර", "Location");
		Gazzatters.put("විදුලි", "Location");
		Gazzatters.put("මාර්ගයට", "Location");
		Gazzatters.put("විමර්ශන", "Person");
		
		
		cleanText("මාතර දෙස සිට අධිකවේගයෙන් පැමිණ ඇති ලොරියේ තිබූ විදුලි කණු හදිසියේම මහා මාර්ගයට ඇද වැටීම නිසා රියදුරුට ලොරිය පාලනය කර ගැනීමට නොහැකි වීමෙන් මෙම අනතුර සිදුවී ඇතැයි මූලික විමර්ශන වලින් හෙළිව තිබෙනවා");
		generateEtities();
		System.out.println(generateJSON());
		System.out.println("Size: "+entityObjectMap.size());
	
	}
	
	
}
