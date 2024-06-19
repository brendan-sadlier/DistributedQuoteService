import org.apache.activemq.ActiveMQConnectionFactory;
import service.core.Quotation;
import service.message.ClientMessage;
import service.message.OfferMessage;
import service.message.QuotationMessage;

import javax.jms.*;
import java.util.HashMap;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) throws JMSException {

        String host = args.length == 0 ? "localhost":args[0];

        System.out.println("Connecting to ActiveMQ at: failover://tcp://activemq:61616");
        ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://"+host+":61616");
        Connection connection = factory.createConnection();
        connection.setClientID("broker");
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Queue offersQueue = session.createQueue("OFFERS");
        Queue quotationsQueue = session.createQueue("QUOTATIONS");
        Topic applicationsTopic = session.createTopic("APPLICATIONS");

        MessageConsumer applicationsConsumer = session.createConsumer(applicationsTopic);
        MessageConsumer quotationsConsumer = session.createConsumer(quotationsQueue);
        MessageProducer offersProducer = session.createProducer(offersQueue);

        System.out.println("Starting Broker...");
        connection.start();
        System.out.println("Broker started...");

        final HashMap<Long, LinkedList<Quotation>> quotationsCache = new HashMap<Long, LinkedList<Quotation>>();

        new Thread () {

            public void run () {

                try {
                    quotationsConsumer.setMessageListener(new MessageListener() {
                        public void onMessage(Message message) {
                            try {
                                QuotationMessage request = (QuotationMessage) ((ObjectMessage) message).getObject();
                                System.out.println("Received Quotation from: " + request.getQuotation().company);
                                LinkedList<Quotation> quotes = quotationsCache.get(request.getToken());

                                if (quotes == null) {
                                    quotes = new LinkedList<Quotation>();
                                }

                                quotes.add(request.getQuotation());
                                quotationsCache.put(request.getToken(), quotes);
                                System.out.println("Added Quotation from: " + request.getQuotation().company);
                                message.acknowledge();
                            } catch (Exception e) {
                                throw new RuntimeException();
                            }
                        }
                    });
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

        applicationsConsumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                try {
                    ClientMessage request = (ClientMessage) ((ObjectMessage) message).getObject();
                    System.out.println("Received Application for: " + request.getInfo().name);

                    new Thread(() -> {
                        try {
                            Thread.sleep(3000);
                            LinkedList<Quotation> quotes = new LinkedList<>();

                            if (quotationsCache.containsKey(request.getToken())) {
                                quotes = quotationsCache.get(request.getToken());
                            }

                            OfferMessage offerMessage = new OfferMessage(request.getInfo(), quotes);
                            Message response = session.createObjectMessage(offerMessage);
                            offersProducer.send(response);
                            message.acknowledge();
                            System.out.println("Sent Offer to Client for: " + request.getInfo().name);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                } catch (Exception e) {
                    throw new RuntimeException();
                }
            }
        });
    }
}
