package de.dhbw.grails.openData;

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService

import javax.annotation.PostConstruct
/**
 *
 * @author Marco K.
 *
 */
@Singleton
public class ConfigScanner {
	public static final String confPath = "C:\\TEMP\\alumnetConfig"
	public static final String fileP = "\\db_properties"

	public static final Path myDir = Paths.get(ConfigScanner.confPath)

	//	public static final String confPath = "/opt/wikidata/config"
	//	public static final String fileP = "/db_properties"


	private final File fileName;
	private final static Charset ENCODING = StandardCharsets.UTF_8;
	static boolean configfileexists = true;

	@PostConstruct
	void init(){
		if(configfileexists) {

			// Read db settings from file
			String actualPath = myDir.toString() + ConfigScanner.fileP
			log.info "Config Scanner has Path: " + actualPath

			try {
				filePath = new File(fileName)
				log.info "File Path is now: " + filePath
				try {
					processLineByLine()
				} catch (IOException e) {
					log.error "Problem reading File", e
					return
				}
			}
			catch(NoSuchFileException | FileNotFoundException e) {
				configfileexists = false;
				log.error "File Not found: " + fileName, e
			}
		}

		while(ConfigScanner.configfileexists) {
			try {
				// Create a watcher for changes in file system
				WatchService watcher = myDir.getFileSystem().newWatchService()
				myDir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY)
				log.info "watchservice registered"

				WatchKey watckKey = watcher.take()

				List<WatchEvent<?>> events = watckKey.pollEvents()
				for (WatchEvent event : events) {

					// Changes in file "DB_PROPERTIES"? --> read file
					if (event.context().toString().equalsIgnoreCase("DB_PROPERTIES")) {
						log.info "reading updated file: "
						// Read db settings from file
						try {
							processLineByLine()
						} catch (IOException e) {
							log.error "Problem reading File", e
							return
						}
						Database.instance.rebuildConnection()
					}
				}
			} catch (Exception e) {
				log.error "Something went wrong:", e
				return
			}
		}
	}

	public File getFilePath() {
		return filePath;
	}

	/** Template method that calls {@link #processLine(String)}. */
	public final void processLineByLine() throws IOException {
		log.info "processLineByLine() called"
		String line = "";
		if (this.configfileexists){
			try {
				Scanner scanner = new Scanner(filePath, ENCODING.name())
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if (!line.equals("") && !line.substring(0, 1).equals("#")) {
						processLine(line);
					}
				}
			} catch (Exception e) {
				log.error "couldnt instanciate scanner"
			}
		}
	}

	/**
	 * Overridable method for processing lines in different ways.
	 */
	protected void processLine(String line) {
		log.info "processLine() called"
		Scanner scanner = new Scanner(line);
		scanner.useDelimiter("=");
		if (scanner.hasNext()) {
			String property = scanner.next();
			String value = scanner.next();

			property = property.trim();
			value = value.trim();

			switch (property) {
				case "DATABASE_PATH":
					Database.setDatabasePath(value);
					log.info "set new dbpath: " + value
					break;
				case "DB_USERNAME":
					Database.setDatabaseUsername(value);
					log.info "set new dbuser: " + value
					break;
				case "DB_PASSWORD":
					Database.setDatabasePassword(value);
					log.info "set new dbpw: *** "
					break;
				case "SCHEMA":
					Database.setDatabaseSchema(value);
					log.info "set new dbschema: " + value
					break;
			}
		} else {
			log.info "Empty or invalid line. Unable to process."
		}
		scanner.close();
		log.info "scanner closed"
	}
}