import service.GAQService;

import javax.xml.ws.Endpoint;

public class Main {

    public static void main(String[] args) {
        GAQService gaqService = new GAQService();
        Endpoint.publish("http://0.0.0.0:9003/quotations", gaqService);
        gaqService.advertiseService();
        System.out.println("GAQ service started...");
    }
}
