#!groovy

/**
 * Get the appropriate library version to use.
 *
 * @return A string representing the shared library version to use.
 */
String call(Map conf = [:]) {
    // env variables should be set at the Jenkins Server level
    String latestJobPattern = env.LIBRARY_LATEST_JOB_MATCHER

    String libraryRepo = env.LIBRARY_REPO
    if(conf.libraryRepo) {
        libraryRepo = conf.libraryRepo
    }

    String defaultLatestLibraryVersion = env.LIBRARY_DEFAULT_LATEST_VERSION
    String defaultStableLibraryVersion = env.LIBRARY_DEFAULT_STABLE_VERSION ?: "stable"

    String githubApiURL = env.GITHUB_API_URL ?: "api.github.com"
    String githubCredentialsID = env.GITHUB_CREDENTIALS_ID

    assert latestJobPattern
    assert libraryRepo
    assert defaultStableLibraryVersion
    assert githubApiURL

    if(latestJobPattern){
        // Jobs matching this pattern will receive the latest Github release of the shared library
        if(env.JOB_NAME =~ latestJobPattern) {
            if(defaultLatestLibraryVersion) {
                return defaultLatestLibraryVersion
            }
            // fetch latest release tag
            def requestConfig = [
                    url: "https://${githubApiURL}/repos/${libraryRepo}/releases/latest",
                    httpMode: 'GET',
                    contentType: 'APPLICATION_JSON',
                    validResponseCodes: '200',
            ]
            if(githubCredentialsID) {
                requestConfig.authentication = githubCredentialsID
            }

            def response = httpRequest(requestConfig)
            return readJSON(text: response.content).tag_name
        }
    }
    // All others get the "stable" tag
    return defaultStableLibraryVersion
}