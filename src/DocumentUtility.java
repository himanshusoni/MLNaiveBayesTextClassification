import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


public class DocumentUtility {
	HashMap<String, Integer> fileCount = new HashMap<String,Integer>();
	int totalFiles = 0;

	public DocumentUtility(){

	}
	public HashSet<String> readClasses(File data){
		String[] classesArray = data.list();
		HashSet<String> classes = new HashSet<String>(Arrays.asList(classesArray));
		return classes;
	}
	public int countDocs(File data){
		File[] classesFileList = data.listFiles();
		for(File f : classesFileList){	
			int count = countDocsInClass(f, f.getName());
			totalFiles+=count;
		}
		return totalFiles;
	}
	public int countDocsInClass(File data,String classValue){
		Integer count = fileCount.get(classValue);
		count = (count != null) ? count : data.listFiles().length;
		fileCount.put(classValue, count);
		return count;
	}
}
