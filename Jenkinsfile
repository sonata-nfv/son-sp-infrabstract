pipeline {
  agent any
  stages {
    stage('Build') {
      parallel {
        stage('VIM adaptor') {
          steps {
            echo 'Building VIM Adaptor container'
            sh './pipeline/build/vimadaptor.sh'
          }
        }
        stage('WIM adaptor') {
          steps {
            echo 'Building WIM Adaptor container'
            sh './pipeline/build/wimadaptor.sh'
          }
        }
      }
    }
    stage('Unittests'){
      parallel {
        stage('Unittest VIM Adaptor') {
          steps {
            sh './pipeline/unittest/vimadaptor.sh'
          }
        }
        stage('Unittest WIM Adaptor') {
          steps {
            sh './pipeline/unittest/wimadaptor.sh'
          }
        }
      }
    }
    stage('Publish to :latest') {
      parallel {
        stage('VIM Adaptor') {
          steps {
            echo 'Publishing VIM Adaptor container'
            sh './pipeline/publish/vimadaptor.sh latest'
          }
        }
        stage('WIM Adaptor') {
          steps {
            echo 'Publishing WIM Adaptor container'
            sh './pipeline/publish/wimadaptor.sh latest'
          }
        }
      }
    }
    stage('Deploying in pre-integration ') {
      when{
        not{
          branch 'master'
        }        
      }      
      steps {
        sh 'rm -rf tng-devops || true'
        sh 'git clone https://github.com/sonata-nfv/tng-devops.git'
        dir(path: 'tng-devops') {
          sh 'ansible-playbook roles/sp.yml -i environments -e "target=pre-int-sp component=infrastructure-abstraction"'
        }
      }
    }
    stage('Publishing to :int') {
      when{
        branch 'master'
      }      
      parallel {
        stage('VIM Adaptor') {
          steps {
            echo 'Publishing VIM Adaptor container'
            sh './pipeline/publish/vimadaptor.sh int'
          }
        }
        stage('WIM Adaptor') {
          steps {
            echo 'Publishing WIM adaptor container'
            sh './pipeline/publish/wimadaptor.sh int'
          }
        }
      }
    }
    stage('Deploying in integration') {
      when{
        branch 'master'
      }      
      steps {
        sh 'rm -rf tng-devops || true'
        sh 'git clone https://github.com/sonata-nfv/tng-devops.git'
        dir(path: 'tng-devops') {
          sh 'ansible-playbook roles/sp.yml -i environments -e "target=int-sp component=infrastructure-abstraction"'
        }
      }
    }
  }
  post {
    always {
      echo 'Clean Up'
    }
    success {
        emailext (
          subject: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
          body: """<p>SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
            <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>""",
        recipientProviders: [[$class: 'DevelopersRecipientProvider']]
        )
      }
    failure {
      emailext (
          subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
          body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
            <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>""",
          recipientProviders: [[$class: 'DevelopersRecipientProvider']]
        )
    }  
  }
}
