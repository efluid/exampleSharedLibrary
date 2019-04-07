import com.efluid.log.Log
import com.efluid.test.utils.TestUtils

TestUtils testUtils
Log log

def name() {
    'LogTest.groovy'
}

def initialise(varTestUtils) {
    testUtils = varTestUtils
    log = new Log(this)
}

def execute(scriptParams) {
    testUtils.callTestMethod(this, 'should_return_empty_when_getLastLog_is_called_after_debug_in_no_debug_mode', scriptParams)
    testUtils.callTestMethod(this, 'should_return_test_message_when_getLastLog_is_called_after_debug_in_debug_mode', scriptParams)
    testUtils.callTestMethod(this, 'should_return_test_message_info_when_getLastLog_is_called_after_info', scriptParams)
}

def should_return_empty_when_getLastLog_is_called_after_debug_in_no_debug_mode(scriptParams) {
    //Given
    String resultExpected = ""
    env.DEBUG = 'false'
    log.debug("test")

    //When
    String result = log.getLastLog()

    //Then
    testUtils.assertEquals(resultExpected, result)
}

def should_return_test_message_when_getLastLog_is_called_after_debug_in_debug_mode(scriptParams) {
    //Given
    String resultExpected = "[DEBUG] test"
    env.DEBUG = 'true'
    log.debug("test")

    //When
    String result = log.getLastLog()

    //Then
    testUtils.assertEquals(resultExpected, result)
    env.DEBUG = 'false'
}

def should_return_test_message_info_when_getLastLog_is_called_after_info(scriptParams) {
    //Given
    String resultExpected = "[INFO] test"
    log.info("test")

    //When
    String result = log.getLastLog()

    //Then
    testUtils.assertEquals(resultExpected, result)
}

return this;
