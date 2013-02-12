groovyw186
==========

A thin groovy 1.8.6 wrapper.

Examples of how to use:

1)

java -jar groovyw186.jar someGroovyScript.groovy

2)

In this example we will assume you have a groovy script named: alpha.groovy

Step1 - copy groovyw186.jar to alpha.jar
Step2 - Run by double clicking on the alpha.jar file or do the command line: java -jar alpha.jar

Note: The jar and the groovy script file should be in the same directory/folder.

If you add the next line to your groovy script:
// #CreateStartScripts=true#
it will create start start scripts (bat and bash).

You can also embed you groovy script into the jar file.  When the jar file starts running 
it first looks for an external groovy file with the same name as the jar file. If an external
script file is not found it will look for one embedded in the jar file. If no script files
are found (external and internal to the jar) the jar file defaults to just being a generic 
groovy script runner (1st arg should be the path to a groovy file).
