groovyw186
==========

A thin groovy 1.8.6 wrapper.

Examples of how to use:

1)

java -jar groovyw186.jar someGroovyScript.groovy

or

java -jar -DCreateStartScripts=true groovyw186.jar someGroovyScript.groovy

Above command will create start scripts to launch groovyw186.jar

2)

In this example we will assume you have a groovy script named: alpha.groovy

Step1 - copy groovyw186.jar to alpha.jar

Step2 - Run by double clicking on the alpha.jar file or do the command line: java -jar alpha.jar

Note: The jar and the groovy script file should be in the same directory/folder.

If you add the next line to your groovy script:

// #CreateStartScripts=true#

it will create start start scripts (bat and bash).  You can also use -DCreateStartScripts=true with your java command.

You can also embed your groovy script into the jar file 

(which means you could email a single tiny jar file and 
a user would only have to double click on the emailed jar to run your embedded groovy script 
[obviously user would have to have Java installed on their computer]) 

with the jar command:

jar -uf alpha.jar alpha.groovy

To extract alpha.groovy from alpha.jar use the jar command:

jar -xf alpha.jar alpha.groovy

When the jar file starts running 
it first looks for an external groovy file with the same name (excluding file extension) as the jar file. If an external
script file is not found it will look for one embedded in the jar file. If no script files
are found (external and internal to the jar) the jar file defaults to just being a generic 
groovy script runner (1st arg should be the path to a groovy file or an http based url).

The groovyw186.jar can also function as a thin portable ivy 2.2.0 jar by
renaming the groovyw186.jar to ivyw.jar or doing the command: java -jar groovyw186.jar ivyw.groovy -your ivy args
