// #CreateStartScripts=false#

ant = new AntBuilder();

ant.delete( dir: '.gradle' )
ant.delete( dir: 'build' )

// ant.delete( dir: 'gradle' )
// ant.delete( file: 'gradlew' )
// ant.delete( file: 'gradlew.bat' )
