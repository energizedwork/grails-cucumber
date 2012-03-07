package grails.plugin.cucumber.io

import com.energizedwork.test.io.FileSystemBuilder
import org.codehaus.groovy.grails.test.GrailsTestTargetPattern
import spock.lang.Specification
import spock.lang.Unroll

class FilteredFileResourceLoaderSpec extends Specification {

    FileSystemBuilder fileSystem = new FileSystemBuilder()

    def cleanup() { fileSystem.tearDown() }
    
    @Unroll({"returns $files given patterns [$patterns]"})
    def 'does not filter any files if given no grails test target patterns'() {
        given:
        fileSystem {
            'cucumber' {
                'editor.feature'()
                'admin.feature'()
                'user.feature'()
            }
        }

        def loader = new FilteredFileResourceLoader(patterns)
        def resources = loader.resources(fileSystem['cucumber'].absolutePath, 'feature')

        expect:
        files == resources.collect { it.path }.sort()

        where:
        files                                               | patterns
        ['admin.feature', 'editor.feature', 'user.feature'] | new GrailsTestTargetPattern[0]
        ['admin.feature', 'editor.feature', 'user.feature'] | new GrailsTestTargetPattern[0]
        ['admin.feature', 'editor.feature', 'user.feature'] | pattern('**.*')
        ['admin.feature', 'editor.feature', 'user.feature'] | null
        ['editor.feature']                                  | pattern('editor')
        []                                                  | pattern('Editor')
    }

    private GrailsTestTargetPattern[] pattern(String raw) {
        [new GrailsTestTargetPattern(raw)] as GrailsTestTargetPattern[]
    }

}
