/**
 * Created with IntelliJ IDEA.
 * User: ltoenjes
 * Date: 2/5/13
 * Time: 2:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class JavaMain {

    public static void main(String[] args) throws Exception {
        System.setProperty("start.time", ( (Long) System.currentTimeMillis() ).toString() );

        boolean installMode = (args.length > 0) ? args[0].equals("install") : false ;
        if ( installMode ) {
            JavaMainInstall.main(args);
        } else {
            JavaMainRun.main(args);
        }
    }

}
