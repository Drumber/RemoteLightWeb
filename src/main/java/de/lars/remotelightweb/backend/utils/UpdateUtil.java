package de.lars.remotelightweb.backend.utils;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Paths;

import org.springframework.boot.system.ApplicationHome;
import org.tinylog.Logger;

import de.lars.remotelightclient.settings.types.SettingString;
import de.lars.remotelightclient.utils.DirectoryUtil;
import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.updater.sites.GitHubParser;
import de.lars.updater.utils.FileDownloader;

public class UpdateUtil {
	
	public final String API_URL = "https://api.github.com/repos/Drumber/RemoteLightWeb/releases";
	private final String UPDATER_NAME = "updater.jar";
	private GitHubParser parser;
	
	public UpdateUtil(String currentVersion) {
		parser = new GitHubParser(currentVersion, API_URL);
	}
	
	public void check() {
		try {
			parser.check();
		} catch (Exception e) {
			Logger.error(e, "Error while checking for updates.");
		}
	}
	
	public GitHubParser getParser() {
		return parser;
	}
	
	
	/**
	 * Download latest release and replace current jar file
	 * <!> This method will EXIT the application!
	 * @param shutdown Should the system be shut down after the update?
	 * @throws Exception 
	 */
	public void install(boolean shutdown) throws Exception {
		File jarDir = new ApplicationHome(RemoteLightWeb.class).getSource();
		
		String rootDir = DirectoryUtil.getRootPath();
		rootDir = Paths.get(".").toAbsolutePath().normalize().toString();
		
		//download updater jar
		File updaterFile = downloadUpdater(rootDir);
		
		// execute updater
		String args = "-cv " + RemoteLightWeb.VERSION + " -o \"" + jarDir.getAbsolutePath() +
					"\" -u " + API_URL + " -w -cmd \"";
		
		if(shutdown) {
			args += "\"shutdown -h now\"";
		} else {
			String runCmd = ((SettingString) RemoteLightWeb.getInstance().getAPI().getSettingsManager().getSettingFromId("rlweb.runcmd")).getValue();
			if(runCmd == null || runCmd.isEmpty()) {
				runCmd = "\"java -jar " + jarDir.getAbsolutePath() + "\"";
			}
			args += runCmd;
		}
		args += "\"";
		
		String command = String.format("java -jar %s %s", updaterFile.getAbsoluteFile(), args);
		Logger.info("Run Updater with the following command: " + command);
		Runtime.getRuntime().exec(command);
		
		RemoteLightWeb.exitApplication();
	}
	
	private File downloadUpdater(String rootDir) throws Exception {
		File updaterFile = new File(rootDir + File.separator + UPDATER_NAME);
		if(!updaterFile.exists()) {
			GitHubParser parser = new GitHubParser("0", "Drumber", "Updater");
			try {
				
				parser.check();
				Logger.info("Downloading Uploader version " + parser.getNewestVersionTag() +" to " + updaterFile.getAbsolutePath());
				FileDownloader downloader = new FileDownloader(parser.getNewestDownloadUrl(), updaterFile.getAbsolutePath());
				if(downloader.isDownloadSuccessful()) {
					return updaterFile;
				}
				
			} catch (Exception e) {
				throw new Exception("Could not download Updater.", e);
			}
			return null;
		}
		return updaterFile;
	}

}
