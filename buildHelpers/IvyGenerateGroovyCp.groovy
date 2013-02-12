// the working directory at runtime is up one level (build folder)
// because it is called by gradle at build time

// this code also calls ivyw.jar

gi = groovy.inspect.swingui.ObjectBrowser.&inspect;

def ant = new AntBuilder();
ant.mkdir( dir: 'src/main/resources/generated' )

def ivyPathsTextFile   = new File("./src/main/resources/generated/ivyPaths.txt").canonicalFile;
def mavenUrlsTextFile  = new File("./src/main/resources/generated/mavenUrls.txt").canonicalFile;

def outputFiles = []
outputFiles << ivyPathsTextFile;
outputFiles << mavenUrlsTextFile;

boolean forceRun = args.find { it.toLowerCase().contains('forceRun'.toLowerCase()) } != null ;

if (!forceRun) {
	if ( outputFiles.collect { it.exists() }.every() ) {
		println "### IvyGenerateGroovyCp - nothing to do."
		return; // !!! nothing to do !!!
	}
} else {
	outputFiles.each { itOutputFile ->
		ant.delete( file: itOutputFile.toString() )
	}
}

def userHome = System.properties.'user.home'
def ivyHomeDir = new File( userHome, ".ivy2" ).canonicalFile

def getIvyCacheDir() {
    def userHome = System.properties.'user.home'
    def ivyHomeDir = new File( userHome, ".ivy2" ).canonicalFile
    return new File( ivyHomeDir, "cache" ).canonicalFile;
}

def SPACE = (32 as Character) as String;

String sCmd = """
			java -jar ./buildHelpers/ivyw.jar
			-dependency org.codehaus.groovy    groovy-all    1.8.6
			-types      jar			
""".trim().split().collect { it.trim() }.findAll { it }.join( SPACE ).toString() ;

///////////////////////////

println "### sCmd: $sCmd"
StringBuffer sb1 = new StringBuffer();

Process p = sCmd.execute();
p.consumeProcessOutput(sb1,sb1);
int ev = p.waitFor();
println sb1.toString()
assert ev==0 ;


///////////////////////////

// org.codehaus.groovy-groovy-all-caller-default.xml
File groovyDependsXmlFile = new File( ivyCacheDir, "org.codehaus.groovy-groovy-all-caller-default.xml" ).canonicalFile
assert groovyDependsXmlFile.exists()

def ivyDependsXmlFileToIvyPathsAndMavenUrls( aIvyDependsXmlFile ) {
    // returns map with keys ivyPaths and mavenUrls  
    def result    = [:]
    def ivyPaths  = []
    def mavenUrls = []
    
    def userHome    = System.properties.'user.home'
    def ivyHomeDir  = new File( userHome, ".ivy2" ).canonicalFile
    def ivyCacheDir = new File( ivyHomeDir, "cache" ).canonicalFile    
    
    // ### main input ###
    // File groovyDependsXmlFile = new File( ivyCacheDir, "org.codehaus.groovy-groovy-all-caller-default.xml" ).canonicalFile
    // assert groovyDependsXmlFile.exists()
    
    def root       = new XmlParser().parse( aIvyDependsXmlFile )
    def artifacts_ = root.depthFirst().findAll { it.name() == 'artifact' } // incorrect path order
    // create and populate a correctly path ordered artifacts list
	def artifacts = [null] * artifacts_.size(); // will have correct path order
	artifacts_.each {
		int idx        = it.parent().parent().@position as Integer
		artifacts[idx] = it;
	}

    // [name, type, ext, status, details, size, time, location]
    
    filePaths = [] // has absolute path to jars in local ivy cache
    artifacts.each {
        filePaths << it.@location
        mavenUrls << (it.'origin-location'.first().@location).toString().trim()
        
    }
    
    // convert the absolute path jars to generic ivy paths relative to ivy cache dir
    ivyPaths = filePaths.collect { (it.trim().toString() - ivyCacheDir)[1 .. -1].replace('\\', '/') } // skipping leading slash
    
    result.ivyPaths  = ivyPaths
    result.mavenUrls = mavenUrls
    
    return result;
}

// ivyPaths and mavenUrls
def resultMap = ivyDependsXmlFileToIvyPathsAndMavenUrls( groovyDependsXmlFile );

ivyPathsTextFile.text  = resultMap.ivyPaths.join('\n').trim()   
mavenUrlsTextFile.text = resultMap.mavenUrls.join('\n').trim()
