#!groovy
def libraryVersion = getLibraryVersion()
library "stubLibrary@${libraryVersion}"

pipeline {
  agent any
  stages {
    stage('Library Dynamically Loaded!') {
      steps {
        echo "Using library version: ${libraryVersion}"
      }
    }
  }
}