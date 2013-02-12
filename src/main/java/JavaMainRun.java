/**
 * Created with IntelliJ IDEA.
 * User: ltoenjes
 * Date: 2/5/13
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;

public class JavaMainRun {

    static String getUserHome() {
        return System.getProperty("user.home");
    }

    static File getIvyCacheDir() throws Exception {

        File ivyCacheDir = new File( getUserHome() , ".ivy2/cache").getCanonicalFile();
        return ivyCacheDir;
    }

    public static URL[] getMavenUrls() throws Exception {
        String resText = new Scanner(
                JavaMainRun.class.getResourceAsStream("generated/mavenUrls.txt")
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
                JavaMainRun.class.getResourceAsStream("generated/ivyPaths.txt")
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

    public static File[] getLocalIvyJarFiles() throws Exception {
        String ivyPathsContent = new Scanner(
                JavaMainRun.class.getResourceAsStream("generated/ivyPaths.txt")
        ).useDelimiter("\\A").next();

        String[] ivyPathsArray = ivyPathsContent.split("\n");
        File[] ivyJarFilesArray = new File[ ivyPathsArray.length ];
        URL[] ivyJarFilesUrls = new URL[ ivyPathsArray.length ];

        for (int i = 0; i < ivyPathsArray.length; i++) {
            ivyJarFilesArray[i] = new File( getIvyCacheDir(), ivyPathsArray[i] ).getCanonicalFile();
            ivyJarFilesUrls[i] = ivyJarFilesArray[i].toURI().toURL();
        }

        return ivyJarFilesArray;
    }

    public static boolean calcMissingLocalJars() throws Exception {
        int missingFilesCtr = 0;
        for (File file : getLocalIvyJarFiles()) {
            if ( !(file.exists()) ) {
                missingFilesCtr++;
            }
        }
        return missingFilesCtr > 0;
    }

    public static void createStartScripts() throws Exception {
        URL thisJarUrl         = JavaMainRun.class.getProtectionDomain().getCodeSource().getLocation();
        String thisJarName     =  new File( thisJarUrl.toURI() ).getName();
        String thisJarJustName = thisJarName.substring(0, thisJarName.lastIndexOf("."));

        // bat script
        String batScriptFileName    = thisJarJustName + ".bat";
        File batScriptFile          = new File( batScriptFileName ).getCanonicalFile();
        if ( !batScriptFile.exists() ) {
            String sDefaultBatFileContent = new Scanner(
                    JavaMainRun.class.getResourceAsStream("Default.bat")
            ).useDelimiter("\\A").next();
            FileOutputStream batFileOutputStream = new FileOutputStream(batScriptFile);
            batFileOutputStream.write( sDefaultBatFileContent.getBytes() );
            batFileOutputStream.flush();
            batFileOutputStream.close();
        }

        // cmd script
        batScriptFileName    = thisJarJustName + ".cmd";
        batScriptFile          = new File( batScriptFileName ).getCanonicalFile();
        if ( !batScriptFile.exists() ) {
            String sDefaultBatFileContent = new Scanner(
                    JavaMainRun.class.getResourceAsStream("Default.cmd")
            ).useDelimiter("\\A").next();
            FileOutputStream batFileOutputStream = new FileOutputStream(batScriptFile);
            batFileOutputStream.write( sDefaultBatFileContent.getBytes() );
            batFileOutputStream.flush();
            batFileOutputStream.close();
        }

        // bash script
        String bashScriptFileName    = thisJarJustName;
        File bashScriptFile          = new File( bashScriptFileName ).getCanonicalFile();
        if ( !bashScriptFile.exists() ) {
            String sDefaultBashFileContent = new Scanner(
                    JavaMainRun.class.getResourceAsStream("Default.sh")
            ).useDelimiter("\\A").next().replace("\r", "");
            FileOutputStream bashFileOutputStream = new FileOutputStream(bashScriptFile);
            bashFileOutputStream.write( sDefaultBashFileContent.getBytes() );
            bashFileOutputStream.flush();
            bashFileOutputStream.close();
            // ### make executable ###
            bashScriptFile.setExecutable(true);
        }
    }


    public static void main(String[] args) throws Exception {
        URL thisJarUrl = JavaMainRun.class.getProtectionDomain().getCodeSource().getLocation();
        File thisJarFile = new File( thisJarUrl.toURI() ).getCanonicalFile();
        File thisDirFile = new File( "." ).getCanonicalFile();

        boolean isWorkingDirSameAsJarDir = thisDirFile.getCanonicalPath()
                .equalsIgnoreCase(thisJarFile.getParentFile().getCanonicalPath());

        // System.out.println("### isWorkingDirSameAsJarDir: " + isWorkingDirSameAsJarDir);

        boolean missingLocalJars = calcMissingLocalJars();
        if (missingLocalJars) {
            try {
                ProcessBuilder pb = new ProcessBuilder( "java", "-jar", thisJarFile.toString(), "install" );
                pb.start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        String thisJarPath = new File( thisJarUrl.toURI() ).getPath();
        String thisJarName =  new File( thisJarUrl.toURI() ).getName();

        String thisJarJustName = thisJarName.substring(0, thisJarName.lastIndexOf("."));

        String arg0 = "";
        if ( args.length >=1 ) {
            arg0 = args[0].trim();
        }
        String groovyScriptFileName = thisJarJustName + ".groovy";
        if ( arg0.endsWith(".groovy") ) {
            groovyScriptFileName = arg0;
        }

        // external script file has precedence over script file in jar/classpath
        File groovyScriptFile = new File( groovyScriptFileName ).getCanonicalFile();
        URL groovyScriptUrl;
        if ( groovyScriptFile.exists() ) {
            groovyScriptUrl = groovyScriptFile.toURI().toURL();
        } else {
            groovyScriptUrl = JavaMainRun.class.getResource( groovyScriptFileName );

            //if ( groovyScriptUrl == null ) {
            String scriptResourceName = (groovyScriptUrl == null) ? "Default.groovy" : groovyScriptFileName ;

            String sGroovyCode = new Scanner(
                    JavaMainRun.class.getResourceAsStream( scriptResourceName )
            ).useDelimiter("\\A").next();

            // create temp groovy script file
            File tmpGroovyFile = File.createTempFile(
                    // "TmpDefault"
                    thisJarJustName + "_tmp_" // stops java Prefix string too short error
                    , ".groovy");

            FileOutputStream fos = new FileOutputStream(tmpGroovyFile);
            fos.write(sGroovyCode.getBytes());
            fos.flush();
            fos.close();

            groovyScriptUrl = tmpGroovyFile.toURI().toURL();
        }

        String sGroovyCode = new Scanner( groovyScriptUrl.openStream() ).useDelimiter("\\A").next();

        boolean sysPropCreateStartScriptsExists =  System.getProperty("CreateStartScripts") != null;
        String  sysPropCreateStartScriptsValue = "";
        if (sysPropCreateStartScriptsExists) {
            sysPropCreateStartScriptsValue = System.getProperty("CreateStartScripts").trim();
        }
        boolean sysPropCreateStartScripts = sysPropCreateStartScriptsValue.trim().toLowerCase().equals("true");

        // ### createStartScripts ###
        if ( isWorkingDirSameAsJarDir ) {
            if (sGroovyCode.contains("#CreateStartScripts=true#") || sysPropCreateStartScripts) {
                createStartScripts();
            }
        }

        URL[] urls = missingLocalJars ? getMavenUrls() : getLocalIvyJarUrls() ;
        URLClassLoader cl = new URLClassLoader(urls);

        Thread.currentThread().setContextClassLoader(cl);
        Class<?> groovyMainCls = cl.loadClass("groovy.ui.GroovyMain");
        Method groovyMainMethod = groovyMainCls.getMethod("main", String[].class);

        ArrayList<String> argsList = new ArrayList<String>();
        argsList.add( groovyScriptUrl.toString() );
        argsList.addAll( Arrays.asList(args) );

        String[] mainArgs = argsList.toArray( new String[argsList.size()] );

        groovyMainMethod.invoke(null, (Object) mainArgs ); // because of var args cast as object
    }
}
