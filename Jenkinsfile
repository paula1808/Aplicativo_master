pipeline {
    agent any

    environment {
        AWS_ACCESS_KEY_ID     = credentials('aws-access-key')      // credencial tipo Secret text
        AWS_SECRET_ACCESS_KEY = credentials('aws-secret-key')      // credencial tipo Secret text
        AWS_SESSION_TOKEN = credentials('aws_session_token')
        AWS_REGION            = 'us-east-1'

        IMAGE = 'docker.io/paulagalindo/sistema-academico'
        DOCKER_CREDENTIALS_ID = 'dockerhub'
        KUBECONFIG_CREDENTIAL_ID = 'kubeconfig'
        JMETER_CMD = 'jmeter'
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
                git url: 'https://github.com/paula1808/Aplicativo_master.git', branch: 'master'
            }
        }

        stage('Build con Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test-Pruebas unitarias') {
            steps {
                echo 'Running unit tests...'
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'  // Publica los reportes de pruebas unitarias en Jenkins
                }
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
                        def image = docker.image("${IMAGE}:${BUILD_NUMBER}")
                        image.push()
                        image.push('latest') // 👈 añade esto para que haya un `:latest`
                    }
                }
            }
        }

        stage('Actualizar Deployment con tag') {
            steps {
                script {
                    sh """
                    sed -i 's|image: paulagalindo/sistema-academico:latest|image: paulagalindo/sistema-academico:${BUILD_NUMBER}|g' k8s/spring-deployment.yaml
                    """
                }
            }
        }

        stage('Configurar kubeconfig EKS') {
            steps {
                sh '''
                  mkdir -p ~/.kube
                  aws eks --region $AWS_REGION update-kubeconfig --name sistema-academico-eks
                '''
            }
        }

        stage('Desplegar ELK con Ansible') {
            steps {
                dir('ansible') {
                sh 'KUBECONFIG=~/.kube/config ansible-playbook -i inventory.ini playbook.yaml --become'
                }
            }
        }

        stage('Despliegue Kubernetes') {
            steps {
               // withCredentials([file(credentialsId: KUBECONFIG_CREDENTIAL_ID, variable: 'KUBECONFIG')]) {
                    sh '''
                    kubectl config current-context
                    kubectl apply -f k8s/mysql-deployment.yaml
                    echo "Contenido del archivo mysql-service.yaml:"
                    cat k8s/mysql-service.yaml
                    kubectl apply -f k8s/mysql-service.yaml
                    kubectl apply -f k8s/spring-deployment.yaml
                    kubectl apply -f k8s/spring-service.yaml
                    '''
                }
            
        }

        stage('Prueba de Despliegue') {
            steps {
                sh '''
                kubectl rollout status deployment/sistema-academico
                HOSTNAME=$(kubectl get svc sistema-service -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')
                
                for i in {1..24}; do
                    echo "Intentando acceder a http://$HOSTNAME (intento $i)..."
                    if curl -s --fail http://$HOSTNAME/login; then
                        echo "Aplicación disponible ✅"
                        exit 0
                    fi
                    sleep 5
                done

                echo "La aplicación no respondió tras 2 minutos ❌"
                exit 1
                '''
            }
        }
        
        stage('Obtener DNS del LoadBalancer') {
            steps {
                script {
                    def externalIp = sh(script: "kubectl get svc sistema-service -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'", returnStdout: true).trim()
                    echo "📡 DNS del LoadBalancer: http://${externalIp}"
                }
            }
        } 
        
        
    }
          

    //post {
      //  failure {
        //    mail to: 'paulagalindo1@hotmail.com',
          //       subject: "🚨 Fallo en el pipeline ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            //     body: "Revisa Jenkins para más detalles: ${env.BUILD_URL}"
        //}
    //}
}
