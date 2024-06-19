package service.core;

public class Quotation {

	public Quotation(String company, String reference, double price) {
		this.company = company;
		this.reference = reference;
		this.price = price;
		
	}

	public Quotation() {}
	
	public String company;
	public String reference;
	public double price;
}
