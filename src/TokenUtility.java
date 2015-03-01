import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;


public abstract class TokenUtility {
	
	String classifier = "";
	boolean ignoreStopWords;
	String stopWordsFile = "stop_words.txt";
	String totalTokens = "totalTokens";
	String tokenRegEx = "[^A-Za-z]+";
	HashSet<String> stopWords = new HashSet<String>();
	HashSet<String> vocabulary = new HashSet<String>();
	
	public abstract HashSet<String> extractVocabulary(File data,HashSet<String> classes);
	public abstract void extractVocabularyFromClass(File data, String classValue);
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
	public HashSet<String> getStopWords(){
		return stopWords;
	}
	public HashMap<String,Integer> extractTokenFromDocument(File data){
		HashMap<String,Integer> tokenCount = new HashMap<String,Integer>();
		try{
			Scanner tokenScanner = new Scanner(data);
			tokenScanner.useDelimiter(tokenRegEx);
			while (tokenScanner.hasNext()) {
				String token = tokenScanner.next();

				if (ignoreStopWords && stopWords.contains(token)){
					continue;
				}else{
					Integer count = tokenCount.get(token);
					if (count == null) {
						tokenCount.put(token, new Integer(1));
					} else {
						count++;
						tokenCount.put(token, count);
					}
				}
			}
			tokenScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return tokenCount;
	}
	public void printResult(HashSet<String> classes,int[] count,int[] score){
		int j = 0;
		String stopWordText = (ignoreStopWords == false) ? " " : "\nwith Ignore Stop Words ";
		System.out.println(stopWordText);
		for(String classValue : classes){
			double accuracy = ((double)score[j]/count[j])*100;
			System.out.print(classValue + " ( %)\t" + accuracy+"\t");
			j++;
		}
	}
}
