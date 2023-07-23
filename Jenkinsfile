def image_name = "contest-server"
def main_dir = "./"
pipeline {
    agent any
    environment {
        aws_ecr_uri = credentials('AWS-ECR-URI')
        aws_region = credentials('AWS-REGION')
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
        stage('Build Docker Image && Tagging to AWS ECR Repository') {
            steps {
                sh("docker build --build-arg build/libs/contest_server-0.0.1-SNAPSHOT.jar " +
                        "-t $image_name:${currentBuild.number} $main_dir")
                sh("docker tag $image_name:${currentBuild.number} " +
                        "$aws_ecr_uri" + "/" + "$image_name:${currentBuild.number}")
            }
        }
        stage("Push to AWS ECR Repository") {
            steps {
                sh("aws ecr get-login-password --region $aws_region" +
                        " | docker login --username AWS --password-stdin $aws_ecr_uri")
                sh("docker push " + "$aws_ecr_uri" + "/" + "$image_name:${currentBuild.number}")
            }
        }
    }
}
