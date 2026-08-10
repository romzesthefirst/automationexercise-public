pipeline {
    agent any

    environment {
        PATH = "/opt/homebrew/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Environment') {
            steps {
                sh '''
                    java -version
                    mvn -version
                    "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" --version
                '''
            }
        }

        stage('Tests') {
            steps {
                sh 'mvn clean test -Dheadless=true'
            }
        }
    }

    post {
        always {
            allure([
                results: [[path: 'target/allure-results']]
            ])
        }
    }
}
