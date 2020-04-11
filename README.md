# RemoteLightWeb

Progressive Web Application for RemoteLight

# Quick Start
### Linux
- install sudo if not already installed `apt-get install sudo`
- update system `sudo apt-get update && apt-get upgrade`
- install java (min Java 8) `sudo apt-get install openjdk-8-jre`
- load the [RemoteLightWeb Jar](https://github.com/Drumber/RemoteLightWeb/releases) on your computer, for example via SFT using [WinSCP](https://winscp.net/eng/download.php).
- start RemoteLightWeb: `java -jar remotelightweb-0.0.2.jar`
- it should have automatically created an `config.properties` in the `RemoteLightWeb` folder
- the default ports (443 and 80) can be edited in the `config.properties`
- stop it: press `Ctrl + c`
- run in background: `nohup java -jar remotelightweb-0.0.2.jar &`  
or using screen: `screen -dm -S rlweb java -jar remotelightweb-0.0.2.jar`

### Windows
- install [java](https://java.com) (min Java 8)
- download the [RemoteLightWeb Jar](https://github.com/Drumber/RemoteLightWeb/releases)
- open console: search cmd or press `WINDOWS + R` and type `cmd.exe`
- navigate to the directory where the RemoteLightWeb Jar is located: `cd /path/to/remotelightweb`
- start it: `java -jar remotelightweb-0.0.2.jar`
- it should have automatically created an `config.properties` in the `RemoteLightWeb` folder
- the default ports (443 and 80) can be edited in the `config.properties`
- stop it: press `Ctrl + c`

### Mac
I don't know, just try to install java and run the RemoteLightWeb jar... `¯\_(ツ)_/¯`

# Screenshots
#### Desktop
<img src="https://user-images.githubusercontent.com/29163322/79047660-59732880-7c18-11ea-95d4-e3e5605d81ed.png" width="440"> <img src="https://user-images.githubusercontent.com/29163322/79047724-b7a00b80-7c18-11ea-9d94-872951afe5ac.png" width="440">
#### Mobile
<img src="https://user-images.githubusercontent.com/29163322/79047787-2d0bdc00-7c19-11ea-9b28-70d80ce2399a.png" height="450"> <img src="https://user-images.githubusercontent.com/29163322/79047826-69d7d300-7c19-11ea-8de5-3e78d023d347.png" height="450">