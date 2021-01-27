package vars

import helpers.PipelineSpockTestBase
import org.codehaus.groovy.runtime.powerassert.PowerAssertionError

class GetLibraryVersionTest extends PipelineSpockTestBase {

    def 'getLibraryVersion fails when not configured correctly'() {
        when:
        runScript('test/unit/resources/getLibraryVersionTest.Jenkinsfile')
        printCallStack()

        then:
        thrown PowerAssertionError
    }

    def 'getLibraryVersion returns latest release tag when JOB_NAME matches LIBRARY_LATEST_JOB_MATCHER'() {
        given:
        addEnvVar('JOB_NAME', 'testing-pipeline')
        addEnvVar('LIBRARY_LATEST_JOB_MATCHER', '^testing-|^my-cool-jobs')
        addEnvVar('LIBRARY_REPO', 'myorg/example-repo')

        when:
        helper.registerAllowedMethod('httpRequest', [Map], { map ->
            if (map.url == "https://api.github.com/repos/myorg/example-repo/releases/latest") {
                return ["status": 200, "content": '{"tag_name": "v3.8.5"}']
            }
        })
        runScript('test/unit/resources/getLibraryVersionTest.Jenkinsfile')
        printCallStack()

        then:
        assert helper.callStack.findAll { call ->
            call.methodName == "library"
        }.any { call ->
            call.toString().contains('stubLibrary@v3.8.5')
        }
        assertJobStatusSuccess()
    }

    def 'getLibraryVersion configured latest version tag when JOB_NAME matches LIBRARY_LATEST_JOB_MATCHER'() {
        given:
        addEnvVar('JOB_NAME', 'testing-pipeline')
        addEnvVar('LIBRARY_LATEST_JOB_MATCHER', '^testing-')
        addEnvVar('LIBRARY_REPO', 'myorg/example-repo')
        addEnvVar('LIBRARY_DEFAULT_LATEST_VERSION', 'main')

        when:
        runScript('test/unit/resources/getLibraryVersionTest.Jenkinsfile')
        printCallStack()

        then:
        assert helper.callStack.findAll { call ->
            call.methodName == "library"
        }.any { call ->
            call.toString().contains('stubLibrary@main')
        }
        assertJobStatusSuccess()
    }

    def 'getLibraryVersion returns default stable version tag when JOB_NAME does not match LIBRARY_LATEST_JOB_MATCHER'() {
        given:
        addEnvVar('JOB_NAME', 'not-a-testing-pipeline')
        addEnvVar('LIBRARY_LATEST_JOB_MATCHER', '^testing-')
        addEnvVar('LIBRARY_REPO', 'myorg/example-repo')

        when:
        runScript('test/unit/resources/getLibraryVersionTest.Jenkinsfile')
        printCallStack()

        then:
        assert helper.callStack.findAll { call ->
            call.methodName == "library"
        }.any { call ->
            call.toString().contains('stubLibrary@stable')
        }
        assertJobStatusSuccess()
    }

    def 'getLibraryVersion returns configured stable version tag when JOB_NAME does not match LIBRARY_LATEST_JOB_MATCHER'() {
        given:
        addEnvVar('JOB_NAME', 'not-a-testing-pipeline')
        addEnvVar('LIBRARY_LATEST_JOB_MATCHER', '^testing-')
        addEnvVar('LIBRARY_REPO', 'myorg/example-repo')
        addEnvVar('LIBRARY_DEFAULT_STABLE_VERSION', 'production')

        when:
        runScript('test/unit/resources/getLibraryVersionTest.Jenkinsfile')
        printCallStack()

        then:
        assert helper.callStack.findAll { call ->
            call.methodName == "library"
        }.any { call ->
            call.toString().contains('stubLibrary@production')
        }
        assertJobStatusSuccess()
    }
}