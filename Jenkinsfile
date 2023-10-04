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
          slackSend(message: ":white_check_mark: [${env.GIT_BRANCH} #${env.BUILD_NUMBER}] Build 성공!", color: 'good')
        }

        failure {
          slackSend(message: ":x: [${env.GIT_BRANCH} #${env.BUILD_NUMBER}] Build 실패!!", color: 'danger')
        }
      }
    }

    stage('Build Docker Image') {
        steps {
            withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
              sh "echo $PASSWORD | docker login -u $USERNAME --password-stdin"
              sh "docker build -t 0ne6yte/life4cut:${env.BUILD_NUMBER} ."
              sh 'docker images'
              sh "docker push 0ne6yte/life4cut:${env.BUILD_NUMBER}"
              sh "docker rmi 0ne6yte/life4cut:$BUILD_NUMBER"
            }
        }
        post {
          success {
            slackSend(message: ":hammer_and_pick: [${env.GIT_BRANCH} #${env.BUILD_NUMBER}] Deploy 시작!")
          }
        }
    }

    stage('Deploy') {
      steps {
        sh "docker pull 0ne6yte/life4cut:$BUILD_NUMBER"
        sh "docker stop life4cut"
        sh "docker rm life4cut"
        sh "docker run --name life4cut -e SPRING_DATASOURCE_PASSWORD=${env.MYSQL_PASSWORD} -d -p 8080:8080 0ne6yte/life4cut:$BUILD_NUMBER"
      }
      post {
        success {
          slackSend(message: ":white_check_mark: [${env.GIT_BRANCH} #${env.BUILD_NUMBER}] Deploy 성공!", attachments: attachments('good', changeLogs))
        }

        failure {
          slackSend(message: ":x: [${env.GIT_BRANCH} #${env.BUILD_NUMBER}] Deploy 실패!", attachments: attachments('danger', ''))
        }
      }
    }
  }
}
