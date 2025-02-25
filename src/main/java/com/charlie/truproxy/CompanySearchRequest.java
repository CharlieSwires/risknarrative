package com.charlie.truproxy;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanySearchRequest {
	public CompanySearchRequest() {}
    public CompanySearchRequest(String companyName, String companyNumber) {
		super();
		this.companyName = companyName;
		this.companyNumber = companyNumber;
	}
	private String companyName;
    private String companyNumber;
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyNumber() {
		return companyNumber;
	}
	public void setCompanyNumber(String companyNumber) {
		this.companyNumber = companyNumber;
	}
	@Override
	public String toString() {
		return "CompanySearchRequest [companyName=" + companyName + ", companyNumber=" + companyNumber + "]";
	}
}

class CompanyResponse {
	public CompanyResponse() {}
    public CompanyResponse(int totalResults, List<CompanyDetails> items) {
		super();
		this.totalResults = totalResults;
		this.items = items;
	}
    @JsonProperty("total_results")
    private int totalResults;
    private List<CompanyDetails> items;
	public int getTotalResults() {
		return totalResults;
	}
	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}
	public List<CompanyDetails> getItems() {
		return items;
	}
	public void setItems(List<CompanyDetails> items) {
		this.items = items;
	}
	@Override
	public String toString() {
		return "CompanyResponse [totalResults=" + totalResults + ", items=" + items + "]";
	}
}

class CompanyDetails {
	public CompanyDetails() {}
    public CompanyDetails(String companyNumber, String companyType, String title, String companyStatus,
			String dateOfCreation, AddressDetails address, List<OfficerDetails> officers) {
		super();
		this.companyNumber = companyNumber;
		this.companyType = companyType;
		this.title = title;
		this.companyStatus = companyStatus;
		this.dateOfCreation = dateOfCreation;
		this.address = address;
		this.officers = officers;
	}
    @JsonProperty("company_number")
	private String companyNumber;
    @JsonProperty("company_type")
    private String companyType;
    private String title;
    @JsonProperty("company_status")
    private String companyStatus;
    @JsonProperty("date_of_creation")
    private String dateOfCreation;
    private AddressDetails address;
    private List<OfficerDetails> officers;
	public String getCompanyNumber() {
		return companyNumber;
	}
	public void setCompanyNumber(String companyNumber) {
		this.companyNumber = companyNumber;
	}
	public String getCompanyType() {
		return companyType;
	}
	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCompanyStatus() {
		return companyStatus;
	}
	public void setCompanyStatus(String companyStatus) {
		this.companyStatus = companyStatus;
	}
	public String getDateOfCreation() {
		return dateOfCreation;
	}
	public void setDateOfCreation(String dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}
	public AddressDetails getAddress() {
		return address;
	}
	public void setAddress(AddressDetails address2) {
		this.address = address2;
	}
	public List<OfficerDetails> getOfficers() {
		return officers;
	}
	public void setOfficers(List<OfficerDetails> officers) {
		this.officers = officers;
	}
	@Override
	public String toString() {
		return "CompanyDetails [companyNumber=" + companyNumber + ", companyType=" + companyType + ", title=" + title
				+ ", companyStatus=" + companyStatus + ", dateOfCreation=" + dateOfCreation + ", address=" + address
				+ ", officers=" + officers + "]";
	}
}

class AddressDetails {
	public AddressDetails() {}
    public AddressDetails(String locality, String postalCode, String premises, String addressLine1, String country) {
		super();
		this.locality = locality;
		this.postalCode = postalCode;
		this.premises = premises;
		this.addressLine1 = addressLine1;
		this.country = country;
	}
	private String locality;
    @JsonProperty("postal_code")
    private String postalCode;
    private String premises;
    @JsonProperty("sddress_line_1")
    private String addressLine1;
    private String country;
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getPremises() {
		return premises;
	}
	public void setPremises(String premises) {
		this.premises = premises;
	}
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	@Override
	public String toString() {
		return "Address [locality=" + locality + ", postalCode=" + postalCode + ", premises=" + premises
				+ ", addressLine1=" + addressLine1 + ", country=" + country + "]";
	}
}

class OfficerDetails {
	public OfficerDetails() {}
    public OfficerDetails(String name, String officerRole, String appointedOn, AddressDetails address) {
		super();
		this.name = name;
		this.officerRole = officerRole;
		this.appointedOn = appointedOn;
		this.address = address;
	}
	private String name;
    @JsonProperty("officer_role")
    private String officerRole;
    @JsonProperty("appointed_on")
    private String appointedOn;
    @JsonProperty("resigned_on")
    private String resignedOn;
    private AddressDetails address;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOfficerRole() {
		return officerRole;
	}
	public void setOfficerRole(String officerRole) {
		this.officerRole = officerRole;
	}
	public String getAppointedOn() {
		return appointedOn;
	}
	public void setAppointedOn(String appointedOn) {
		this.appointedOn = appointedOn;
	}
	public AddressDetails getAddress() {
		return address;
	}
	public void setAddress(AddressDetails address) {
		this.address = address;
	}
	@Override
	public String toString() {
		return "OfficerDetails [name=" + name + ", officerRole=" + officerRole + ", appointedOn=" + appointedOn
				+ ", resignedOn=" + resignedOn + ", address=" + address + "]";
	}
	public void setResignedOn(String resignedOn) {
		this.resignedOn = resignedOn;
		
	}	
	public String getResignedOn() {
		return this.resignedOn;
		
	}
}
