/**
 * Created with IntelliJ IDEA.
 * User: ltoenjes
 * Date: 2/4/13
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;


public class JavaMainInstall {




    public static String ivyDepends = "org.apache.ivy:ivy:2.2.0";
    public static String strMavenBaseUrl = "http://repo1.maven.org/maven2/";
    public static String ivyMavenRelPath = "org/apache/ivy/ivy/2.2.0/ivy-2.2.0.jar";

    //public static URL ivyRemoteUrl = new URL( "http://repo1.maven.org/maven2/org/apache/ivy/ivy/2.2.0/ivy-2.2.0.jar" );

    static URL dependsToRemoteJarFile( String aDepends ) throws Exception {
        String[] lst = aDepends.split(":");
        String group = lst[0];
        String module = lst[1];
        String version = lst[2];

        String groupSlashed = group.replace(".", "/");
        String sUrl = "{strMavenBaseUrl}{groupSlashed}/{module}/{version}/{module}-{version}.jar";
        sUrl = "{strMavenBaseUrl}{groupSlashed}/{module}/{version}/{module}-{version}.jar"
                .replace("{strMavenBaseUrl}", strMavenBaseUrl)
                .replace("{groupSlashed}", groupSlashed)
                .replace("{module}", module)
                .replace("{version}", version);

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
        String group = lst[0];
        String module = lst[1];
        String version = lst[2];

        String s1 = String.format( "%s/%s/jars/%s-%s.jar", group, module, module, version );
        File f1 = new File(ivyCacheDir, s1).getCanonicalFile();
        return f1;
    }

    public static URL dependsToURL( String aDepends ) throws Exception {
        File localJarFile = dependsToLocalJarFile(aDepends);
        return localJarFile.exists() ? localJarFile.toURI().toURL() : dependsToRemoteJarFile(aDepends);
    }

    public static URL[] getMavenUrls() throws Exception {
        String resText = new Scanner(
                JavaMainInstall.class.getResourceAsStream("generated/mavenUrls.txt")
        ).useDelimiter("\\A").next();

        String[] lines = resText.split("\n");
        URL[] urls = new URL[ lines.length ];

        int idx = 0;
        for (String line : lines) {
            urls[ idx++ ] = new URL( line );
        }

        return urls;
    }

    public static URL[] getLocalIvyJarUrls() throws Exception {
        String ivyPathsContent = new Scanner(
                JavaMainInstall.class.getResourceAsStream("generated/ivyPaths.txt")
        ).useDelimiter("\\A").next();

        String[] ivyPathsArray = ivyPathsContent.split("\n");
        File[] ivyJarFilesArray = new File[ ivyPathsArray.length ];
        URL[] ivyJarFilesUrls = new URL[ ivyPathsArray.length ];

        for (int i = 0; i < ivyPathsArray.length; i++) {
            ivyJarFilesArray[i] = new File( getIvyCacheDir(), ivyPathsArray[i] ).getCanonicalFile();
            ivyJarFilesUrls[i] = ivyJarFilesArray[i].toURI().toURL();
        }

        return ivyJarFilesUrls;
    }

    public static void main(final String[] args) throws Exception {
        System.setProperty("start.time", ( (Long) System.currentTimeMillis() ).toString() );






        // System.out.println("### missingLocalIvyFilesCounter: " + missingLocalIvyJarFilesCounter.toString());


        URL thisJarUrl = JavaMainInstall.class.getProtectionDomain().getCodeSource().getLocation();
        String thisJarPath = new File( thisJarUrl.toURI() ).getPath();
        String thisJarName =  new File( thisJarUrl.toURI() ).getName();

        String thisJarJustName = thisJarName.substring(0, thisJarName.lastIndexOf("."));





        // System.out.println( thisJarJustName );

        // System.err.flush();
        // System.out.println( dependsToLocalJarFile( ivyDepends ) );
        // System.out.println( dependsToRemoteJarFile( ivyDepends ) );
        URL ivyUrl = dependsToURL(ivyDepends);
        URL[] urlArr = new URL[] { ivyUrl } ;
        URLClassLoader cl = new URLClassLoader( urlArr );
        Thread.currentThread().setContextClassLoader(cl);
        Class<?> ivyMainCls = cl.loadClass("org.apache.ivy.Main");
        final Method ivyMainMethod = ivyMainCls.getMethod("main", String[].class);

        //System.out.println("111");
        //String[] sa = new String[] { "-dependency", "org.apache.ivy", "ivy", "2.2.0", "-notransitive", "-types", "jar", "-cachepath", "ivy.cp.txt"  } ;
        //ivyMainMethod.invoke(null, (Object) sa ); // var args

        // String javaNetworkPid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        // System.out.println( "### JavaMainRunOld: " + javaNetworkPid );

        String[] ivyArgsArray = new String[] {
                "-warn"
                // "-verbose"
                , "-dependency", "org.codehaus.groovy", "groovy-all", "1.8.6"
                , "-types", "jar"
        } ;

        final ArrayList<String> ivyArgs = new ArrayList<String>();
        ivyArgs.addAll( Arrays.asList(ivyArgsArray) );

        // File cpFile = new File("groovy.cp.txt");

        /*
        if ( !cpFile.exists() ) {
            ivyArgs.add( "-cachepath" );
            ivyArgs.add( cpFile.toString() );
        }
        */

        // ###
        //ivyArgs.add( "-main");
        //ivyArgs.add( "groovy.ui.GroovyMain" );
        //ivyArgs.add( groovyScriptUrl.toString() );





        // ivyArgs.add( "-args" ); // don't need, ivy bug???

        //List<String> mainArgsList = Arrays.asList(args);
        //ivyArgs.addAll( mainArgsList );
        ivyArgs.addAll(Arrays.asList(args));

        //System.out.println("### mainArgsList: " + mainArgsList);

        /*
        if (missingLocalIvyJarFilesCounter > 0) {
            ivyMainMethod.invoke(null, (Object) ivyArgs.toArray(new String[ivyArgs.size()])); // var args
        } else {
            URLClassLoader cl2 = new URLClassLoader(ivyJarFilesUrls);
            Thread.currentThread().setContextClassLoader(cl2);
            Class<?> groovyMainCls = cl2.loadClass("groovy.ui.GroovyMain");
            Method groovyMainMethod = groovyMainCls.getMethod("main", String[].class);

            ArrayList<String> argsList2 = new ArrayList<String>();
            argsList2.add( groovyScriptUrl.toString() );
            argsList2.addAll( Arrays.asList(args) );

            groovyMainMethod.invoke(null, (Object) argsList2.toArray( new String[argsList2.size()] ) ); // var args
        }
        */
        // exp

        ivyMainMethod.invoke(null, (Object) ivyArgs.toArray(new String[ivyArgs.size()])); // var args




    } // end main



}
