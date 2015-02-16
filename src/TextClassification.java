import java.io.File;
import java.util.HashSet;


public class TextClassification {

	
	public static void main(String[] args) {
		String trainPath = "train";
		String testPath = "test";
		File data = new File(trainPath);
		
		HashSet<String> classes = new DocumentUtility().readClasses(data);
		
		BayesClassification bayesClassification = new BayesClassification();
		NBModel nbModel = bayesClassification.trainMultinomial(classes, data);
		
		data = new File(testPath);
		
		// for each file in the test data check its classification
		// match this classification to check whether it is correctly classified
		bayesClassification.testAccuracy(classes, nbModel, data);
		
	}
}
