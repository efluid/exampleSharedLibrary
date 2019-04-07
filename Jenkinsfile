allTestsReport = [:]
allTestsStatus = [:]
allTestsNode = [:]
allTestsMustRunInSequential = []
testUtils = null

node() {
    dir('jenkinsSharedLibrary') {
        def scriptParams = [:]
        scriptParams['branch'] = 'master' //change this in multibranch project
        scriptParams['executeTests'] = params.executeTests

        stage('Checkout'){
            sh 'rm -Rf *'
            checkout scm
            addGitHubSshAccess()
        }

        def final branchName = createBranchSharedLibrary(scriptParams['branch'])
        scriptParams['sharedLibraryBranch'] = branchName
        //WARNING : exampleSharedLibrary must exist in Jenkins Administration !
        def lib = library identifier: "exampleSharedLibrary@$branchName"
        testUtils = lib.com.efluid.test.utils.TestUtils.new(this, allTestsReport)

        try {
            executeBuildAndTU()
            if (currentBuild.resultIsBetterOrEqualTo('SUCCESS')) {
                executeTI(scriptParams)
            }
        } catch (Exception e) {
            //Add mail if needed
            throw e
        } finally {
            deleteBranchSharedLibrary(branchName)
        }
    }
}

private addGitHubSshAccess() {
    try {
        withCredentials([sshUserPrivateKey(credentialsId: 'github', keyFileVariable: 'keyFile', passphraseVariable: '', usernameVariable: 'User')]) {
            sh "cp ${env.KEYFILE} ~/.ssh/id_rsa"
        }
    } catch (Exception e) {
        //Maybe still here
    }
}

private createBranchSharedLibrary(branch) {
    def branchName = "nonRegJenkinsSharedLibrary_$branch"
    deleteBranchSharedLibrary(branchName)
    sh "git branch $branchName"
    sh "git push --set-upstream origin $branchName:$branchName"
    return branchName
}

private void deleteBranchSharedLibrary(branchName) {
    try {
        sh "git branch -D $branchName"
        sh "git push origin :$branchName"
    } catch (Exception e) {
        //not problem
    }
}

private void executeBuildAndTU() {
    stage('Build And Unit Tests') {
        withEnv(["PATH=${tool 'gradle-5.2.1'}/bin:${env.PATH}"]) {
            env.GRADLE_OPTS = '-Xmx2G -Dorg.gradle.daemon=false'
            sh 'gradle build'
        }

        junit 'build/test-results/**/*.xml'
    }
}

private void executeTI(scriptParams) {
    stage('IT Tests') {
        stash allowEmpty: true, includes: 'it-test/resources/**', name: 'technicalTestResources'
        executeAllTIInParallel(scriptParams)
    }
}

private void executeAllTIInParallel(scriptParams) {
    //Warning : findFiles need this plugin : https://plugins.jenkins.io/pipeline-utility-steps
    def testsToRun = findFiles(glob: 'it-test/com/efluid/**/*/*.groovy')

    def stepsForParallel = [:]
    def stepsForSequential = [:]
    for (int i = 0; i < testsToRun.size(); i++) {
        if (allTestsMustRunInSequential.contains(getTestName(testsToRun[i].path))) {
            addTestToRun(stepsForSequential, testsToRun[i].path, scriptParams)
        } else {
            addTestToRun(stepsForParallel, testsToRun[i].path, scriptParams)
        }
    }

    runTestsAndShowResult(stepsForParallel, stepsForSequential)
}

def runTest(testName, testPath, scriptParams) {
    return {
        stash includes: testPath, name: testName
        def nodeLabel = allTestsNode.get(testName) != null ? allTestsNode.get(testName) : ''
        node(nodeLabel) {
            unstash testName
            unstash 'technicalTestResources'
            echo "Run test ${testPath}"
            def test = load testPath
            allTestsStatus.put(testName, 'OK')
            test.initialise(testUtils)
            try {
                test.execute(scriptParams)
            } catch (Exception e) {
                echo "${testName} : KO in test class \n" + e.getClass().getName() + " : " + e.getMessage() + "\n\t\t" + e.getStackTrace().join('\n\t\t')
                if (allTestsReport.get(testName) == null) {
                    allTestsReport.put(testName, [:])
                }
                allTestsReport.get(testName)[testName] = [:]
                allTestsReport.get(testName)[testName].put('status', 'KO')
                allTestsReport.get(testName)[testName].put('exception', e)
            }
        }
    }
}

private void runTestsAndShowResult(stepsForParallel, stepsForSequential) {
    echo "Start running sequential IT tests"
    stepsForSequential.each {
        k, v -> v.call()
    }
    echo "Start running parallel IT tests"
    parallel stepsForParallel
    echo "Result of IT tests"
    setBuildResultAndGlobalStatusRegardingTIResult()
    showTestsResult()
}

private void showTestsResult() {
    //Need this jenkins plugin : https://wiki.jenkins.io/display/JENKINS/AnsiColor+Plugin
    ansiColor('xterm') {
        allTestsReport.each {
            allKey, allValue ->
                if (allTestsStatus.get(allKey) == 'OK') {
                    echo "TEST : ${allKey} => \u001B[32m${allTestsStatus.get(allKey)}\u001B[0m"
                } else {
                    echo "TEST : ${allKey} => \u001B[31m${allTestsStatus.get(allKey)}\u001B[0m"
                }
                testUtils.displayTestReport(allKey)
        }
    }
}

private void setBuildResultAndGlobalStatusRegardingTIResult() {
    allTestsReport.each {
        allKey, allValue ->
            allValue.each {
                key, value ->
                    if (value.get('status') == 'KO') {
                        allTestsStatus.put(allKey, 'KO')
                        currentBuild.result = 'UNSTABLE'
                    }
            }
    }
}

private void addTestToRun(stepsForTests, testPath, scriptParams) {
    def testName = getTestName(testPath)
    stepsForTests[testName] = runTest(testName, testPath, scriptParams)
}

private String getTestName(testPath) {
    return testPath.substring(testPath.lastIndexOf("/") + 1)
}