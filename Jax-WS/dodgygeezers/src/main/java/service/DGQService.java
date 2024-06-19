package service;

import service.core.AbstractQuotationService;
import service.core.ClientInfo;
import service.core.Quotation;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.net.InetAddress;

@WebService(name = "QuotationService", targetNamespace = "http://core.service/", serviceName = "QuotationService")
@SOAPBinding()
public class DGQService extends AbstractQuotationService {
	// All references are to be prefixed with an DG (e.g. DG001000)
	public static final String PREFIX = "DG";
	public static final String COMPANY = "Dodgy Geezers Corp.";

	public void advertiseService() {

		try {

			JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

			ServiceInfo serviceInfo = ServiceInfo.create("_quote._tcp.local.", "dodgygeezers", 9002, "path=/quotations");
			jmdns.registerService(serviceInfo);

			System.out.println("Service advertised" + "("+ serviceInfo.getType() + "): " + serviceInfo.getName() + " running on " + serviceInfo.getPort() + " with path=" + serviceInfo.getNiceTextString());

		} catch (Exception e) {
			System.out.println("Service could not be advertised");
		}

	}

	
	/**
	 * Quote generation:
	 * 10% discount for smoking
	 * up to 20% discount for high BMI
	 * no penality for medical issues
	 */
	@Override
	public Quotation generateQuotation(ClientInfo info) {
		// Create an initial quotation between 800 and 1000
		double price = generatePrice(800, 200);
		
		int discount = bmiDiscount(info);	
		if (info.smoker) discount += 10;
	
		// Generate the quotation and send it back
		return new Quotation(COMPANY, generateReference(PREFIX), (price * (100-discount)) / 100);
	}


	public int bmiDiscount(ClientInfo info) {
		double bmi = this.bmi(info.weight, info.height);
		if (bmi < 18.5) return 0;
		if (bmi < 24.5) return 5;
		if (bmi < 30) return 10;
		if (bmi < 40) return 15;
		return 20;
	}
}
