import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;

public class Driver {

	public static void main(String[] args) {
		

		System.out.println("Hello! This is the Beat Saber Custom Song Converter!");

		String os = System.getProperty("os.name");
		String username = System.getProperty("user.name");
		File downloads;
		if (os.equals("Linux")) {
			downloads = new File("//home//" + username + "//Downloads");
		} else if (os.equals("Windows")) {
			downloads = new File("C://Users//" + username + "//Downloads");
		} else

			do {
				System.out.println("Uh oh, we couldn't find your downloads folder(we suck)");
				System.out.println(
						"Go ahead and copy and paste in the path to the folder/nwhich holds your songs and press enter.");
				Scanner scan = new Scanner(System.in);
				String answer = scan.nextLine();
				downloads = new File(answer);
				scan.close();
			} while (!downloads.exists());

		System.out.println("Found Downloads folder at:");// testing
		System.out.println(downloads);// testing
		System.out.println("downloads folder exists: " + downloads.exists());// testing
		System.out.println();// testing

		ArrayList<File> validDirectories = new ArrayList<File>();
		ArrayList<File> validZippedFolders = new ArrayList<File>();

		for (File file : downloads.listFiles()) {
			if (file.isDirectory()) {
				if (Driver.containsSongs(file))
					validDirectories.add(file);
			} else if (UnzipUtility.isZipFile(file)) {
				if (UnzipUtility.testZippedBeatSaberSong(file))
					validZippedFolders.add(file);
			}
		}

		System.out.println();
		System.out.println("Found these directories with Songs in them");
		for (File f : validDirectories)
			System.out.println(f.getName());
		System.out.println();

		System.out.println("Found these zipped folders with Songs in them");
		for (File f : validZippedFolders) {
			//System.out.println(f.getName());
			try {
				System.out.println(downloads.toString()+"//"+f.getName().substring(0,f.getName().length()-4));
				File unzippedSong = new File(downloads.toString()+"//"+f.getName().substring(0,f.getName().length()-4));
				UnzipUtility.unzip(f.toString(), unzippedSong.toString());
				validDirectories.add(unzippedSong);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		System.out.println();

		System.out.println("Extracting zipped folders");
		System.out.println();
		File customSongsFolder = new File("C:\\Program Files\\Oculus\\Software\\Software\\hyperbolic-magnetism-beat-saber\\Beat Saber_Data\\CustomLevels");
		File modifiedSongs;
		if(!customSongsFolder.exists())
		modifiedSongs = new File(downloads.toString() + "//ModifiedBeatSaberSongs");
		else		
			modifiedSongs = customSongsFolder;

			
		if (modifiedSongs.exists()) {
			System.out.println("Found a folder for songs at:\n"+modifiedSongs);
		} else if (modifiedSongs.mkdir()) {
			System.out.println("Proceeding to create a folder at:");
			System.out.println(modifiedSongs);
		} else
			System.out.println("We couldn't make the folder and we couldn't find one either...");

		for (File srcFolder : validDirectories) {
			try {
				File destinationFolder = new File(modifiedSongs + "//" + srcFolder.getName());
				Driver.copyFolder(srcFolder, destinationFolder);
				System.out.println();

				File info = new File(destinationFolder + "//info.dat");
				Scanner infoScanner = new Scanner(info);
				infoScanner.useDelimiter(",");

				ArrayList<File> difficulties = new ArrayList<File>();
				while (infoScanner.hasNext()) {
					String token = infoScanner.next();
					String[] split = token.split(":");
					split[0] = split[0].trim();
					if (split[0].equals("\"_beatmapFilename\"")) {

						split[1] = split[1].trim();
						File toAdd = new File(destinationFolder + "//" + split[1].substring(1, split[1].length() - 1));
						difficulties.add(toAdd);

					}
					
				}
				infoScanner.close();

				System.out.println("modifying:");
				for (File toModify : difficulties) {
					System.out.println(toModify);
					Scanner convertSongToText = new Scanner(toModify);
					convertSongToText.useDelimiter("//Z");
					String levelText = convertSongToText.next();
					FileWriter modifier = new FileWriter(toModify, false);
					modifier.append(SongConverter.convertSong(levelText));
					modifier.close();
					convertSongToText.close();
					
				}
				System.out.println("\n");

			} catch (Exception e) {
				System.out.println("Could not find info.dat\n" + e);

			}
		}

		System.out.println("Modified files can now be found in //Download//BeatSaberModifiedSongs.");
		System.out.println("Would you like to delete the source directories? (yes or no)");
		Scanner scan = new Scanner(System.in);
		String answer = scan.nextLine();
		while (!(answer.contentEquals("yes") || answer.contentEquals("no"))) {
			System.out.println("Say yes or no, try again.");
			answer = scan.nextLine();
		}
		scan.close();
		if (answer.contentEquals("yes")) {
			for (File toDelete : validDirectories) {
				try {

					Driver.delete(toDelete);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Files not deleted.");

		}

		System.out.println("Process completed without errors.");
	}

	private static boolean containsSongs(File directory) {
		for (File f : directory.listFiles()) {
			if (f.getName().equals("info.dat")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This function recursively copy all the sub folder and files from sourceFolder
	 * to destinationFolder
	 */
	private static void copyFolder(File sourceFolder, File destinationFolder) throws IOException {
		// Check if sourceFolder is a directory or file
		// If sourceFolder is file; then copy the file directly to new location
		if (sourceFolder.isDirectory()) {
			// Verify if destinationFolder is already present; If not then create it
			if (!destinationFolder.exists()) {
				destinationFolder.mkdir();
				System.out.println("Directory created :: " + destinationFolder);
			}

			// Get all files from source directory
			String files[] = sourceFolder.list();

			// Iterate over all files and copy them to destinationFolder one by one
			for (String file : files) {
				File srcFile = new File(sourceFolder, file);
				File destFile = new File(destinationFolder, file);

				// Recursive function call
				copyFolder(srcFile, destFile);
			}
		} else {
			// Copy the file content from one place to another
			Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
			// System.out.println("File copied :: " + destinationFolder);
		}
	}

	public static void delete(File file) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();
				System.out.println("Directory is deleted : " + file.getAbsolutePath());

			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
					System.out.println("Directory is deleted : " + file.getAbsolutePath());
				}
			}

		} else {
			// if file, then delete it
			file.delete();
			System.out.println("File is deleted : " + file.getAbsolutePath());
		}
	}
}