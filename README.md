# RemoteLightWeb

Progressive Web Application for RemoteLight

# Quick Start
### Linux
- update system `sudo apt-get update && sudo apt-get upgrade`
- install java (min Java 8) `sudo apt-get install openjdk-8-jre`
- load the [RemoteLightWeb Jar](https://github.com/Drumber/RemoteLightWeb/releases) to the computer, for example via SFTP using [WinSCP](https://winscp.net/eng/download.php).
- start RemoteLightWeb: `java -jar remotelightweb-1.0.4.jar`
- it should have automatically created a `config.properties` file in the `RemoteLightWeb` folder
- the default ports (443 and 80) can be edited in the `config.properties`
- to stop it press `Ctrl + c`
- run in background: `nohup java -jar remotelightweb-1.0.4.jar &`  
or using screen: `screen -dm -S rlweb java -jar remotelightweb-1.0.4.jar`

> Note: The program must be executed as root if the ports are `<=1024`. Therefore error messages may appear when running as non-root user. The ports can be edited in the `config.properties` to e.g. `8080` and `4443`.

#### Run on startup
- open `rc.local` file: `sudo nano /etc/rc.local`
- paste the following right before `exit 0`:  
`su - [user] -c "java -jar /path/to/remotelightweb-1.0.4.jar &"`  
e.g. as 'pi' user: `su - pi -c "java -jar /home/pi/remotelightweb-1.0.4.jar &"`
- RemoteLightWeb should now run as background process after restart

### Windows
- install [java](https://java.com) (min Java 8)
- download the [RemoteLightWeb Jar](https://github.com/Drumber/RemoteLightWeb/releases)
- open console: search cmd or press `WINDOWS + R` and type `cmd.exe`
- navigate to the directory where the RemoteLightWeb Jar is located: `cd /path/to/remotelightweb`
- start it: `java -jar remotelightweb-1.0.4.jar`
- it should have automatically created an `config.properties` in the `RemoteLightWeb` folder
- the default ports (443 and 80) can be edited in the `config.properties`
- press `Ctrl + c` to stop it

### Mac
- install [java](https://java.com) (min Java 8)
- download the [RemoteLightWeb Jar](https://github.com/Drumber/RemoteLightWeb/releases)
- open terminal and navigate to the directory where the jar is located
- start it: `java -jar remotelightweb-1.0.4.jar`
- it should have automatically created an `config.properties` in the `RemoteLightWeb` folder
- the default ports (443 and 80) can be edited in the `config.properties`
- press `Ctrl + c` to stop it

# Screenshots
#### Desktop
<img src="https://user-images.githubusercontent.com/29163322/79047660-59732880-7c18-11ea-95d4-e3e5605d81ed.png" width="440"> <img src="https://user-images.githubusercontent.com/29163322/79047724-b7a00b80-7c18-11ea-9d94-872951afe5ac.png" width="440">
#### Mobile
<img src="https://user-images.githubusercontent.com/29163322/79047787-2d0bdc00-7c19-11ea-9b28-70d80ce2399a.png" height="450"> <img src="https://user-images.githubusercontent.com/29163322/79047826-69d7d300-7c19-11ea-8de5-3e78d023d347.png" height="450">