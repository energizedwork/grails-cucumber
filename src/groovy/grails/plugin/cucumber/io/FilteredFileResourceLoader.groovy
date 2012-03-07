package grails.plugin.cucumber.io

import cucumber.io.Resource
import cucumber.io.ResourceLoader
import org.codehaus.groovy.grails.test.GrailsTestTargetPattern
import cucumber.io.FileResource
import cucumber.io.FileResourceLoader

class FilteredFileResourceLoader implements ResourceLoader {

    private GrailsTestTargetPattern[] patterns

    FilteredFileResourceLoader(GrailsTestTargetPattern[] patterns) {
        this.patterns = patterns
    }

    Iterable<Resource> resources(String path, String suffix) {
        def loader = new FileResourceLoader()

        loader.resources(path, suffix).findAll { FileResource file ->
            !isFeature(file) || match(file, suffix)
        }
    }

    private boolean match(FileResource file, String suffix) {
        boolean result = true

        if(patterns) {
            result = patterns.find { GrailsTestTargetPattern pattern ->
                pattern.matchesClass(file.path, suffix)
            }
        }

        result
    }

    private boolean isFeature(FileResource file) { file.path.endsWith('feature') }

}
