import java.io.File;
import java.util.HashMap;
import java.util.HashSet;


public class LogisticRegression {
	
	final int ITERATIONS;
	boolean ignoreStopWords;
	String missingToken = "missing";
	int laplaceSmoothing = 1;
	double lr = 0.001;
	double penalty = 10;
	LRTokenUtility lrTokenUtility;

	HashMap<String,Double> weightVector = new HashMap<String, Double>();

	public LogisticRegression(boolean ignoreStopWords,int iterations) {
		this.ignoreStopWords = ignoreStopWords;
		lrTokenUtility = new LRTokenUtility(ignoreStopWords);
		this.ITERATIONS = iterations;
	}
	public LogisticRegression(boolean ignoreStopWords,int iterations,double lr,double penalty) {		
		this(ignoreStopWords,iterations);
		this.lr =lr;
		this.penalty = penalty;
	}
	public void trainMultinomial(HashSet<String> classes,File data){
		System.out.println("Training with n(eta) : "+lr + " and lambda : " + penalty +" Hard Limit : " + ITERATIONS);

		HashSet<String> vocabulary = lrTokenUtility.extractVocabulary(data,classes);
		HashMap<String,HashMap<String,HashMap<String,Integer>>> tokenForLR = lrTokenUtility.tokenForLR;	

		initWeigthVector(vocabulary);
		HashMap<String,Double> probabilityForFile = new HashMap<String, Double>();

		for(int i = 1;i<=ITERATIONS;i++){
			
			System.out.print(".");
			if(i%50==0)
				System.out.println();
			
			for(String classValue : classes){
				for(String fileItem : tokenForLR.get(classValue).keySet()){
					double probability = 0;
					HashMap<String, Integer> temp = tokenForLR.get(classValue).get(fileItem);
					for(String s : temp.keySet()){
						probability+= weightVector.get(s) * temp.get(s);
					}
					probability = 1 / (1 + Math.exp(-probability));
					probabilityForFile.put(fileItem, probability);
				}
			}
			int Y = 0;
			for(String word : vocabulary){ 
				double result = 0;
				for(String classValue : classes){
					if(classValue.equals("spam"))
						Y = 1;
					else
						Y = 0;
					for(String fileName : tokenForLR.get(classValue).keySet()){
						Double probability = probabilityForFile.get(fileName);
						Integer X = tokenForLR.get(classValue).get(fileName).get(word);
						if(X == null)
							X = 0;
						result = result + X*(Y - probability);
					}
				}
				double weight = weightVector.get(word);
				weight = weight + lr * result - lr*penalty*weight;
				weightVector.put(word, weight);
			}
		}
	}
	private void initWeigthVector(HashSet<String> vocabulary) {
		for(String s : vocabulary){
			double value = 0.15;
			weightVector.put(s, value);
		}
	}
	public String applyMultinomial(HashMap<String,Double> weightMap,File data){
		HashMap<String,Integer> tokenCount = lrTokenUtility.extractTokenFromDocument(data);
		double scoreValue = 0;
		for(String token : tokenCount.keySet()){
			if(ignoreStopWords && lrTokenUtility.getStopWords().contains(token))
				continue;
			Double weight = weightMap.get(token);
			if(weight == null)
				continue;
			scoreValue += weight*tokenCount.get(token);
		}
		if(scoreValue > 0)
			return "spam";
		else
			return "ham";
	}
	public void testAccuracy(HashSet<String> classes,File data){
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
				String classifiedValue = applyMultinomial(weightVector, fileItem);

				if(classifiedValue.equals(classValue))
					score[i]++;
			}
			i++;
		}

		lrTokenUtility.printResult(classes, count, score);
	}
}
