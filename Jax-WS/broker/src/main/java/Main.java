import service.LocalBrokerService;

import javax.xml.ws.Endpoint;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        Endpoint.publish("http://0.0.0.0:9000/broker", new LocalBrokerService());
        System.out.println("Broker service started...");
    }

}
