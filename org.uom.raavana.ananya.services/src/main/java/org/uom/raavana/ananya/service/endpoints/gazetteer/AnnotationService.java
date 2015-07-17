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
	private Map<String, EntityObject> entityObjectMap;
	private static final String PUNCTUATIONS = ",./-@#$ ";

	public AnnotationService(String gazetteerPath){

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

		System.out.println(checkText+"------------->"+count);
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
		SinhalaVowelLetterFixer vowelFixer = new SinhalaVowelLetterFixer();
	    fixedText = vowelFixer.fixText(originText,false);
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
			// text for tag
			arrayOfTexts = new String[]{currentToken};

			for (int[] offest : offSetList){
				List<int[]> offSetListForTag = new ArrayList<>();
				offSetListForTag.add(offest);
				EntityObject entity = new EntityObject(tag, arrayOfTexts,offSetListForTag);
				entityObjectMap.put("T" + Integer.toString(entityCount), entity);
				entityCount++;
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

		AnnotationService service = new AnnotationService("/home/farazath/Ananya/input/gazetteer");

		String response =
				service.tagNamedEntities("මධ්\u200Dයම පරිසර අධිකාරිය විසින් දීප ව්\u200Dයාප්තව පාසල් මට්ටමින් ක\u200D්\u200Dරියාත්මක කරනු ලබන ජාතික පරිසර නියමු වැඩසටහනේ කෑගල්ල දිස්ත\u200D්\u200Dරික් පරිසර නියමු කඳවුර පසුගිය ඔක්තෝම්බර් 14 සහ 15 යන දෙදින පුරා කෑගලූ විද්\u200Dයාලයේ දී පැවැත්විණි. මෙම කඳවුර සදහා කෑගල්ල දිස්ත\u200D්\u200Dරික්කයේ පාසල් නියෝජනය කරමින් පරිසර නියමු සිසු සිසුවියන් විශාල සංඛ්\u200Dයාවක් සහභාගී වූ අතර සිසුන්ගේ පරිසර දැනුම වර්ධනය සදහා ඉවහල් වන ක\u200D්\u200Dරියාකාරකම් රැසකින් කදවුර සමන්විත විය. මෙම දිස්ත\u200D්\u200Dරික් පරිසර නියමු කදවුරෙහි සමාරම්භක උත්සවය මධ්\u200Dයම පරිසර අධිකාරියේ සභාපති චරිත හේරත් මහතාගේ ප\u200D්\u200Dරධානත්වයෙන් කෑගලූ විද්\u200Dයාලීය ප\u200D්\u200Dරධාන ශාලාවේ දී පැවැත්විණ. දිස්ත\u200D්\u200Dරික් කඳවුර සදහා පරිසර නියමුවන්ගේ සංස්කෘතික අංග මෙන්ම දරු දැරියන් සදහා ඉතා හරවත් විද්වත් දේශන රැසක් එක්කර තිබීම විශේෂත්වයකි. පොලිතින් සහ ප්ලාස්ටික් මගින් ඇති විය හැකි පරිසර හානිය සහ ඒවා නිසි ලෙස කළමනාකරණය කිරීම පිළිබදව මෙහිදී විශේෂ දැනුම්වත් කිරීමක් මධ්\u200Dයම පරිසර අධිකාරියේ පසුභාවික ප්ලාස්ටික් අපද්\u200Dරව්\u200Dය කළමනාකරණ ජාතික ව්\u200Dයාපෘතිය මගින් සිදු කෙරිණ. මෙහිදී කෑගලූ විද්\u200Dයාලය සඳහා කසල වෙන් වෙන් වශයෙන් බැහැර කළ හැකි බඳුන් පරිත්\u200Dයාග කිරීමක් ද සිදුවිය. පරිසර කඳවුරේ ප\u200D්\u200Dරායෝගික ක\u200D්\u200Dරියාකාරකමක් ලෙස ක්\u200Dෂේත\u200D්\u200Dර ගවේශණය සඳහා පරිසර නියමුවන් පාසල අසල පිහිටි කුරුලූ කැළය වෙත රැගෙන ගිය අතර වන සංරක්\u200Dෂණ නිලධාරීන්ගේ සහාය ඇතිව ක්\u200Dෂේත\u200D්\u200Dර ක\u200D්\u200Dරියාකාරකම් මගින් ප\u200D්\u200Dරායෝගික අත් දැකීමක් ලබා ගැනීමට ඔවුනට අවස්ථාව උදා විය");
//		System.out.println(response);
	
	}
	
	
}
