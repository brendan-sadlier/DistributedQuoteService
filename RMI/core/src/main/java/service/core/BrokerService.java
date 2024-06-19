package service.core;

import java.rmi.Remote;
import java.util.List;

public interface BrokerService extends java.rmi.Remote {
	public List<Quotation> getQuotations(ClientInfo info) throws java.rmi.RemoteException;
	void registerService(String name, Remote service) throws java.rmi.RemoteException;
}
