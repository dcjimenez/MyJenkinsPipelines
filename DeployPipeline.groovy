node {
	stage("Clone") {
		git url: "${gitRepo}", branch: "${branch}", credentialsId: "bitBucketAut"
	}
	stage("Sonar") {
		sh "${env.SONAR_SCANNER_HOME}/bin/sonar-scanner -Dsonar.projectKey=WEB_Autoclickrentacar -Dsonar.projectName=WEB_Autoclickrentacar -Dsonar.sources=. -Dsonar.host=http://localhost:9000"
	}
	stage("Despliegue") {
		sh 'ssh staging.autoclickrentacar.com "rm -Rf /var/www/*"'
		sh 'ssh staging.autoclickrentacar.com "rm -Rf /var/www/.git"'
		sh 'scp -rp * staging.autoclickrentacar.com:/var/www'
	}
	stage("Limpieza de workspace") {
		sh 'rm -Rf /home/ubuntu/Jenkins/jenkins_home/workspace/WEB_Autoclickrentacar_SCP_Pipeline/*'
	}
}