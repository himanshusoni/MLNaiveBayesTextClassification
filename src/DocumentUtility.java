import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
			//System.out.println("here " + count);
		}
		return totalFiles;
	}
	public int countDocsInClass(File data,String classValue){
		Integer count = fileCount.get(classValue);
		count = (count != null) ? count : data.listFiles().length;
		fileCount.put(classValue, count);
		return count;
	}
	public File concatenateTextFromAllDocsInClasses(File data,String classValue){

		File textC = new File(classValue + "-All");
		if(textC.exists())
		{
			textC.delete();
		}

			BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(textC, true));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

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
		System.out.println(" Files in " + classValue + " : " + classFile.listFiles().length);
		for(File input : classFile.listFiles()){

			//System.out.println("reading from : " + input.getName());

			FileInputStream fis;
			try {
				fis = new FileInputStream(input);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));

				String line;
				while ((line = in.readLine()) != null) {
					out.write(line);
					out.newLine();
				}

				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}

		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("Done");
		return textC;
	}
}
