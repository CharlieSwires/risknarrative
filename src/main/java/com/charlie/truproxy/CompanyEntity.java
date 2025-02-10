package com.charlie.truproxy;


import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "companies")
public class CompanyEntity {

	public CompanyEntity() {}
	
	public CompanyEntity(String companyNumber, String companyType, String title, String companyStatus,
			String dateOfCreation, Address address, List<OfficerEntity> officers) {
		super();
		this.companyNumber = companyNumber;
		this.companyType = companyType;
		this.title = title;
		this.companyStatus = companyStatus;
		this.dateOfCreation = dateOfCreation;
		this.address = address;
		this.officers = officers;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
    private String id;
	private String companyNumber;
	private String companyType;
	private String title;
	private String companyStatus;
	private String dateOfCreation;
	
	@Embedded
	private Address address;

	@OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)  
	private List<OfficerEntity> officers;
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

	public List<OfficerEntity> getOfficers() {
		return officers;
	}

	public void setOfficers(List<OfficerEntity> officers) {
		this.officers = officers;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "CompanyEntity [id=" + id + ", companyNumber=" + companyNumber + ", companyType=" + companyType
				+ ", title=" + title + ", companyStatus=" + companyStatus + ", dateOfCreation=" + dateOfCreation
				+ ", address=" + address + ", officers=" + officers + "]";
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
		private String postalCode;
		private String premises;
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

	@Entity
	@Table(name = "officers")
	static class OfficerEntity {
		public OfficerEntity() {}
		public OfficerEntity(String id, String name, String officerRole, String appointedOn, Address address,
				String resigned) {
			super();
			this.id = id;
			this.name = name;
			this.officerRole = officerRole;
			this.appointedOn = appointedOn;
			this.address = address;
			this.resignedOn = resigned;
		}

		@Id
		@GeneratedValue(strategy = GenerationType.UUID)
		private String id;
		private String name;
		private String officerRole;
		private String appointedOn;
		private String resignedOn;

		@Embedded
		private Address address;
		
		@ManyToOne
		@JoinColumn(name = "company_id", nullable = false)
		private CompanyEntity company;
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

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
			return "OfficerEntity [id=" + id + ", name=" + name + ", officerRole=" + officerRole + ", appointedOn="
					+ appointedOn + ", resignedOn=" + resignedOn + ", address=" + address + "]";
		}
		public CompanyEntity getCompany() {
			return company;
		}
		public void setCompany(CompanyEntity company) {
			this.company = company;
		}
		public String getResignedOn() {
			return resignedOn;
		}
		public void setResignedOn(String resignedOn) {
			this.resignedOn = resignedOn;
		}
	}

}

