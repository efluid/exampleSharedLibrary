# exampleSharedLibrary

This project contains an example of jenkins shared library and How to test it.

* Unit test should be in test folder.
* Integration test should be in it-test folder and follow this pattern : <className>Test.groovy

## Steps

If you run the Jenkinsfile of this project you will see that it run some steps :

* Checkout the project (classical github checkout but you can change it)
* Create a specific branch of the project and push it to github (in order to load the shared library based on it later)
* Build the project with gradle, and execute all of the unit tests
  * report will be in Jenkins Tests reports (Thanks to Junit)
* If there is no unit test error then
* Execute all of the integration tests (you can change this steps if you want to run only some it tests)
  * report will be in the Jenkins log (and not in the Jenkins test report) but build status change if some test failed

## Prerequisites

To run this example project you need to add this following plugins in Jenkins master instance :

* Pipeline utility steps : https://plugins.jenkins.io/pipeline-utility-steps
* Ansi color : https://wiki.jenkins.io/display/JENKINS/AnsiColor+Plugin

## Jenkinsfile customization

### Run test in parallel

By default all of the it-test are running in parallel (on node for each test class).
This improve performance but sometimes this is not possible because 2 tests cannot run each other at the same times.
For that it is possible to declare the list of "sequential test" in the groovy List _allTestsMustRunInSequential_

### Configure specific node

Each it test is running in separate Jenkins node.
By default we call a node without label.
If a specific test need a specific label this can be defined in the groovy Map _allTestsNode_
