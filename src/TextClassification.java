import java.io.File;
import java.util.HashSet;


public class TextClassification {

	private static int phase = 0;
	private static long startTime, endTime, elapsedTime;

	String trainPath = "train";
	String testPath = "test";
	File trainData = new File(trainPath);
	File testData = new File(testPath);

	int hardLimit = 25;
	double lambda = 5;
	double learningRate = 0.01;

	int[] hardBountIterations = new int[]{10,25,50,100,200,300,500,1000};
	HashSet<String> classes = new DocumentUtility().readClasses(trainData);

	public static void main(String[] args) {
		TextClassification tc = new TextClassification();

		try {
			if(args.length == 5){
				tc.trainPath = args[0];
				tc.testPath = args[1];
				tc.lambda = Double.parseDouble(args[2]);
				tc.learningRate = Double.parseDouble(args[3]);
				tc.hardLimit = Integer.parseInt(args[4]);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}


		tc.runNaiveBayes();

		tc.runLogisiticRegression(tc.lambda,tc.learningRate,tc.hardLimit);

	}
	public void runLogisiticRegression(double lambda, double learningRate, int iterations){

		System.out.println("----Logistic Regression Classifier----");
		/*double[] learningRate = new double[]{0.1,0.01,0.001};
		double[] lambdaSpace = new double[]{1,3,5,10,15};
		for(int i = 0;i<lambdaSpace.length;i++)
		{*/
			//System.out.println(" lambda value : " + lambdaSpace[i]);

			System.out.println("---------------------------------------------------");

			timer();
			LogisticRegression lr = new LogisticRegression(false,iterations,learningRate,lambda);
			lr.trainMultinomial(classes, trainData);
			testData = new File(testPath);
			lr.testAccuracy(classes, testData);
			timer();

			System.out.println("---------------------------------------------------");

			timer();
			lr = new LogisticRegression(true,iterations,learningRate,lambda);
			lr.trainMultinomial(classes, trainData);
			testData = new File(testPath);
			lr.testAccuracy(classes, testData);
			timer();

			System.out.println("---------------------------------------------------");
		//}
	}
	public void runNaiveBayes(){

		System.out.println("----Naive Bayes Classifier----");
		System.out.println("---------------------------------------------------");

		timer();
		BayesClassification bayesClassification = new BayesClassification(false);
		NBModel nbModel = bayesClassification.trainMultinomial(classes, trainData);

		testData = new File(testPath);

		// for each file in the test data check its classification
		// match this classification to check whether it is correctly classified
		bayesClassification.testAccuracy(classes, nbModel, testData);
		timer();

		System.out.println("---------------------------------------------------");

		timer();
		bayesClassification = new BayesClassification(true);
		nbModel = bayesClassification.trainMultinomial(classes, trainData);
		testData = new File(testPath);
		bayesClassification.testAccuracy(classes, nbModel, testData);
		timer();


		System.out.println("---------------------------------------------------");
	}
	public static void timer()
	{
		if(phase == 0) {
			startTime = System.currentTimeMillis();
			phase = 1;
		} else {
			endTime = System.currentTimeMillis();
			elapsedTime = endTime-startTime;
			System.out.println("\nTime: " + elapsedTime + " msec.");
			phase = 0;
		}
	}
}
