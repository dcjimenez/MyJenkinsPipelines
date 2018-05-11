import groovy.json.JsonSlurper;
import groovy.json.JsonOutput;

node {
	def version;
	def closedVersion;
	def increasedVersion;
	stage("Git clone from develop") {
	   git url: "${gitRepo}", branch: "develop", credentialsId: "dcjimenez"
	}
	stage("Prepare version") {
		File versionFile = new File("${env.WORKSPACE}/version.json")
		if(versionFile.exists()) {
			JsonSlurper sl = new JsonSlurper();
			Map versionJson = (Map)sl.parseText(versionFile.getText())
			version = versionJson.getAt("version");
			closedVersion = version.split("-SNAPSHOT")[0]
			def increasedDigit = Integer.valueOf(closedVersion.split("\\.")[1]) +1 ;
			increasedVersion = closedVersion.split("\\.")[0] + "." + increasedDigit + "." + closedVersion.split("\\.")[2] + "-SNAPSHOT";
			println("Increased Version: ${increasedVersion}")
			println("Closed Version: ${closedVersion}")
		}
		else {
			throw new Exception("No hay archivo de version definido en el proyecto.")
		}
	}
	stage("Close version") {
		File versionFile = new File("${env.WORKSPACE}/version.json")
		JsonSlurper sl = new JsonSlurper();
		Map versionJson = (Map)sl.parseText(versionFile.getText())
		versionJson.putAt("version", "${closedVersion}")
		def newJson = JsonOutput.prettyPrint(JsonOutput.toJson(versionJson));
		println(newJson);
		versionFile.setText(newJson);
	}
	stage("Push closed version to master branch") {
		withCredentials([sshUserPrivateKey(credentialsId: 'dcjimenez')]) {
			sh("git add -A")
			sh('git status')
			sh('git commit -m "Subida de version cerrada"')
			sh('git push --set-upstream origin develop')
			sh("git push")
			sh('git tag -a "Version cerrada" -m "Cerrado de version desde proceso de release"')
			sh('git push origin --tags')
		}
	}
	stage("Compile and genetate closed version") {
		
	}
	stage("Increase snapshot version") {
		
	}
	stage("Push snapshot version to develop branch") {
		
	}
}