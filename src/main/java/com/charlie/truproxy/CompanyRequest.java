package com.charlie.truproxy;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


public class CompanyRequest {

	public CompanyRequest() {}
	
	public CompanyRequest(String companyNumber, String companyType, String title, String companyStatus,
			String dateOfCreation, Address address, List<Officer> officers) {
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
	private Address address;
	private List<Officer> officers;

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

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<Officer> getOfficers() {
		return officers;
	}

	public void setOfficers(List<Officer> officers) {
		this.officers = officers;
	}
	@Override
	public String toString() {
		return "CompanyEntity [companyNumber=" + companyNumber + ", companyType=" + companyType + ", title=" + title
				+ ", companyStatus=" + companyStatus + ", dateOfCreation=" + dateOfCreation + ", address=" + address
				+ ", officers=" + officers + "]";
	}


	static class Address {
		public Address() {}
		public Address(String locality, String postalCode, String premises, String addressLine1, String country) {
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
		@JsonProperty("address_line_1")
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

	static class Officer {
		public Officer(String name, String officerRole, String appointedOn, String resignedOn, Address address,
				String company) {
			super();
			this.name = name;
			this.officerRole = officerRole;
			this.appointedOn = appointedOn;
			this.resignedOn = resignedOn;
			this.address = address;
		}
		public Officer() {}

		private String name;
		@JsonProperty("officer_role")
		private String officerRole;
		@JsonProperty("appointed_on")
		private String appointedOn;
		@JsonProperty("resigned_on")
		private String resignedOn;

		private Address address;


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

		public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}

		@Override
		public String toString() {
			return "Officer [" + ", name=" + name + ", officerRole=" + officerRole + ", appointedOn="
					+ appointedOn + ", resignedOn=" + resignedOn + ", address=" + address + "]";
		}
		public String getResignedOn() {
			return resignedOn;
		}
		public void setResignedOn(String resignedOn) {
			this.resignedOn = resignedOn;
		}
	}
}

