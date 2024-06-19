package service;

import java.net.InetAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import service.core.BrokerService;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuotationService;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceEvent;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

@WebService(name = "BrokerService", targetNamespace = "http://core.service/", serviceName = "BrokerService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public class LocalBrokerService implements BrokerService {

    private Map<String, String> quoteURLs = new HashMap<>();

    private Map<String, String> getQuoteURLS() {
        return quoteURLs;
    }

    private void discoverServices() {
        try {
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

            // Create a service listener
            jmdns.addServiceListener("_quote._tcp.local.", new ServiceListener() {
                @Override
                public void serviceAdded(ServiceEvent serviceEvent) {
                }

                @Override
                public void serviceRemoved(ServiceEvent serviceEvent) {
                    String serviceName = serviceEvent.getInfo().getName();
                    quoteURLs.remove(serviceName);
                    System.out.println("Removed service: " + serviceName);

                }

                @Override
                public void serviceResolved(ServiceEvent serviceEvent) {
                    String url = "http://" + serviceEvent.getInfo().getHostAddresses()[0] + ":" + serviceEvent.getInfo().getPort() + "/quotations";
                    String serviceName = serviceEvent.getInfo().getName();
                    quoteURLs.put(serviceName ,url);
                    System.out.println("Added service " + serviceName + " at " + url);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LocalBrokerService () {
        discoverServices();
    }

    @WebMethod
    public LinkedList<Quotation> getQuotations(ClientInfo info)  {

        LinkedList<Quotation> quotations = new LinkedList<>();


        for (Map.Entry<String, String> entry : getQuoteURLS().entrySet()) {

            String serviceName = entry.getKey();
            String url = entry.getValue();

            try {
                URL wsdlUrl = new URL(url);
                QName SERVICE_NAME = new QName("http://core.service/", "QuotationService");
                Service service = Service.create(wsdlUrl, SERVICE_NAME);
                QName PORT_NAME = new QName("http://core.service/", "QuotationServicePort");
                QuotationService quotationService = service.getPort(PORT_NAME, QuotationService.class);
                Quotation quotation = quotationService.generateQuotation(info);
                quotations.add(quotation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return quotations;
    }
}
