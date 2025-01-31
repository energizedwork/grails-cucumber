/*
 * Copyright 2011-2012 Martin Hauner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugin.cucumber


import org.codehaus.groovy.grails.test.GrailsTestTypeResult
import org.codehaus.groovy.grails.test.event.GrailsTestEventPublisher
import org.codehaus.groovy.grails.test.report.junit.JUnitReportsFactory
import org.codehaus.groovy.grails.test.support.GrailsTestTypeSupport
import grails.plugin.cucumber.io.FilteredFileResourceLoader


class CucumberTestType extends GrailsTestTypeSupport {
    static final NAME = "cucumber"
    GroovyShell grailsShell
    String baseDir

    Cucumber cucumber

    CucumberTestType (String relativeSourcePath, String baseDir, GroovyShell grailsShell) {
        super(NAME, relativeSourcePath)
        this.grailsShell = grailsShell
        this.baseDir = baseDir
    }

    @Override
    List<String> getTestExtensions () {
        ["feature"]
    }

    @Override
    int doPrepare () {
        prepareCucumber ()
        loadFeatures ()
        countScenarios ()
    }

    @Override
    GrailsTestTypeResult doRun (GrailsTestEventPublisher eventPublisher) {
        runFeatures (eventPublisher)
    }

    @Override
    String toString () {
        NAME
    }

    private void prepareCucumber () {
        def shell = new GroovyShell (getTestClassLoader (), grailsShell.context)
        def resourceLoader = new FilteredFileResourceLoader(testTargetPatterns)
        cucumber = new Cucumber (getTestClassLoader(), resourceLoader, shell, featurePath ())
    }

    private void loadFeatures () {
        cucumber.loadFeatures ()
    }

    private int countScenarios () {
        cucumber.countScenarios ()
    }

    private GrailsTestTypeResult runFeatures (def publisher) {
        def formatter = createFormatter (publisher)

        cucumber.run (formatter, formatter)

        // todo merge finish into done!?
        formatter.finish ()
        formatter.done ()

        cucumber.printSummary (System.out)

        formatter.getResult ()
    }

    private def createFormatter (def publisher) {
        def swapper = createSystemOutAndErrSwapper ()
        def factory = createJUnitReportsFactory ()

        def report = new FeatureReport (new FeatureReportHelper (factory, swapper))
        def pretty = new PrettyFormatterWrapper (new PrettyFormatterFactory ())

        new CucumberFormatter (publisher, report, pretty, pretty)
        //new DebugFormatter (System.out, pretty)
    }

    private JUnitReportsFactory createJUnitReportsFactory () {
        JUnitReportsFactory.createFromBuildBinding (buildBinding)
    }

    private String featurePath () {
        ["test", NAME].join (File.separator)
    }
}
