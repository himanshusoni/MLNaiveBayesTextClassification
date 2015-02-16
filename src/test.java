import java.io.File;
import java.util.HashSet;


public class test {

	public static void main(String[] args) {

		File data = new File("train");
		DocumentUtility doc = new DocumentUtility();
		HashSet<String> classes = new HashSet<String>();
		classes = doc.readClasses(data);
		System.out.println(classes);

		System.out.println(doc.countDocs(data));

		for(String s : classes){
			System.out.println(" docs in " + s);
			System.out.println(doc.countDocsInClass(data, s));
		}
		
		doc.concatenateTextFromAllDocsInClasses(data, "spam");
	}

}
