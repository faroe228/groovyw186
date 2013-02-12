// #CreateStartScripts=false#

public class IvywHelper {

    public static String ivyDepends      = "org.apache.ivy:ivy:2.2.0";
    public static String strMavenBaseUrl = "http://repo1.maven.org/maven2/";
    public static String ivyMavenRelPath = "org/apache/ivy/ivy/2.2.0/ivy-2.2.0.jar";
    
    //public static URL ivyRemoteUrl = new URL( "http://repo1.maven.org/maven2/org/apache/ivy/ivy/2.2.0/ivy-2.2.0.jar" );
    
    static URL dependsToRemoteJarFile( String aDepends ) throws Exception {
        String[] lst = aDepends.split(":");
        String group = lst[0];
        String module = lst[1];
        String version = lst[2];
    
        String groupSlashed = group.replace(".", "/");
        String sUrlTemplate = "{strMavenBaseUrl}{groupSlashed}/{module}/{version}/{module}-{version}.jar";
        String sUrl         = sUrlTemplate
                        .replace("{strMavenBaseUrl}", strMavenBaseUrl)
                        .replace("{groupSlashed}"   , groupSlashed)
                        .replace("{module}"         , module)
                        .replace("{version}"        , version);
    
        return new URL( sUrl );
    }
    
    static String getUserHome() {
        return System.getProperty("user.home");
    }
    
    static File getIvyCacheDir() throws Exception {
        File ivyCacheDir = new File( getUserHome() , ".ivy2/cache").getCanonicalFile();
        return ivyCacheDir;
    }
    
    static File dependsToLocalJarFile( String aDepends ) throws Exception {
        String userHome = System.getProperty("user.home");
        File ivyCacheDir = new File(userHome, ".ivy2/cache").getCanonicalFile();
    
        String[] lst = aDepends.split(":");
        String group   = lst[0];
        String module  = lst[1];
        String version = lst[2];
    
        String s1 = String.format( "%s/%s/jars/%s-%s.jar", group, module, module, version );
        File f1 = new File(ivyCacheDir, s1).getCanonicalFile();
        return f1;
    }
    
    public static URL dependsToURL( String aDepends ) throws Exception {
        File localJarFile = dependsToLocalJarFile(aDepends);
        return localJarFile.exists() ? localJarFile.toURI().toURL() : dependsToRemoteJarFile(aDepends);
    }

}

// --------------------------------------------------------------------------------
// main
// --------------------------------------------------------------------------------

def thisJarFile = new File( this.getClass().name + '.jar' ).canonicalFile

// System.err.println "### ivyw.groovy $args"

def installMode = 'installIvy' in args ;

def ivyJarUrl = IvywHelper.dependsToURL( "org.apache.ivy:ivy:2.2.0" )
// System.err.println "### ivyJarUrl: $ivyJarUrl"

// install ivy locally for use at a future time
if ( !installMode && ivyJarUrl.toString().startsWith('http') ) {
    def pb = new ProcessBuilder( 'java', '-jar', thisJarFile.toString(), 'installIvy' )
    pb.start()
}

// println IvywHelper.dependsToLocalJarFile("org.apache.ivy:ivy:2.2.0")
// println IvywHelper.dependsToLocalJarFile("org.apache.ivy:ivy:2.2.0").exists()

def cl = new URLClassLoader( [ivyJarUrl] as URL[] )
Thread.currentThread().setContextClassLoader( cl ); // set classloader !!!
def ivyMainClass = cl.loadClass('org.apache.ivy.Main')

if ( installMode ) {
    ivyMainClass.main( "-types", "jar", '-notransitive', '-dependency', 'org.apache.ivy', 'ivy', '2.2.0' )
} else {
    ivyMainClass.main( args ) // ivy.Main calls sys exit
}
// note ivy main method of main class calls sys exit when done
