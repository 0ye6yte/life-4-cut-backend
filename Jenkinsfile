def attachments(color, changes) {
  [
    [
      'title': "#${env.BUILD_NUMBER} Build URL",
      'title_link': env.BUILD_URL,
      'color': color,
      'fields': [
        [
          'title': 'Branch',
          'value': env.GIT_BRANCH
        ],
        [
          'title': 'Changes',
          'value': changes
        ]
      ]
    ]
  ]
}

pipeline {
  agent any
  stages {
    stage('Prepare') {
      steps {
        echo 'prepare'
        sh 'env'
      }
      post {
        always {
          slackSend(message: ":hammer_and_pick: [${env.GIT_BRANCH} #${env.BUILD_NUMBER}] Build 시작!")
        }
      }
    }

    stage('Bulid') {
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
    }
  }
}
