import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar; 

public class Registrant implements Serializable{

	private String childName;
	private String carerName;
	private String birthDate;
	private String address;
	private String allergyInfo;
	private String phoneNumber;
	private int session;	
	private double fee;
	private int ageGroup;
	private LocalDate registerDate;
	private LocalDate invoiceDate;
	private LocalDate nextInvoiceDate;
	private LocalDate paymentDueDate;
	private boolean paid;

	public Registrant(String childName, String carerName, String birthDate, String address, String allergyInfo, 
			String phoneNumber, int session, int ageGroup, LocalDate registerDate, LocalDate invoiceDate, LocalDate nextInvoiceDate, 
			LocalDate paymentDueDate, boolean paid){
		this.childName = childName;
		this.carerName = carerName;
		this.birthDate = birthDate;
		this.address = address;
		this.allergyInfo = allergyInfo;
		this.phoneNumber = phoneNumber;
		this.session = session;
		this.ageGroup = ageGroup;
		this.registerDate = registerDate;
		this.invoiceDate = invoiceDate;
		this.nextInvoiceDate = nextInvoiceDate;
		this.paymentDueDate = paymentDueDate;
		this.paid = paid;
	}
	
	public LocalDate getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(LocalDate invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public LocalDate getPaymentDueDate() {
		return paymentDueDate;
	}

	public void setPaymentDueDate(LocalDate paymentDueDate) {
		this.paymentDueDate = paymentDueDate;
	}

	public int getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(int ageGroup) {
		this.ageGroup = ageGroup;
	}
	
	public String getChildName() {
		return childName;
	}
	public void setChildName(String childName) {
		this.childName = childName;
	}
	
	public String getCarerName() {
		return carerName;
	}
	public void setCarerName(String carerName) {
		this.carerName = carerName;
	}
	
	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getAllergyInfo() {
		return allergyInfo;
	}
	public void setAllergyInfo(String allergyInfo) {
		this.allergyInfo = allergyInfo;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public int getSession() {
		return session;
	}
	public void setSession(int session) {
		this.session = session;
	}
	
	public LocalDate getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(LocalDate registerDate) {
		this.registerDate = registerDate;
	}

	public double getFee() {
		return fee;
	}
	public void setFee(double fee) {
		this.fee = fee;
	}

	public LocalDate getNextInvoiceDate() {
		return nextInvoiceDate;
	}

	public void setNextInvoiceDate(LocalDate nextInvoiceDate) {
		this.nextInvoiceDate = nextInvoiceDate;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}
	
	
	
	
}
