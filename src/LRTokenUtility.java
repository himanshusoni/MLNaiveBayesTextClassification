import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;


public class LRTokenUtility extends TokenUtility{
	HashMap<String,HashMap<String,HashMap<String,Integer>>> tokenForLR = new HashMap<>();	

	public LRTokenUtility(boolean ignoreStopWords) {
		this.classifier = "Logisitc Regression Classifier";
		this.ignoreStopWords = ignoreStopWords;
	}
	public HashSet<String> extractVocabulary(File data,HashSet<String> classes){
		if(ignoreStopWords)
			readStop(new File(stopWordsFile));

		for(String classValue : classes){
			extractVocabularyFromClass(data, classValue);
		}
		return vocabulary;
	}
	public void extractVocabularyFromClass(File data, String classValue) {
		HashMap<String,HashMap<String,Integer>> fileTokenCount = new HashMap<String,HashMap<String,Integer>>();
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
			HashMap<String,Integer> tokenCount = new HashMap<String, Integer>();
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
			fileTokenCount.put(fileItem.getName(), tokenCount);
		}

		tokenForLR.put(classValue, fileTokenCount);
	}
}
