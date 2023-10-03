pipeline {
  agent any
  stages {
    stage('Prepare') {
      post {
        always {
          slackSend(message: ":hammer_and_pick: [${env.GIT_BRANCH} #${env.BUILD_NUMBER}] Build 시작!")
        }

      }
      steps {
        echo 'prepare'
        sh 'env'
      }
    }

    stage('Bulid') {
      post {
        always {
          junit 'build/test-results/**/*.xml'
        }

        success {
          slackSend(message: ":white_check_mark: [${env.GIT_BRANCH} #${env.BUILD_NUMBER}] Build 성공!", attachments: attachments('good', changeLogs))
        }

        failure {
          slackSend(message: ":x: [${env.GIT_BRANCH} #${env.BUILD_NUMBER}] Build 실패!!", attachments: attachments('danger', ''))
        }

      }
      steps {
        dir(path: '.') {
          script {
            previousCommit = env.GIT_PREVIOUS_SUCCESSFUL_COMMIT ?: env.GIT_PREVIOUS_COMMIT
            echo previousCommit
            changeLogs = sh(script: "git log --pretty=format:'%h - %s (%an)' ${previousCommit}..${env.GIT_COMMIT}", returnStdout: true).trim()
            echo changeLogs

            sh './gradlew clean build'
          }

        }

      }
    }

  }
}
