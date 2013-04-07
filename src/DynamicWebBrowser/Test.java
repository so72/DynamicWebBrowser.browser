package DynamicWebBrowser;

import DynamicWebBrowser.protocols.Protocol;
import DynamicWebBrowser.protocols.ProtocolFinder;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Steffen Olson
 */
public class Test {
    
    public static void main(String args[]) {
        ProtocolFinder finder = new ProtocolFinder();
        URI uri = null;
        try {
            String uriString = "time:";
            if (uriString.endsWith(":")) uriString += "/";
            uri = new URI(uriString);
        } catch (URISyntaxException ex) {
            System.out.println("URI syntax");
            System.exit(1);
        }
        
        for (int i = 0; i < 2; i++) {
            Protocol time = finder.findProtocol(uri.getScheme());
            if (time != null) {
                System.out.println(time.execute(uri));
            } else {
                System.out.println("Protocol not implemented");
            }
        }
    }
}
