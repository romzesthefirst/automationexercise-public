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
        booleanParam(name: 'ADS_HANDLING', defaultValue: true,
                     description: 'Enable advertisement handling (ads.handling.enabled)')
        choice(name: 'THREAD_COUNT', choices: ['4', '1', '2', '6', '8'],
               description: 'Cucumber worker count')
        booleanParam(name: 'SMOKE', defaultValue: false,
                     description: 'Run only API, UI and BDD smoke tests')
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
                    def suites = params.SMOKE ? ['api-smoke', 'ui-smoke', 'bdd-smoke'] : ['api', 'ui', 'bdd']
                    for (suite in suites) {
                        stage(suite) {
                            catchError(buildResult: 'FAILURE', stageResult: 'FAILURE', catchInterruptions: false) {
                                def arguments = "tools/run_ci.py ${suite} --browser ${params.BROWSER} " +
                                    "--headless ${params.HEADLESS} --threads ${params.THREAD_COUNT}"
                                withEnv(["AE_ADS_HANDLING_ENABLED=${params.ADS_HANDLING}"]) {
                                    if (isUnix()) {
                                        if (params.BROWSER == 'firefox' && !suite.startsWith('api') &&
                                                sh(script: 'uname -s', returnStdout: true).trim() == 'Darwin') {
                                            // Preserve Firefox's app-data identity through macOS LaunchServices.
                                            withEnv([
                                                "JAVA_TOOL_OPTIONS=${env.JAVA_TOOL_OPTIONS ?: ''} \"-Dwebdriver.firefox.bin=${pwd()}/bin/firefox-macos\""
                                            ]) {
                                                sh "python3 ${arguments}"
                                            }
                                        } else {
                                            sh "python3 ${arguments}"
                                        }
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
