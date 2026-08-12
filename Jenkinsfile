pipeline {
    agent any

    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Browser for UI tests'
        )

        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run UI tests in headless'
        )
        
        choice(
        	name: 'THREAD_COUNT',
        	choices: ['4', '1', '2', '6', '8'],
        	description: 'Number of parallel test threads'
    	)
    }

    environment {
        PATH = "/opt/homebrew/bin:${env.PATH}"
    }

    stages {
	    stage('API Tests') {
	        steps {
	            sh """
	                mvn test \
	                    -DtestGroups=api \
	                    -DthreadCount=${params.THREAD_COUNT}
	            """
	        }
	    }
	    
        stage('Test') {
            steps {
                sh """
                    mvn clean test \
                    	-DtestGroups=ui \
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