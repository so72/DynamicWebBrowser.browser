package DynamicWebBrowser;

import DynamicWebBrowser.protocols.Protocol;
import DynamicWebBrowser.protocols.ProtocolFinder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author Mark, Steffen, Shane
 */
public class DynamicWebBrowser {

    /**
     * A complete Java class that demonstrates how to create an HTML viewer with
     * styles, using the JEditorPane, HTMLEditorKit, StyleSheet, and JFrame.
     *
     * @author alvin alexander, devdaily.com.
     *
     */
    public static void main(String[] args) {


        new DynamicWebBrowser();
    }

    public DynamicWebBrowser() {

        final ProtocolFinder finder = new ProtocolFinder();


        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // create jeditorpane
                final JEditorPane jEditorPane = new JEditorPane();

                // make it read-only
                jEditorPane.setEditable(true);

                // create a scrollpane; modify its attributes as desired
                JScrollPane scrollPane = new JScrollPane(jEditorPane);

                // add an html editor kit
                HTMLEditorKit kit = new HTMLEditorKit();
                jEditorPane.setEditorKit(kit);

//        // add some styles to the html
//        StyleSheet styleSheet = kit.getStyleSheet();
//        styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
//        styleSheet.addRule("h1 {color: blue;}");
//        styleSheet.addRule("h2 {color: #ff0000;}");
//        styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");

                // create some simple html as a string
                String htmlString = "<html>\n"
                        + "<body>\n"
                        + "<h1>Welcome!</h1>\n"
                        + "<h2>type a url above</h2>\n"
                        + "<p>like \"time:\\\" or \"time:\"</p>\n"
                        + "<p></p>\n"
                        + "</body>\n"
                        + "</html>\n";

                // create a document, set it on the jeditorpane, then add the html
                Document doc = kit.createDefaultDocument();
                jEditorPane.setDocument(doc);
                jEditorPane.setText(htmlString);


                // now add it all to a frame
                JFrame j = new JFrame("Dynamic Web Browser");
                final JTextField url = new JTextField();
                JButton go = new JButton("GO");
                j.add(url, BorderLayout.NORTH);
                j.add(go, BorderLayout.EAST);

                j.getContentPane().add(scrollPane, BorderLayout.CENTER);

                // make it easy to close the application
                j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // display the frame
                j.setSize(new Dimension(800, 600));

                // center the jframe, then make it visible
                j.setLocationRelativeTo(null);
                j.setVisible(true);




                //do the action stuff
                go.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        try {
                            String uriString = url.getText();

                            if (uriString.endsWith(":")) {
                                uriString += "/";
                            }

                            URI uri = new URI(uriString);

                            String htmlString = "";

                            if (!uri.isAbsolute() || uri.getScheme().equalsIgnoreCase("http")) {
                                // not absolute (no scheme: provided assume http)
                                // or http was specified
                                // TODO: after http is implemented deal with this
                                Protocol protocol = finder.findProtocol(uri.getScheme());
                                htmlString = protocol.execute(uri);

                            } else {
                                // otherwise a non http scheme was provided
                                Protocol protocol = finder.findProtocol(uri.getScheme());
                                if (protocol == null) {
                                    htmlString = "<html>\n"
                                            + "<body>\n"
                                            + "<h1>501 not implemented</h1>\n"
                                            + "<p>" + url.getText() + " has not beed implemented yet</p>\n"
                                            + "<p></p>\n"
                                            + "</body>\n"
                                            + "</html>\n";
                                    jEditorPane.setText(htmlString);
                                } else {
                                    htmlString = protocol.execute(uri);
                                }
                            }
                            jEditorPane.setText(htmlString);

                        }
                        catch (URISyntaxException ex) {
                            System.out.println("Bad URI");
                        }
                    }
                });
            }
        });
    }
}