import service.DGQService;

import javax.xml.ws.Endpoint;

public class Main {

    public static void main(String[] args) {

        DGQService dgqService = new DGQService();
        Endpoint.publish("http://0.0.0.0:9002/quotations", dgqService);
        dgqService.advertiseService();
        System.out.println("DGQ service started...");
    }
}
