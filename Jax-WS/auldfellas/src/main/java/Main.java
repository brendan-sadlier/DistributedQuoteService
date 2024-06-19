import service.AFQService;

import javax.xml.ws.Endpoint;

public class Main {

    public static void main(String[] args) {
        AFQService afqService = new AFQService();
        Endpoint.publish("http://0.0.0.0:9001/quotations", afqService);
        afqService.advertiseService();
        System.out.println("AFQ service started...");
    }
}
