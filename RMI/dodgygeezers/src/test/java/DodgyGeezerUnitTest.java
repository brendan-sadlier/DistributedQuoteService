import org.junit.BeforeClass;
import org.junit.Test;
import service.core.ClientInfo;
import service.core.Constants;
import service.core.Quotation;
import service.core.QuotationService;
import service.dodgygeezers.DGQService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DodgyGeezerUnitTest {

    private static Registry registry;

    @BeforeClass
    public static void setup() {

        QuotationService gaqService = new DGQService();

        try {
            registry = LocateRegistry.createRegistry(1099);
            System.out.println("Server started...");

            QuotationService quotationService = (QuotationService)
                    UnicastRemoteObject.exportObject(gaqService,0);

            registry.bind(Constants.DODGY_GEEZERS_SERVICE, quotationService);
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
    }

    @Test
    public void connectionTest() throws Exception {
        QuotationService service = (QuotationService)
                registry.lookup(Constants.DODGY_GEEZERS_SERVICE);
        assertNotNull(service);
    }

    @Test
    public void quotationTest() throws Exception {

        QuotationService service = (QuotationService)
                registry.lookup(Constants.DODGY_GEEZERS_SERVICE);
        ClientInfo info = new ClientInfo("Niki Collier", ClientInfo.FEMALE, 49, 1.5494, 80, false, false);

        Quotation quotation = service.generateQuotation(info);

        assertNotNull(quotation);
    }
    
}
