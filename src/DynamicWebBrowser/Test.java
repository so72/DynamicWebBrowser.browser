package DynamicWebBrowser;

import DynamicWebBrowser.protocols.Protocol;
import DynamicWebBrowser.protocols.ProtocolFinder;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author Steffen Olson
 */
public class Test {
    
    public static void main(String args[]) {
        ProtocolFinder finder = new ProtocolFinder();
        
        Protocol time = finder.findProtocol("time");
        if (time != null) {
            try {
                System.out.println(time.execute(new URI("time")));
            } catch (URISyntaxException ex) {
                System.out.println("Invalid URI");
            }
        } else {
            System.out.println("Protocol not implemented");
        }
    }
}
