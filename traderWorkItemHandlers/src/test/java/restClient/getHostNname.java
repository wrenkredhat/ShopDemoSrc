package restClient;
	
 
import java.net.InetAddress;
import java.net.UnknownHostException;
 
/**
 * @author Crunchify.com
 */
 
public class getHostNname {
 
    public static void main(String[] args) {
 
        InetAddress ip;
        String hostname;
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            ip.getHostAddress();
            System.out.println("Your current IP address : " + ip);
            System.out.println("Your current Hostname : " + hostname);
            System.out.println("Your current HostAddress : " +  ip.getHostAddress());
        } catch (UnknownHostException e) {
 
            e.printStackTrace();
        }
    }
}
