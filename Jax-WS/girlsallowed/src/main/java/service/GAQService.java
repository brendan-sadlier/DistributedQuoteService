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
public class GAQService extends AbstractQuotationService {
	// All references are to be prefixed with an GA (e.g. GA001000)
	public static final String PREFIX = "GA";
	public static final String COMPANY = "Girls Allowed Inc.";

	public void advertiseService() {

		try {

			JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
			ServiceInfo serviceInfo = ServiceInfo.create("_quote._tcp.local.", "girlsallowed", 9003, "path=/quotations");
			jmdns.registerService(serviceInfo);
			System.out.println("Service advertised" + "("+ serviceInfo.getType() + "): " + serviceInfo.getName() + " running on " + serviceInfo.getPort() + " with path=" + serviceInfo.getNiceTextString());

		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	/**
	 * Quote generation:
	 * 50% discount for being female
	 * 30% surcharge for being a mam
	 * upto 20% discount for being low BMI
	 * 20% penalty for high BMI
	 * 20% penalty for Medical Issues
	 * 40% penalty for smoking
	 */
	@Override
	public Quotation generateQuotation(ClientInfo info) {
		// Create an initial quotation between 600 and 1000
		double price = generatePrice(600, 400);
		
		// Automatic 50% discount for being female
		int discount = (info.gender == ClientInfo.FEMALE) ? 50:-30;
		
		// Add a points discount
		discount += bmiDiscount(info);

		// Apply a medical weighting
		discount += medicalWeighting(info);
			
		// Generate the quotation and send it back
		return new Quotation(COMPANY, generateReference(PREFIX), (price * (100-discount)) / 100);
	}

	public int bmiDiscount(ClientInfo info) {
		double bmi = this.bmi(info.weight, info.height);
		if (bmi < 18.5) return 20;
		if (bmi < 24.5) return 10;
		if (bmi < 30) return 0;
		if (bmi < 40) return -20;
		return 40;
	}

	public int medicalWeighting(ClientInfo info) {
		int weighting = 0;
		if (info.medicalIssues) weighting-=20;
		if (info.smoker) weighting -= 40;
		return weighting;
	}

}
