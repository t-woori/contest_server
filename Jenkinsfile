def image_name = "contest-server"
def main_dir = "./"
pipeline {
    agent any
    environment {
        aws_ecr_uri = credentials('AWS-ECR-URI')
        aws_region = credentials('AWS-REGION')
        build_filename = credentials('BUILD-FILENAME')
        spring_active_file = credentials('SPRING-ACTIVE-FILE')
    }
    stages {
        stage('Clean Workspace') {
            steps {
                sh('rm ~/.dockercfg || true')
                sh('rm ~/.docker/config.json || true')
            }
        }
        stage('Pull Codes from Github') {
            steps {
                checkout scm
            }
        }
        stage('Build Codes by Gradle') {
            steps {
                sh('cd $mainDir')
                sh('./gradlew clean build')
            }
        }
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh "./gradlew sonar"
                }
            }
        }
        stage("Quality gate") {
            steps {
                waitForQualityGate abortPipeline: true
            }
        }
        stage('Build Docker Image && Tagging to AWS ECR Repository') {
            steps {
                sh("docker build --build-arg JAR_FILE=$build_filename --build-arg SPRING_ACTIVE_PROFILE=$spring_active_file" + " " +
                        "-t $image_name:${currentBuild.number} $main_dir")
                sh("docker tag $image_name:${currentBuild.number} " +
                        "$aws_ecr_uri" + "/" + "$image_name:${currentBuild.number}")
            }
        }
        stage("Push to AWS ECR Repository") {
            steps{
                withAWS(region: "$aws_region", credentials: 'ci-user') {
                    sh("aws ecr get-login-password --region $aws_region | docker login --username AWS --password-stdin $aws_ecr_uri")
                    sh("docker push " + "$aws_ecr_uri" + "/" + "$image_name:${currentBuild.number}")
                }
            }
        }
    }
}