package com.efluid.test.utils

class TestUtils implements Serializable {

    def steps
    def globalTestReport

    TestUtils(steps, allTestReport) {
        this.steps = steps
        this.globalTestReport = allTestReport
    }

    private getReportForThisTest(scriptLoad){
        def testName = scriptLoad.name()
        println "getReportForThisTest : ${testName}"
        if (globalTestReport.get(testName) == null){
            globalTestReport.put(testName, [:])
        }
        return globalTestReport.get(testName)
    }

    def callTestMethod(scriptLoad, methodName, scriptParams) {
        getReportForThisTest(scriptLoad)[methodName] = [:]
        getReportForThisTest(scriptLoad)[methodName].put('start', System.currentTimeMillis())
        if (scriptParams['executeTests']?.trim() && !scriptParams['executeTests'].split(' *, *').contains(methodName)) {
            steps.echo "Skip test : " + methodName
            getReportForThisTest(scriptLoad)[methodName].put('status', 'skip')
        } else {
            steps.echo "Execute test : " + methodName
            try {
                if (scriptLoad.getMetaClass().respondsTo(scriptLoad, methodName, Object).size() > 0) {
                    scriptLoad."$methodName"(scriptParams)
                    steps.echo "${methodName} : OK"
                    getReportForThisTest(scriptLoad)[methodName].put('status', 'OK')
                } else {
                    steps.error "Error : No method found"
                    getReportForThisTest(scriptLoad)[methodName].put('status', 'Not found')
                }
            } catch (Exception | AssertionError e) {
                steps.echo "${methodName} : KO \n" + e.getClass().getName() + " : " + e.getMessage() + "\n\t\t" + e.getStackTrace().join('\n\t\t')
                getReportForThisTest(scriptLoad)[methodName].put('status', 'KO')
                getReportForThisTest(scriptLoad)[methodName].put('exception', e)
            }
        }
        testReportEndTime(scriptLoad, methodName)
    }

    private testReportEndTime(scriptLoad, label) {
        if (getReportForThisTest(scriptLoad)[label] != null) {
            def start = getReportForThisTest(scriptLoad)[label]['start']
            def end = System.currentTimeMillis()
            getReportForThisTest(scriptLoad)[label].put('end', end)
            getReportForThisTest(scriptLoad)[label].put('elapsed', (end - start) / 1000)
        }
    }

    // tag::displayTestReport[]
    /**
     * affiche un tableau, résumant les temps passé et le statut des tests
     */
    // end::displayTestReport[]
    def displayTestReport(testName) {
        steps.echo 'label'.center(100) + ';' + 'status'.center(10) + ';' + 'elapsed time (s)'.center(16)
        def keysTimesSummary = globalTestReport.get(testName).keySet() as List
        def display = ''
        for (int i = 0; i < keysTimesSummary.size(); i++) {
            def key = keysTimesSummary[i]
            def value = globalTestReport.get(testName)[key]
            def label = key.padRight(100)
            def status = value['status'] != null ? getPrintableStatus(value['status']) : ''.center(10)
            def elapsed = value['elapsed'] != null ? value['elapsed'].toString().center(16) : ''.center(16)
            display = display + label + ';' + status + ';' + elapsed + '\n'
            if (value['exception'] != null) {
                display += value['exception'].getClass().getName() + " : " + value['exception'].getMessage() + '\n'
            }
        }
        steps.echo display
    }

    private String getPrintableStatus(String status){
        if (status == 'OK'){
            return ''.center(5)+'\u001B[32m'+status+'\u001B[0m'+''.center(5)
        }else if (status == 'skip'){
            return ''.center(5)+'\u001B[33m'+status+'\u001B[0m'+''.center(5)
        }else if (status == 'Not found'){
            return ''.center(5)+'\u001B[35m'+status+'\u001B[0m'+''.center(5)
        }
        return ''.center(5)+'\u001B[31m'+status+'\u001B[0m'+''.center(5)
    }

    def assertEquals(ExpectedValue, resultValue) {
        try {
            assert ExpectedValue == resultValue
        } catch (AssertionError e) {
            steps.echo "assert '${ExpectedValue}' == '${resultValue}'"
            throw e
        }
    }

    def assertNotEquals(ExpectedValue, resultValue) {
        try {
            assert ExpectedValue != resultValue
        } catch (AssertionError e) {
            steps.echo "assert '${ExpectedValue}' != '${resultValue}'"
            throw e
        }
    }
}