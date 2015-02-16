import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class BayesClassification {

	boolean ignoreStopWords;
	String missingToken = "missing";
	int laplaceSmoothing = 1;
	
	DocumentUtility documentUtility = new DocumentUtility();
	TokenUtility tokenUtility = new TokenUtility();
	public BayesClassification(){

	}
	
	public NBModel trainMultinomial(HashSet<String> classes,File data){

		tokenUtility.setClasses(classes);

		ArrayList<Double> prior = new ArrayList<Double>();
		HashMap<String,HashMap<String,Double>> conditionalProbMap = new HashMap<String,HashMap<String,Double>>();		

		HashSet<String> vocabulary = tokenUtility.extractVocabulary(data,ignoreStopWords);
		int totalFile = documentUtility.countDocs(data);
		for(String classValue : classes){
			tokenUtility.setCurrentClass(classValue);
			HashMap<String,Double> conditionalProbability = new HashMap<String,Double>();
			conditionalProbMap.put(classValue, conditionalProbability);

			int numberOfFilesInClass = documentUtility.countDocsInClass(data, classValue);
			prior.add((double)numberOfFilesInClass/totalFile);
			File textC = documentUtility.concatenateTextFromAllDocsInClasses(data, classValue);

			//int countOfWordsinvocabulary = tokenUtility.countTokens(textC);
			int countOfWordsinvocabulary = vocabulary.size();
			int countOfWordsinClass = tokenUtility.countTokens(textC);

			conditionalProbability.put(missingToken, (double)laplaceSmoothing/(countOfWordsinClass+countOfWordsinvocabulary+laplaceSmoothing));

			for(String token : vocabulary){
				int tokenCount = tokenUtility.countTokens(textC, token);

				double condProb = (double)(tokenCount+laplaceSmoothing)/(countOfWordsinClass+countOfWordsinvocabulary+laplaceSmoothing);
				conditionalProbability.put(token,condProb);
			}
		}

		return new NBModel(vocabulary,prior,conditionalProbMap);
	}
	public String applyMultinomial(HashSet<String> classes,NBModel nbModel,File data){
		ArrayList<String> tokens = tokenUtility.extractTokenFromDocument(nbModel.getVocabulary(), data,ignoreStopWords);

		HashMap<String,Double> score = new HashMap<String,Double>();

		//System.out.print("\nFile : "+data.getName());
		int i = 0;
		for(String classValue : classes){
			HashMap<String,Double> conditionalProbability = nbModel.getConditionalProbMap().get(classValue);
			double prior = nbModel.getPrior().get(i);
			double scoreValue = Math.log(prior);

			for(String token : tokens){
				Double condProb = conditionalProbability.get(token);
				if(condProb==null)
				{
					condProb = conditionalProbability.get(missingToken); //System.out.print(token+" MM : ");
				}
				//System.out.print(" = "+condProb+" = ");
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
		//System.out.print(" : classified as : " + maxEntry.getKey());
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

		int j = 0;
		System.out.println("Accuracy of Bayes Classification");
		System.out.println("================================");
		System.out.println("class\t\tTotal Files\tCorrectly Classified");
		for(String classValue : classes){
			System.out.println(classValue+"\t\t"+count[j]+"\t\t"+score[j]);
			double accuracy = ((double)score[j]/count[j])*100;
			System.out.println("Accuracy (in %)\t\t" + accuracy);
			j++;
		}
	}
}
