import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

import service.core.BrokerService;
import service.dodgygeezers.DGQService;
import service.core.QuotationService;
import service.core.Constants;

public class Main {

	public static void main(String[] args) {
		
		QuotationService dgqService = new DGQService();
		try {
			// Connect to the RMI Registry - creating the registry will be the 
			// responsibility of the broker
			Registry registry = LocateRegistry.getRegistry("broker", 1099);

//            if (args.length == 0) {
//                registry = LocateRegistry.createRegistry(1099);
//            } else {
//                registry = LocateRegistry.getRegistry(args[0], 1099);
//            }

			// Create the Remote Object
//			QuotationService quotationService = (QuotationService)
//				UnicastRemoteObject.exportObject(dgqService, 0);

			QuotationService stub = (QuotationService) UnicastRemoteObject.exportObject(dgqService, 0);


			// Register the object with the RMI Registry
//			registry.bind(Constants.DODGY_GEEZERS_SERVICE, quotationService);

			BrokerService brokerService = (BrokerService) registry.lookup(Constants.BROKER_SERVICE);
			brokerService.registerService(Constants.DODGY_GEEZERS_SERVICE, stub);

			System.out.println("STOPPING SERVER SHUTDOWN");

			while (true) {
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			System.out.println("Trouble: " + e);

		}

	}

}