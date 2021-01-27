package helpers

import com.lesfurets.jenkins.unit.RegressionTest
import spock.lang.Specification

import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library
import static com.lesfurets.jenkins.unit.global.lib.ProjectSource.projectSource

/**
 * A base class for Spock testing using the pipeline helper
 */
class PipelineSpockTestBase extends Specification  implements RegressionTest {

    /**
     * Delegate to the test helper
     */
    @Delegate PipelineTestHelper pipelineTestHelper

    /**
     * Do the common setup
     */
    def setup() {
        // Set callstacks path for RegressionTest
        callStackPath = 'tests/fixtures/callstacks/'

        // Create and config the helper
        pipelineTestHelper = new PipelineTestHelper()
        pipelineTestHelper.setUp()

        // Setup the shared library
        def testLibrary = library().name('testLibrary')
                .defaultVersion('<local>')
                .allowOverride(true)
                .implicit(true)
                .targetPath('<local>')
                .retriever(projectSource())
                .build()
        pipelineTestHelper.helper.registerSharedLibrary(testLibrary)

        // Need a library registered to be able to load dynamically
        def stubLibrary = library().name('stubLibrary')
                .defaultVersion('<local>')
                .allowOverride(true)
                .implicit(false)
                .targetPath('<local>')
                .retriever(projectSource())
                .build()
        pipelineTestHelper.helper.registerSharedLibrary(stubLibrary)
    }
}