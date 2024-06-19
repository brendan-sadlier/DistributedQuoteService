package service.core;

public interface QuotationService extends java.rmi.Remote {
	public Quotation generateQuotation(ClientInfo info) throws java.rmi.RemoteException;
}
