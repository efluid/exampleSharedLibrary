package com.efluid.log

import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Before
import org.junit.Test
import utils.Helper

class TestLog extends BasePipelineTest {

    @Override
    @Before
    void setUp() throws Exception {
        Helper.setVariables(binding)
        super.setUp()
        Helper.registerAllowedMethods(helper)
    }

    @Test
    void should_return_no_error_when_all_methods_called() throws Exception {
        runScript("test/com/efluid/log/Jenkinsfile_TestLog.groovy")
        printCallStack()
        assertJobStatusSuccess()
    }
}