pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'paulagalindo/sistema-academico'
        DOCKER_CREDENTIALS_ID = 'dockerhub'
        KUBECONFIG_CREDENTIAL_ID = 'kubeconfig'
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/paula1808/Aplicativo_master.git', branch: 'main'
            }
        }

        stage('Build Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${BUILD_NUMBER}")
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKER_CREDENTIALS_ID) {
                        docker.image("${DOCKER_IMAGE}:${BUILD_NUMBER}").push()
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: KUBECONFIG_CREDENTIAL_ID, variable: 'KUBECONFIG')]) {
                    sh '''
                    kubectl set image deployment/app-deployment app=${DOCKER_IMAGE}:${BUILD_NUMBER} --namespace=default
                    '''
                }
            }
        }
    }

    post {
        failure {
            mail to: 'paulagalindo1@hotmail.com',
                 subject: "🚨 Falla en Pipeline ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "El pipeline falló. Revisa Jenkins para más detalles."
        }
    }
}
