package org.uom.raavana.ananya.service.endpoints.gazetteer;

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

		ArrayList<int[]> offsetList = new ArrayList<int[]>();

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
						"වීරවංශ\n" +
						"බේරුගොඩ\n" +
						"යෝහේස්වරන්\n" +
						"අබේවර්ධන\n" +
						"තොණ්ඩමන්\n" +
						"අලුවිහාරේ\n" +
						"ගමලත්\n" +
						"තෙවරප්පෙරුම\n" +
						"බුද්ධදාස\n" +
						"සෙනෙවිරත්න\n" +
						"වෙදආරච්චි\n" +
						"ශ්\u200Dරී රංගා\n" +
						"ජාගොඩගේ\n" +
						"නාවින්න\n" +
						"හෂීම්\n" +
						"සේමසිංහ\n" +
						"කැට\u200Dගොඩ\n" +
						"විදානගමගේ\n" +
						"වන්නිආරච්චි\n" +
						"රණසිංහ\n" +
						"රත්නායක\n" +
						"මද්දුම බණ්ඩාර\n" +
						"අතුකෝරල\n" +
						"කුමාරණතුංග\n" +
						"ලොකුබණ්ඩාර\n" +
						"අතාවුල්ලා\n" +
						"අමරතුංග\n" +
						"ලොකුගේ\n" +
						"ඒකනායක\n" +
						"වික්\u200Dරමරත්න\n" +
						"ජයසේන\n" +
						"දහනායක\n" +
						"අමීර් අලී\n" +
						"දිසානායක\n" +
						"කාසිම්\n" +
						"යෝගරාජන්\n" +
						"ආනන්දන්\n" +
						"ප්\u200Dරේමජයන්ත\n" +
						"සිල්වා\n" +
						"ස්වර්ණමාලි\n" +
						"ගමගේ\n" +
						"ප්\u200Dරේමදාස\n" +
						"කොඩිතුවක්කු\n" +
						"රාමනායක\n" +
						"රාජපක්ෂ\n" +
						"ඉන්දික\n" +
						"පුංචිනිලමේ\n" +
						"වීරකෝන්\n" +
						"අලගියවන්න\n" +
						"ජයසිංහ\n" +
						"ගනේසන්\n" +
						"මුතුහෙට්ටිගමගේ\n" +
						"පෙරේරා\n" +
						"රාධක්\u200Dරිෂ්නන්\n" +
						"හිස්බුල්ලා\n" +
						"ගුණරත්න\n" +
						"මිත්\u200Dරපාල\n" +
						"යාපා\n" +
						"බාලසූරිය\n" +
						"පතිරණ\n" +
						"හරීස්\n" +
						"කුරේ\n" +
						"බස්නායක\n" +
						"හලීම්\n" +
						"ප්\u200Dරනාන්\u200Dදුපුල්ලේ\n" +
						"ගුණවර්ධන\n" +
						"ජයසේකර\n" +
						"ෆොන්සේකා\n" +
						"සූරියආරච්චි\n" +
						"සේනාරත්න\n" +
						"ජයරත්න\n" +
						"වෙල්ගම\n" +
						"වේලායුදම්\n" +
						"ගජදීර\n" +
						"විජේසේකර\n" +
						"මුරලිදරන්\n" +
						"චන්ද්\u200Dරසේන\n" +
						"දේවානන්ද\n" +
						"අලස්\n" +
						"බණ්ඩාරනායක\n" +
						"රතන\n" +
						"සේනසිංහ\n" +
						"චන්ද්\u200Dරකුමාර්\n" +
						"විජේවික්\u200Dරම\n" +
						"සරවනපවන්\n" +
						"හේරත්\n" +
						"සේගු ඩාවුඩ්\n" +
						"කිරිඇල්ල\n" +
						"අබ්දුල් කාදර්\n" +
						"සුමන්තිරන්\n" +
						"වි\u200Dනෝ\n" +
						"ද සොයිසා\n" +
						"අමරවීර\n" +
						"සේනානායක\n" +
						"අඩෛක්කලනාදන්\n" +
						"ප්\u200Dරේමචන්ද්\u200Dරන්\n" +
						"රත්වත්තේ\n" +
						"ජයසූරිය\n" +
						"අමුණුගම\n" +
						"සේනාධිරාජා\n" +
						"විජයවර්ධන\n" +
						"තෙන්නකෝන්\n" +
						"වීරක්කොඩි\n" +
						"සියඹලාපිටිය\n" +
						"සම්පන්දන්\n" +
						"වීරවර්ධන\n" +
						"ප්\u200Dරනාන්දු\n" +
						"මේධානන්ද\n" +
						"තිසේරා\n" +
						"බණ්ඩාර\n" +
						"ස්වාමිනාදන්\n" +
						"ද සිල්වා\n" +
						"පීරිස්\n" +
						"මහේස්වරන්\n" +
						"ද වාස් ගුණවර්ධන\n" +
						"ෆාරුක්\n" +
						"විජේසිංහ\n" +
						"කරුණාතිලක\n" +
						"නානායක්කාර\n" +
						"වක්කුඹුර\n" +
						"සමරසිංහ\n" +
						"විතාරණ\n" +
						"කුමාර\n" +
						"අබේසිංහ\n" +
						"අත්තනායක\n" +
						"බදියුදීන්\n" +
						"ශ්\u200Dරීතරන්\n" +
						"වික්\u200Dරමසිංහ\n" +
						"කාරියවසම්\n" +
						"පද්මසිරි\n" +
						"මාන්නප්පෙරුම\n" +
						"විමලදාස\n" +
						"අබේගුණවර්ධන\n" +
						"අලහප්පෙරුම\n" +
						"පුෂ්පකුමාර\n" +
						"රණතුංග\n" +
						"දයාරත්න\n" +
						"රාජපක්ෂ\n" +
						"අස්ලම්\n" +
						"සොයිසා\n" +
						"හකීම්\n" +
						"රාජපක්\u200Dෂ\n" +
						"හැරිසන්\n" +
						"ගුණසේකර\n" +
						"ගන්කන්ද\n" +
						"ෆවුසි\n" +
						"විනායගමූර්ති\n" +
						"වික්\u200Dරමනායක\n" +
						"දිගම්බරම්\n" +
						"තව්ෆීක්\n" +
						"සිවලිංගම්\n" +
						"ගමගේ\n" +
						"රඹුක්වැල්ල\n" +
						"රණවක\n" +
						"රාජදුරයි\n" +
						"අලාන්ටින්\n" +
						"මුතුකුමාරණ\n" +
						"සූරියප්පෙරුම\n" +
						"පියසේන\n" +
						"ජයමහ\n" +
						"වීරසේකර\n" +
						"ෆාරූක්\n" +
						"මුස්තාපා\n" +
						"කරල්ලියද්ද\n" +
						"සමරවීර\n" +
						"ග්\u200Dරේරු\n" +
						"ඇන්ටනි\n" +
						"අරියනේත්\u200Dරන්\n" +
						"සුමතිපාල\n" +
						"අලුත්ගමගේ\n" +
						"කරුණානායක\n" +
						"හඳුන්නෙත්ති\n" +
						"කොතලාවල\n" +
						"සෙල්වරාසා");
	}
	
	
}
