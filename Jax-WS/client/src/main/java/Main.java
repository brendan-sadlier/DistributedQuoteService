import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import service.core.BrokerService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuotationService;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class Main {

    /**
     * This is the starting point for the application. Here, we must
     * get a reference to the Broker Service and then invoke the
     * getQuotations() method on that service.
     *
     * Finally, you should print out all quotations returned
     * by the service.
     *
     * @param args
     */

    public static void main(String[] args) throws Exception {

        URL wsdlUrl = new URL("http://localhost:9000/broker" + "?wsdl");
        QName SERVICE_NAME = new QName("http://core.service/", "BrokerService");
        Service service = Service.create(wsdlUrl, SERVICE_NAME);
        QName PORT_NAME = new QName("http://core.service/", "BrokerServicePort");
        BrokerService brokerService = service.getPort(PORT_NAME, BrokerService.class);

        // Create the broker and run the test data
        for (ClientInfo info : clients) {

            displayProfile(info);

            // Retrieve quotations from the broker and display them...
            for(Quotation quotation : brokerService.getQuotations(info)) {
                displayQuotation(quotation);
            }

            // Print a couple of lines between each client
            System.out.println("\n");
        }
    }

    /**
     * Display the client info nicely.
     *
     * @param info
     */
    public static void displayProfile(ClientInfo info) {
        System.out.println("|=================================================================================================================|");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println(
                "| Name: " + String.format("%1$-29s", info.name) +
                        " | Gender: " + String.format("%1$-27s", (info.gender==ClientInfo.MALE?"Male":"Female")) +
                        " | Age: " + String.format("%1$-30s", info.age)+" |");
        System.out.println(
                "| Weight/Height: " + String.format("%1$-20s", info.weight+"kg/"+info.height+"m") +
                        " | Smoker: " + String.format("%1$-27s", info.smoker?"YES":"NO") +
                        " | Medical Problems: " + String.format("%1$-17s", info.medicalIssues?"YES":"NO")+" |");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println("|=================================================================================================================|");
    }

    /**
     * Display a quotation nicely - note that the assumption is that the quotation will follow
     * immediately after the profile (so the top of the quotation box is missing).
     *
     * @param quotation
     */
    public static void displayQuotation(Quotation quotation) {
        System.out.println(
                "| Company: " + String.format("%1$-26s", quotation.company) +
                        " | Reference: " + String.format("%1$-24s", quotation.reference) +
                        " | Price: " + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.price))+" |");
        System.out.println("|=================================================================================================================|");
    }

    /**
     * Test Data
     */
    public static final ClientInfo[] clients = {
            new ClientInfo("Peter Piper", ClientInfo.FEMALE, 49, 1.5494, 80, false, false),
            new ClientInfo("Old Geeza", ClientInfo.MALE, 65, 1.6, 100, true, true),
            new ClientInfo("Hannah Montana", ClientInfo.FEMALE, 21, 1.78, 65, false, false),
            new ClientInfo("Mickey Mouse", ClientInfo.MALE, 49, 1.8, 120, false, true),
            new ClientInfo("Lionel Messi", ClientInfo.MALE, 55, 1.9, 75, true, false),
            new ClientInfo("Donald Duck", ClientInfo.MALE, 35, 0.45, 1.6, false, false)
    };
}
