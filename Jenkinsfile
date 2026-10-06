pipeline {
    agent any

    tools {
        jdk 'Java17'
    }

    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        timestamps()
        timeout(time: 90, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '10'))
    }

    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'],
               description: 'Installed browser for UI tests')
        booleanParam(name: 'HEADLESS', defaultValue: true,
                     description: 'Run UI tests in headless mode')
        choice(name: 'THREAD_COUNT', choices: ['1', '2', '4', '6', '8'],
               description: 'Cucumber worker count')
        booleanParam(name: 'REGRESSION', defaultValue: false,
                     description: 'Run full UI and BDD suites instead of smoke checks')
    }

    stages {
        stage('Fresh checkout') {
            steps {
                deleteDir()
                checkout scm
            }
        }
        stage('Test suites') {
            steps {
                script {
                    def suites = params.REGRESSION ? ['api', 'ui', 'bdd'] : ['api', 'ui-smoke', 'bdd-smoke']
                    for (suite in suites) {
                        stage(suite) {
                            catchError(buildResult: 'FAILURE', stageResult: 'FAILURE', catchInterruptions: false) {
                                def arguments = "tools/run_ci.py ${suite} --browser ${params.BROWSER} " +
                                    "--headless ${params.HEADLESS} --threads ${params.THREAD_COUNT}"
                                if (isUnix()) {
                                    sh "python3 ${arguments}"
                                } else {
                                    bat "python ${arguments}"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts(artifacts: 'target/ci/**', allowEmptyArchive: true)
            junit(testResults: 'target/ci/*/surefire-reports/TEST-*.xml', allowEmptyResults: true)
            // Allure Jenkins plugin and an Allure command-line installation are required.
            allure(results: [[path: 'target/ci/*/allure-results']])
        }
    }
}
