pipeline {
    agent any

    environment {
        IMAGE = 'docker.io/paulagalindo/sistema-academico'
        DOCKER_CREDENTIALS_ID = 'dockerhub'
        KUBECONFIG_CREDENTIAL_ID = 'kubeconfig'
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/paula1808/Aplicativo_master.git', branch: 'main'
            }
        }

        stage('Build con Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Construcción Docker') {
            steps {
                script {
                    docker.build("${IMAGE}:${BUILD_NUMBER}")
                }
            }
        }

        stage('Push Docker Hub') {
            steps {
                script {
                    docker.withRegistry('', DOCKER_CREDENTIALS_ID) {
                        docker.image("${IMAGE}:${BUILD_NUMBER}").push()
                    }
                }
            }
        }

        stage('Despliegue Kubernetes') {
            steps {
                withCredentials([file(credentialsId: KUBECONFIG_CREDENTIAL_ID, variable: 'KUBECONFIG')]) {
                    sh '''
                    kubectl config use-context kubernetes-admin@kubernetes
                    kubectl set image deployment/app-deployment app=${IMAGE}:${BUILD_NUMBER} --namespace=default
                    '''
                }
            }
        }
    }

    post {
        failure {
            mail to: 'paulagalindo1@hotmail.com',
                 subject: "🚨 Fallo en el pipeline ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Revisa Jenkins para más detalles: ${env.BUILD_URL}"
        }
    }
}
