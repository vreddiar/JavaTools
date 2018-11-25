import java.io.*;
/**
 * @author vredd
 *
 */
public class SplitFile {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 4 || args.length > 5){
			System.out.println("Usage: SplitFile <Source Path> <Number Of Lines> <Destination Path> <File Extension> [Keep Headline=false]");
			System.out.println("Example: SplitFile source 3 source-split csv true");
			System.exit(1);
		}
		try {
			String sourcePath = args[0];
			int numOfLinesToRead = Integer.parseInt(args[1]);
			String destinationPath = args[2];
			String fileExtension = args[3];
			boolean keepHeadline = false;
			if (args[4] != null && args[4].equalsIgnoreCase("true")){
				keepHeadline = true;
			}
			
			BufferedReader fileReader = new BufferedReader(new FileReader(sourcePath));
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(destinationPath + "1." + fileExtension));
			int lineCount = 0;
			int fileCount = 1;
			String line = fileReader.readLine();
			String headLine = line;
			if (keepHeadline){
				fileWriter.write(headLine);
				fileWriter.newLine();
				line = fileReader.readLine();
			}
			while (line != null){
				lineCount += 1;
				if (lineCount < numOfLinesToRead ){
					fileWriter.write(line);
					fileWriter.newLine();
				}
				else {
					lineCount = 0;
					fileWriter.close();
					fileWriter = new BufferedWriter(new FileWriter(destinationPath + ++fileCount + "." + fileExtension));
					if (keepHeadline){
						fileWriter.write(headLine);
						fileWriter.newLine();
					}
				}
				line = fileReader.readLine();
			}
			fileReader.close();
			fileWriter.close();
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
