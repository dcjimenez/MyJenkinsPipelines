node {
	stage("Prepare working directory") {
		sh "rm -Rf *"
		sh "rm -Rf .git"
		sh "rm -Rf .gitignore"
	}
	stage("Clone git project") {
		sh "git clone ${gitRepository} -b DESARROLLO ."
		mvnHome = tool 'M3'
	}
	stage("Close version") {
		sh "'${mvnHome}/bin/mvn' build-helper:parse-version versions:set '-DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}' versions:commit"
		sh 'git add -A'
		sh 'git commit -m "Closing version"'
		sh 'git push'
		sh 'git push origin DESARROLLO:RELEASE'
	}
	stage("Deploy Closed Version") {
		sh "'${mvnHome}/bin/mvn' clean deploy -DaltDeploymentRepository=public::default::http://localhost:8085/nexus/content/repositories/releases"
	}
	stage("Increase version") {
		sh "'${mvnHome}/bin/mvn' build-helper:parse-version versions:set '-DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion}-SNAPSHOT' versions:commit"
		sh 'git add -A'
		sh 'git commit -m "Increase version"'
		sh 'git push'
	}
	stage("Deploy Increased Version") {
		sh "'${mvnHome}/bin/mvn' clean deploy -DaltDeploymentRepository=public::default::http://localhost:8081/nexus/content/repositories/snapshots"
	}
}