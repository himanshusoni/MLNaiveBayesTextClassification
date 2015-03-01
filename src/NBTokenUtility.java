import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;


public class NBTokenUtility extends TokenUtility{

	String currentClass;
	HashMap<String,HashMap<String,Integer>> classTokenCount = new HashMap<String,HashMap<String,Integer>>();

	public NBTokenUtility(boolean ignoreStopWords){
		this.classifier = "Naive Bayes Classifier";
		this.ignoreStopWords = ignoreStopWords;
	}
	public void setClasses(HashSet<String> classes){
		for(String s : classes){
			classTokenCount.put(s, new HashMap<String,Integer>());
		}
	}
	public void setCurrentClass(String currentClass){
		this.currentClass = currentClass;
	}
	public HashSet<String> extractVocabulary(File data,HashSet<String> classes){
		if(ignoreStopWords)
			readStop(new File(stopWordsFile));

		for(String classValue : classes){
			extractVocabularyFromClass(data, classValue);
		}
		return vocabulary;
	}
	public void extractVocabularyFromClass(File data,String classValue){
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
	public int countTokens(){
		return classTokenCount.get(currentClass).get(totalTokens);
	}
	public int countTokens(String token){
		Integer count = classTokenCount.get(currentClass).get(token);
		count = (count == null) ? 0 : count;
		return count;
	}
	public int countUniqueTokens(){
		return classTokenCount.get(currentClass).size();
	}
}
