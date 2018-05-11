/*node {
 stage("Levantar Instancia Windows EC2") {
	 def parameters = []
	 parameters.add([$class: 'StringParameterValue', name: 'action', value: 'startup'])
	 build job: 'Manage_EC2_Instances', parameters: parameters
 }
}*/

node("${node}") {
 stage("Clone") {
	 git url: "${gitRepo}", branch: "${branch}", credentialsId: "bitBucketAut"
 }
 stage("Compile") {
	 bat "build.bat"
	 archiveArtifacts artifacts: '*.exe'
 }
 stage("Upload to Nexus") {
	 bat "\"${tool 'Maven 3'}\"/bin/mvn deploy:deploy-file -DgroupId=${groupId} -DartifactId=${artifactId} -Dversion=${version} -Dpackaging=${packaging} -Dfile=${file} -DrepositoryId=maven-releases -Durl=http://10.1.75.200:8081/repository/maven-releases"
 }
}

/*node {
 stage("Apagar Instancia Windows EC2") {
	 def parameters = []
	 parameters.add([$class: 'StringParameterValue', name: 'action', value: 'shutdown'])
	 build job: 'Manage_EC2_Instances', parameters: parameters
 }
}*/