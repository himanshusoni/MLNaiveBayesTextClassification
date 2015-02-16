import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;


public class TokenUtility {

	String currentClass;
	String totalTokens = "totalTokens";
	String tokenRegEx = "[^A-Za-z]+";
	HashSet<String> stopWords = new HashSet<String>();
	HashSet<String> vocabulary = new HashSet<String>();
	HashMap<String,HashMap<String,Integer>> classTokenCount = new HashMap<String,HashMap<String,Integer>>();


	public TokenUtility(){

	}
	public void setClasses(HashSet<String> classes){
		for(String s : classes){
			classTokenCount.put(s, new HashMap<String,Integer>());
		}
	}
	public void setCurrentClass(String currentClass){
		this.currentClass = currentClass;
	}
	public void readStop(File data){
		try{
			Scanner tokenScanner = new Scanner(data);
			tokenScanner.useDelimiter(tokenRegEx);
			while (tokenScanner.hasNext()) {
				String token = tokenScanner.next();
				stopWords.add(token);
			}
			tokenScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public HashSet<String> extractVocabulary(File data,boolean ignoreStopWords){
		for(String classValue : classTokenCount.keySet()){
			extractVocabularyFromClass(data, classValue, ignoreStopWords);
		}
		return vocabulary;
	}
	public void extractVocabularyFromClass(File data,String classValue,boolean ignoreStopWords){
		HashMap<String,Integer> tokenCount = classTokenCount.get(classValue);
		int totalTokenCount = 0;
		File[] classFiles = data.listFiles();
		File classFile = null;
		for(File f : classFiles)
		{
			if(f.getName().equals(classValue))
			{
				classFile = f;
				break;
			}
		}

		for(File fileItem : classFile.listFiles()){
			try {
				Scanner tokenScanner = new Scanner(fileItem);
				tokenScanner.useDelimiter(tokenRegEx);

				while (tokenScanner.hasNext()) {
					String token = tokenScanner.next();

					if (ignoreStopWords && stopWords.contains(token)){
						continue;
					}
					else {
						if(!vocabulary.contains(token))
							vocabulary.add(token);
						totalTokenCount++;
						Integer count = tokenCount.get(token);
						if (count == null) {
							tokenCount.put(token, new Integer(1));
						} else {
							count++;
							tokenCount.put(token, count);
						}
					}
				}
				tokenCount.put(totalTokens, totalTokenCount);

				tokenScanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	public int countTokens(File textC){
		return classTokenCount.get(currentClass).get(totalTokens);
	}
	public int countTokens(File textC, String token){
		Integer count = classTokenCount.get(currentClass).get(token);
		count = (count == null) ? 0 : count;
		return count;
	}
	public int countUniqueTokens(File textC){
		return classTokenCount.get(currentClass).size();
	}
	public ArrayList<String> extractTokenFromDocument(HashSet<String> vocabulary, File data,boolean ignoreStopWords){
		ArrayList<String> tokens = new ArrayList<String>();
		try{
			Scanner tokenScanner = new Scanner(data);
			tokenScanner.useDelimiter(tokenRegEx);
			while (tokenScanner.hasNext()) {
				String token = tokenScanner.next();

				if (ignoreStopWords && stopWords.contains(token)){
					continue;
				}else{
					tokens.add(token);
				}
			}
			tokenScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return tokens;
	}
}
