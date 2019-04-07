package utils

import com.lesfurets.jenkins.unit.PipelineTestHelper

class Helper {

    static void setVariables(Binding binding) throws Exception {
        binding.setVariable('env', ["JOB_NAME": "test"])
        binding.setVariable('JENKINS_HOME', "/var/jenkins")
    }

    static void registerAllowedMethods(PipelineTestHelper helper) throws Exception {
        helper.registerAllowedMethod("checkpoint", [String.class], { c -> false })
        helper.registerAllowedMethod("build", [LinkedHashMap.class], { c -> false })
        helper.registerAllowedMethod("triggerRemoteJob", [LinkedHashMap.class], { c -> false })
        helper.registerAllowedMethod("writeFile", [LinkedHashMap.class], { c -> false })
        helper.registerAllowedMethod("usernamePassword", [Map.class], { c -> "" })
        helper.registerAllowedMethod("withDockerContainer", [String.class, Closure.class], { c -> false })
        helper.registerAllowedMethod("fileExists", [String.class], { c -> false })
        helper.registerAllowedMethod("tool", [LinkedHashMap.class], { c -> false })
        helper.registerAllowedMethod("timestamps", [Closure.class], { c -> c.call() })
        helper.registerAllowedMethod("readFile", [String.class], { c -> "" })
        helper.registerAllowedMethod("mail", [LinkedHashMap.class], { c -> false })
        helper.registerAllowedMethod("emailext", [LinkedHashMap.class], { c -> false })
        helper.registerAllowedMethod("emailextrecipients", [ArrayList.class], { c -> false })
        helper.registerAllowedMethod("readJSON", [LinkedHashMap.class], { c -> [:] })
        helper.registerAllowedMethod("readProperties", [LinkedHashMap.class], { c -> [:] })
        helper.registerAllowedMethod("pwd", [], { c -> false })
        helper.registerAllowedMethod("findFiles", [LinkedHashMap.class], { c -> [] })
        helper.registerAllowedMethod("deleteDir", [], { c -> false })
        helper.registerAllowedMethod("stash", [LinkedHashMap.class], { c -> false })
        helper.registerAllowedMethod("unstash", [String.class], { c -> false })
        helper.registerAllowedMethod("junit", [LinkedHashMap.class], { c -> false })
    }
}
