# jenkins-get-library-version
A Jenkins shared library helper function to help load different versions based on job name.

## What is this?
On my travels through Jenkins, Groovy Shared Library, and Declarative Pipeline. I read the internet, and watched
many Jenkins World talks, often with conflicting opinions and ideas.

One of the talks had an interesting pattern with some plugin or something that ran some code before loading a projects
`Jenkinsfile` to use a version of their shared library, latest or stable, depending on some environment variabe and a regex.

I can't remember which talk it is, but I'll add a link here if I ever find it.

This is an attempt at creating a small library that could be used to do the same thing in a `Jenkinsfile` like this:
```groovy
#!groovy
def libraryVersion = getLibraryVersion()
library "myRealLibrary@${libraryVersion}"

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
```

## Why is this?
Typically, pipeline library versioning goes one of 2 ways... People either pin to `main` and en(joy|dure) the
exhilaration of innovation, or they pin to a branch, tag, sha of a library and make moving forwards difficult.

I've decided that having different versions based on `JOB_NAME` is sensible. This way you could match jobs beginning
with `pipeline-test` and have them use some "latest" version (tag, branch, whatever...), and all other jobs could
reference a slower moving "stable" version.

## How is this?
Most of the configuration is done with environment variables that should be set at the global configuration level:
### Required:

#### Plugins:
- [HTTP Request](https://plugins.jenkins.io/http_request/)

#### Environment Variables:
- `LIBRARY_REPO` - The Github `org/repo` that your main shared library lives in. This is used to query the Github API
  for the latest release `tag_name`.
  
- `LIBRARY_LATEST_JOB_MATCHER` - The pattern to match jobs for the "latest" library version.
  Example: `^pipeline-test|^my-cool-jobs`.
  
### Optional:
#### Environment Variables:
- `LIBRARY_DEFAULT_STABLE_VERSION` - Override the default "stable" version. Default: `stable`.
- `LIBRARY_DEFAULT_LATEST_VERSION` - Override the default "latest" version. Sometimes, you might just want to use
the `main` branch and not worry about creating Github releases. Defaults to Github "Latest Release" tag.
  
- `GITHUB_API_URL` - Override the Github API URL for GIthub Enterprise users. Default: `api.github.com`.
- `GITHUB_CREDENTIALS_ID` - The ID of a credential within Jenkins that holds the username and personal access token to use
when querying the Github API. Required if `LIBRARY_REPO` is private.
  
## Library Configuration
1. Add this library to the Global Configuration and make sure that it is implicitly loaded. Meaning that it will be
   available in any pipeline.
   
2. Add your main library without implicitly loading it. Your `Jenkinsfile` will be responsible for loading it explicitly.
Sadly, you can't load a library dynamically if it is already implicitly loaded. Jenkins will always use the first library
   configuration that it finds. 
