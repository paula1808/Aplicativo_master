pipeline {
    agent any

    environment {
        AWS_ACCESS_KEY_ID     = credentials('aws-access-key')      // credencial tipo Secret text
        AWS_SECRET_ACCESS_KEY = credentials('aws-secret-key')      // credencial tipo Secret text
        AWS_SESSION_TOKEN = credentials('aws-session-token')
        AWS_REGION            = 'us-east-1'

        IMAGE = 'docker.io/paulagalindo/sistema-academico'
        DOCKER_CREDENTIALS_ID = 'dockerhub'
        KUBECONFIG_CREDENTIAL_ID = 'kubeconfig'
    }

    stages {
        
        stage('Terraform Init') {
            steps {
                dir('terraform') {
                    sh 'terraform init'
                }
            }
        }

        stage('Terraform Plan') {
            steps {
                dir('terraform') {
                    sh 'terraform plan -out=tfplan'
                }
            }
        }

        stage('Terraform Apply') {
            steps {
                dir('terraform') {
                    sh 'terraform apply -auto-approve tfplan'
                }
            }
        }
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

        sstage('Configurar kubeconfig EKS') {
            steps {
                sh '''
                  mkdir -p ~/.kube
                  aws eks --region $AWS_REGION update-kubeconfig --name sistema-academico-eks
                '''
            }
        }

       // ('Despliegue con Ansible') {
         //   steps {
           //     dir('ansible') {
             //       sh 'ansible-playbook -i inventory.ini playbook.yaml'
               // }
            //}
        //}

        stage('Despliegue Kubernetes') {
            steps {
                withCredentials([file(credentialsId: KUBECONFIG_CREDENTIAL_ID, variable: 'KUBECONFIG')]) {
                    sh '''
                    kubectl config use-context kubernetes-admin@kubernetes
                    kubectl set image deployment/app-deployment app=${IMAGE}:${BUILD_NUMBER} --namespace=default
                    kubectl apply -f k8s/mysql-deployment.yaml
                    kubectl apply -f k8s/mysql-service.yaml
                    kubectl apply -f k8s/spring-deployment.yaml
                    kubectl apply -f k8s/spring-service.yaml
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
