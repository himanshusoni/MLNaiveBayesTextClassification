import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class BayesClassification {

	int d = 1;
	boolean ignoreStopWords;
	String missingToken = "missing";
	int laplaceSmoothing = 1;

	DocumentUtility documentUtility = new DocumentUtility();
	NBTokenUtility nbTokenUtility;
	public BayesClassification(boolean ignoreStopWords){
		this.ignoreStopWords = ignoreStopWords;
		nbTokenUtility = new NBTokenUtility(ignoreStopWords);
	}

	public NBModel trainMultinomial(HashSet<String> classes,File data){

		nbTokenUtility.setClasses(classes);

		ArrayList<Double> prior = new ArrayList<Double>();
		HashMap<String,HashMap<String,Double>> conditionalProbMap = new HashMap<String,HashMap<String,Double>>();		

		HashSet<String> vocabulary = nbTokenUtility.extractVocabulary(data,classes);
		int totalFile = documentUtility.countDocs(data);
		for(String classValue : classes){
			nbTokenUtility.setCurrentClass(classValue);
			HashMap<String,Double> conditionalProbability = new HashMap<String,Double>();
			conditionalProbMap.put(classValue, conditionalProbability);

			int numberOfFilesInClass = documentUtility.countDocsInClass(data, classValue);
			prior.add((double)numberOfFilesInClass/totalFile);

			int countOfWordsinvocabulary = vocabulary.size();
			int countOfWordsinClass = nbTokenUtility.countTokens();

			conditionalProbability.put(missingToken, (double)laplaceSmoothing/(countOfWordsinClass+countOfWordsinvocabulary+laplaceSmoothing));

			for(String token : vocabulary){
				int tokenCount = nbTokenUtility.countTokens(token);

				double condProb = (double)(tokenCount+laplaceSmoothing)/(countOfWordsinClass+countOfWordsinvocabulary+laplaceSmoothing);
				conditionalProbability.put(token,condProb);
			}
		}

		return new NBModel(vocabulary,prior,conditionalProbMap);
	}
	public String applyMultinomial(HashSet<String> classes,NBModel nbModel,File data){
		HashMap<String,Integer> tokenCount = nbTokenUtility.extractTokenFromDocument(data);
		ArrayList<String> tokens = new ArrayList<String>(tokenCount.keySet());

		HashMap<String,Double> score = new HashMap<String,Double>();

		int i = 0;
		for(String classValue : classes){
			HashMap<String,Double> conditionalProbability = nbModel.getConditionalProbMap().get(classValue);
			double prior = nbModel.getPrior().get(i);
			double scoreValue = Math.log(prior);

			for(String token : tokens){
				if(ignoreStopWords && nbTokenUtility.getStopWords().contains(token))
					continue;
				Double condProb = conditionalProbability.get(token);
				if(condProb==null)
				{
					condProb = conditionalProbability.get(missingToken); 
				}
				scoreValue += Math.log(condProb);
			}
			score.put(classValue,scoreValue);
			i++;
		}

		Map.Entry<String, Double> maxEntry = null;
		for (Map.Entry<String, Double> entry : score.entrySet())
		{
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
			{
				maxEntry = entry;
			}
		}
		// returns the class the file should belong to {Spam OR Ham}
		return maxEntry.getKey();
	}
	public void testAccuracy(HashSet<String> classes,NBModel nbModel,File data){
		int[] score = new int[]{0,0};
		int[] count = new int[]{0,0};
		int i = 0;
		for(String classValue : classes){
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
				count[i]++;
				String classifiedValue = applyMultinomial(classes, nbModel, fileItem);

				if(classifiedValue.equals(classValue))
					score[i]++;
			}
			i++;
		}

		nbTokenUtility.printResult(classes, count, score);
	}
}
