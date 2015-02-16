import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class NBModel {

	HashSet<String> vocabulary;
	ArrayList<Double> prior;
	HashMap<String, HashMap<String,Double>> conditionalProbMap;
	
	public NBModel(HashSet<String> vocabulary, ArrayList<Double> prior,
			HashMap<String, HashMap<String,Double>> conditionalProbMap) {
		this.vocabulary = vocabulary;
		this.prior = prior;
		this.conditionalProbMap = conditionalProbMap;
	}

	public HashSet<String> getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(HashSet<String> vocabulary) {
		this.vocabulary = vocabulary;
	}

	public ArrayList<Double> getPrior() {
		return prior;
	}

	public void setPrior(ArrayList<Double> prior) {
		this.prior = prior;
	}

	public HashMap<String, HashMap<String,Double>> getConditionalProbMap() {
		return conditionalProbMap;
	}

	public void setConditionalProbMap(
			HashMap<String, HashMap<String,Double>> conditionalProbMap) {
		this.conditionalProbMap = conditionalProbMap;
	}
	
}
