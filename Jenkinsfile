def updateGitlabStatus(Map args) {
  def payload = "{\"state\": \"${args.state}\", \"context\": \"${STAGE_NAME}\", \"target_url\": \"${BUILD_URL}\"}"
  def response = httpRequest url: "${GITLAB_ENDPOINT}/projects/${project_id}/statuses/${GIT_COMMIT}",
    httpMode: 'POST',
    contentType: 'APPLICATION_JSON',
    customHeaders: [[name: 'PRIVATE-TOKEN', value: GITLAB_TOKEN]],
    requestBody: payload
}

pipeline {
  agent {
    label 'docker'
  }
  
  options {
    buildDiscarder(logRotator(numToKeepStr: '15'))
    disableConcurrentBuilds()
    office365ConnectorWebhooks([[
      url: 'https://consultoriatecnicanexus.webhook.office.com/webhookb2/c2eb9f32-f28c-4dd2-9df4-871a9164263a@6e3ac0ea-07d3-40ea-ae60-add05d8566dd/JenkinsCI/1f18451c6a0d4e788ee293c26295636a/cf1b295e-563a-463a-87ad-4bc65a852a15',
      startNotification: false,
      notifySuccess: false,
      notifyFailure: false
    ]])
  }

  environment {
    // GitLab
    GITLAB_ENDPOINT = 'http://nexusgit/api/v4'
    GITLAB_TOKEN = credentials('jenkins-token-string')

    // Maven
    MAVEN_CLI_OPTS = '-s .m2/settings.xml --batch-mode'
    MAVEN_REPO_USER = 'nexus'
    MAVEN_REPO_PASS = 'password'
    
    // Sonar
    SONAR_LOGIN = credentials('TokenSONARQUBE')
    
    // Docker (build image & push)
    DOCKER_REGISTRY = 'registry.nexusgeographics.com'
    DOCKER_PROJECT = 'cityos'
    DOCKER_IMAGE = 'inventory'
    DOCKER_IMAGE_URL = "${DOCKER_REGISTRY}/${DOCKER_PROJECT}/${DOCKER_IMAGE}"
    DOCKER_REGISTRY_CREDENTIALS = credentials('registry-pwd')
    DOCKER_DEV_TAG = 'dev'
  }
  
  triggers {
    GenericTrigger(
      token: 'cityos-inventory',
      genericVariables: [
          [expressionType: 'JSONPath', key: 'event_name', value: '$.event_name', defaultValue: ''],
          [expressionType: 'JSONPath', key: 'git_repo_url', value: '$.project.git_http_url', defaultValue: ''],
          [expressionType: 'JSONPath', key: 'project_id', value: '$.project.id', defaultValue: ''],
          [expressionType: 'JSONPath', key: 'project_name', value: '$.project.name', defaultValue: ''],
          [expressionType: 'JSONPath', key: 'project_namespace', value: '$.project.path_with_namespace', defaultValue: ''],
          [expressionType: 'JSONPath', key: 'user_name', value: '$.user_name', defaultValue: ''],
          [expressionType: 'JSONPath', key: 'user_username', value: '$.user_username', defaultValue: '']
        ],
      causeString: 'Commit "$git_repo_url" by "$user_username"',
    )
  }

  stages {
    // Stage A.
    stage('A. Show setup') {
      steps {
        updateGitlabStatus(state: 'running')
        sh 'printenv'
        echo '---------------------------------------------------------------\n'
        echo "Workspace directory is ${workspace}"
        echo 'Pipeline variables: \n' +
          "\tgit_repo_url: ${git_repo_url}\n" +
          "\tproject_name: ${project_name}\n" +
          "\tproject_id: ${project_id}\n" +
          "\tlast_commit_sha: ${GIT_COMMIT}\n" +
          "\tuser_name: ${user_name}\n"
      }
      post {
        success { updateGitlabStatus(state: 'success') }
        failure { updateGitlabStatus(state: 'failed') }
      }
    }

    // Stage B.
    stage('B. Checkout code') {
      steps {
        updateGitlabStatus(state: 'running')
        checkout([
          $class: 'GitSCM',
          branches: [[name: '*/master']],
          changelog: false,
          doGenerateSubmoduleConfigurations: false,
          submoduleCfg: [],
          userRemoteConfigs: [
            [credentialsId: 'jenkinsciuser', url: "${git_repo_url}"]
          ],
          poll: false
        ])
      }
      post {
        success { updateGitlabStatus(state: 'success') }
        failure { updateGitlabStatus(state: 'failed') }
      }
    }

    // Stage C.
    stage('C. Initialize environment') {
      steps {
        updateGitlabStatus(state: 'running')
        script {
          sh '''
            mvn -v
            mvn clean
          '''
        }
      }

      post {
        success { updateGitlabStatus(state: 'success') }
        failure { updateGitlabStatus(state: 'failed') }
      }
    }

    // Stage D.
    stage('D. Build code') {
      steps {
        updateGitlabStatus(state: 'running')
        script {
          sh "mvn compile"
          sh "mvn package -DskipTests"
        }
      }
      post {
        success { updateGitlabStatus(state: 'success') }
        failure { updateGitlabStatus(state: 'failed') }
      }
    }
    
    // Stage E.
    stage('E. Tests') {
      steps {
        updateGitlabStatus(state: 'running')
        script {
          sh "mvn test"
        }
      }
      post {
        success { updateGitlabStatus(state: 'success') }
        failure { updateGitlabStatus(state: 'failed') }
      }
    }
    
    // Stage F.
    stage('F. Code Scanning') {
      steps {
        updateGitlabStatus(state: 'running')
        script {
          sh "sonar-scanner -Dsonar.java.binaries=. -Dsonar.login=${SONAR_LOGIN}"
        }
      }
      post {
        success { updateGitlabStatus(state: 'success') }
        failure { updateGitlabStatus(state: 'failed') }
      }
    }
    
    // Stage G.
    stage('G. Docker build & push (DEV)') {
      when {
        expression { event_name == 'push' }
      }
      steps {
        updateGitlabStatus(state: 'running')
        script {
          docker.withRegistry("https://${DOCKER_REGISTRY}") {
            image = docker.build("${DOCKER_IMAGE_URL}", "--no-cache .")
            image.push("${DOCKER_DEV_TAG}")
          }
        }
      }
      
      post {
        success { updateGitlabStatus(state: 'success') }
        failure { updateGitlabStatus(state: 'failed') }
      }
    }
    
    // Stage H.
    stage('H. Kubernetes restart pod') {
      steps {
        updateGitlabStatus(state: 'running')
        script {
            sh "pending..."
        }
      }
      post {
        success { updateGitlabStatus(state: 'success') }
        failure { updateGitlabStatus(state: 'failed') }
      }
    }

    // Stage I.
    stage('I. Docker build & push (TAG RELEASE)') {
      when {
        expression { event_name == 'tag_push' }
      }
      steps {
        updateGitlabStatus(state: 'running')
        script {
            GITLAB_LAST_TAG = sh (label: 'Get last commit tag from branch', returnStdout: true, script: "git describe --abbrev=0 --tags").trim()
            docker.withRegistry("https://${DOCKER_REGISTRY}") {
            image = docker.build("${DOCKER_IMAGE_URL}", "--no-cache .")
            image.push("${GITLAB_LAST_TAG}")
          }
        }
      }
      post {
        success { updateGitlabStatus(state: 'success') }
        failure { updateGitlabStatus(state: 'failed') }
      }
    }
  }

  post {
    always {
      script {
        cleanWs()
      }
    }
  }
}

