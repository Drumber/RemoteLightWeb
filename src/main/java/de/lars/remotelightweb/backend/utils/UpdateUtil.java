package de.lars.remotelightweb.backend.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.system.ApplicationHome;
import org.tinylog.Logger;

import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingString;
import de.lars.remotelightcore.utils.DirectoryUtil;
import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.updater.sites.GitHubParser;
import de.lars.updater.utils.FileDownloader;

public class UpdateUtil {
	
	public final String API_URL = "https://api.github.com/repos/Drumber/RemoteLightWeb/releases";
	private final String UPDATER_NAME = "updater.jar";
	private final String RUNNER_CLASSPATH = "runner.sh";
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
		boolean linux = System.getProperty("os.name").toLowerCase().contains("linux");
		
		String rootDir = DirectoryUtil.getRootPath();
		rootDir = Paths.get(".").toAbsolutePath().normalize().toString();
		
		//download updater jar
		File updaterFile = downloadUpdater(rootDir);
		
		//copy runner script from classpath
		boolean runnerExists = copyRunnerScript(rootDir);
		
		// execute updater
		String args = "-cv " + RemoteLightWeb.VERSION + " -o \"" + jarDir.getAbsolutePath() +
					"\" -u " + API_URL + " -w -cmd \"";
		
		if(shutdown) {
			String shutdownCmd = ((SettingString) RemoteLightWeb.getInstance().getCore().getSettingsManager().getSettingFromId("rlweb.shutdowncmd")).getValue();
			if(shutdownCmd != null && !shutdownCmd.isEmpty()) {
				args += shutdownCmd;
			} else {
				args += "shutdown -h now";
			}
		} else {
			String runCmd = ((SettingString) RemoteLightWeb.getInstance().getCore().getSettingsManager().getSettingFromId("rlweb.runcmd")).getValue();
			if(runCmd == null || runCmd.isEmpty()) {
				runCmd = "nothing";
			}
			args += runCmd;
		}
		args += "\"";
		
		String command;
		if(linux) {
			if(((SettingBoolean) RemoteLightWeb.getInstance().getCore().getSettingsManager().getSettingFromId("rlweb.updater.screen")).getValue()) {
				command = String.format("screen -dm -S rlwupdater java -jar %s %s", updaterFile.getAbsoluteFile(), args);	// run updater in new screen
			} else {
				command = String.format("java -jar %s %s", updaterFile.getAbsoluteFile(), args);
			}
		} else {
			command = String.format("java -jar %s %s", updaterFile.getAbsoluteFile(), args);
		}
		
		String[] cmd;
		if(linux) {
			// run in background process
			if(runnerExists) { // ...using runner script
				// https://stackoverflow.com/a/7665834/12821118
				cmd = new String[] {"/bin/bash", "-c", "sh runner.sh " + command};
			} else { // ...directly
				cmd = new String[] {"nohup", command, "&"};
			}
		} else {
			cmd = new String[] {command};
		}
		
		Logger.info("Run Updater with the following command: " + String.join(" ", cmd));
		try {
			Runtime.getRuntime().exec(cmd);
		} catch(Exception e) {
			System.out.println("Error while executing command " + String.join(" ", cmd));
			e.printStackTrace();
			Logger.error(e);
		}
		
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
	
	private boolean copyRunnerScript(String rootDir) {
		String dir = rootDir + File.separator + RUNNER_CLASSPATH;
		File file = new File(dir);
		if(!file.exists()) {
			try {
				InputStream input = getClass().getClassLoader().getResourceAsStream(RUNNER_CLASSPATH);
				Files.copy(input, new File(dir).toPath());
				
				// add execute permission
				Set<PosixFilePermission> perms = new HashSet<>();
				perms.add(PosixFilePermission.OWNER_EXECUTE);
				perms.add(PosixFilePermission.OWNER_READ);
				perms.add(PosixFilePermission.OWNER_WRITE);
				Files.setPosixFilePermissions(file.toPath(), perms);
				
			} catch (IOException e) {
				Logger.error(e, "Could not copy runner script from classpath.");
				return false;
			}
		}
		return true;
	}

}
