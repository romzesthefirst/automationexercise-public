pipeline {
    agent any

    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Browser'
        )

        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run headless'
        )
        
        choice(
        	name: 'THREAD_COUNT',
        	choices: ['1', '2', '4', '6', '8'],
        	description: 'Number of parallel test threads'
    	)
    }

    environment {
        PATH = "/opt/homebrew/bin:${env.PATH}"
    }

    stages {
        stage('Test') {
            steps {
                sh """
                    mvn clean test \
                        -Dbrowser=${params.BROWSER} \
                        -Dheadless=${params.HEADLESS} \
                        -DthreadCount=${params.THREAD_COUNT}
                """
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