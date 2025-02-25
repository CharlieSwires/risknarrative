package com.charlie.truproxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.charlie.truproxy.CompanyEntity.Address;
import com.charlie.truproxy.CompanyEntity.OfficerEntity;
import com.charlie.truproxy.CompanyRequest.Officer;

@Service
public class TruProxyService {

	@Autowired
	private WebClient webClient;
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private OfficerRepository officerRepository;

	private static final String BASE_URL = "https://exercise.trunarrative.cloud/TruProxyAPI/rest/Companies/v1";

	public CompanyResponse putCompany(CompanyRequest request, String apiKey) {
		//System.out.println(request.toString());
		CompanyEntity company = new CompanyEntity();

		Address address = new Address();
		if (request.getAddress() != null) {
			address.setAddressLine1(request.getAddress().getAddressLine1());
			address.setCountry(request.getAddress().getCountry());
			address.setLocality(request.getAddress().getLocality());
			address.setPostalCode(request.getAddress().getPostalCode());
			address.setPremises(request.getAddress().getPremises());
		}
		List<OfficerEntity> officers = new ArrayList<>();
		if (request.getOfficers() != null) {
			for (Officer officer: request.getOfficers()) {
				OfficerEntity item = new OfficerEntity();
				if (officer.getAddress() != null) {
					item.setAddress(new Address(officer.getAddress().getLocality(), 
							officer.getAddress().getPostalCode(), 
							officer.getAddress().getPremises(), 
							officer.getAddress().getAddressLine1(), 
							officer.getAddress().getCountry()));
				} else {
					new Address();
				}
				item.setAppointedOn(officer.getAppointedOn());
				//item.setCompany(company);
				item.setName(officer.getName());
				item.setOfficerRole(officer.getOfficerRole());
				item.setCompany(company);
				officers.add(item);

			}
		} else {
			officers = null;
		}
		company.setOfficers(officers);
		company.setTitle(request.getTitle());
		company.setCompanyNumber(request.getCompanyNumber());
		company.setCompanyType(request.getCompanyType());
		company.setCompanyStatus(request.getCompanyStatus());
		company.setDateOfCreation(request.getDateOfCreation());
		company.setAddress(address);

		companyRepository.save(company);
		if (company.getOfficers() != null) {
			for (OfficerEntity officer : company.getOfficers()) {
				officer.setCompany(company); 
		    }
			officerRepository.saveAll(company.getOfficers());
		}

		//System.out.println(company.toString());
		return companyEntityToResponse(company);

	}
	private CompanyResponse companyEntityToResponse(CompanyEntity company) {
		CompanyResponse response = new CompanyResponse();
		List<CompanyDetails> details = new ArrayList<>();
		List<OfficerDetails> officers = new ArrayList<>();
		for (OfficerEntity item : company.getOfficers()) {
			OfficerDetails officer = new OfficerDetails();
			if (item.getAddress() != null) {
				officer.setAddress(new AddressDetails(item.getAddress().getLocality(), 
						item.getAddress().getPostalCode(), 
						item.getAddress().getPremises(), 
						item.getAddress().getAddressLine1(), 
						item.getAddress().getCountry()));
			} else {
				officer.setAddress(null);
			}
			officer.setAppointedOn(item.getAppointedOn());
			officer.setName(item.getName());
			officer.setOfficerRole(item.getOfficerRole());
			officer.setResignedOn(item.getResignedOn());
			officers.add(officer);
		}
		CompanyDetails comp = new CompanyDetails();
		comp.setAddress(new AddressDetails(company.getAddress().getLocality(), 
				company.getAddress().getPostalCode(), company.getAddress().getPremises(), 
				company.getAddress().getAddressLine1(), company.getAddress().getCountry()));
		comp.setCompanyNumber(company.getCompanyNumber());
		comp.setCompanyStatus(company.getCompanyStatus());
		comp.setCompanyType(company.getCompanyType());
		comp.setDateOfCreation(company.getDateOfCreation());
		comp.setTitle(company.getTitle());
		comp.setOfficers(officers);
		details.add(comp);
		response.setTotalResults(details.size());
		response.setItems(details);

		return response;
	}
	public CompanyResponse searchCompany(CompanySearchRequest request, String apiKey, boolean onlyActive) {
	    String url = request.getCompanyNumber() != null ? 
	                 BASE_URL + "/Officers?CompanyNumber=" + request.getCompanyNumber() :
	                 BASE_URL + "/Search?Query=" + request.getCompanyName();

	    //System.out.println("Calling TruProxy API: " + url);

	    CompanyResponse response = webClient.get()
	            .uri(url)
	            .header("x-api-key", apiKey)
	            .retrieve()
	            .bodyToMono(CompanyResponse.class) 
	            .block();

	    //System.out.println("API Response: " + response);
	    return response;
	}

	public CompanyResponse searchDBCompany(CompanySearchRequest request, String apiKey, boolean onlyActive) {
		//System.out.println("========"+request.toString());
		List<CompanyEntity> result = new ArrayList<>();
		if (request.getCompanyName() != null) {
			result = companyRepository.findByTitleIgnoreCase(request.getCompanyName());
		}
		if (request.getCompanyNumber() != null) {
			result = new ArrayList<>();
			Optional<CompanyEntity> res = companyRepository.findByCompanyNumber(request.getCompanyNumber());
			if (res.isPresent() && !res.isEmpty()) {
				result.add(res.get());
			}
		}
		CompanyResponse resp = new CompanyResponse();
		List<CompanyDetails> details = new ArrayList<>();
		List<OfficerDetails> officers = new ArrayList<>();
		CompanyDetails company = new CompanyDetails();
		AddressDetails address = new AddressDetails();
		for (CompanyEntity item : result) {
			if (item.getAddress() != null) {
				address.setAddressLine1(item.getAddress().getAddressLine1());
				address.setCountry(item.getAddress().getCountry());
				address.setLocality(item.getAddress().getLocality());
				address.setPostalCode(item.getAddress().getPostalCode());
				address.setPremises(item.getAddress().getPremises());
			}
			List<OfficerEntity> os = officerRepository.findAllByCompanyId(item.getId());
			//List<OfficerEntity> os = item.getOfficers();
			if (os != null) {
				//System.out.println("$$$$$$$$$$$$$$$$$$$$"+os.size());
				for (OfficerEntity officer: os) {
					OfficerDetails o = new OfficerDetails();
					if (officer.getAddress() != null) {
						o.setAddress(new AddressDetails(officer.getAddress().getLocality(), 
								officer.getAddress().getPostalCode(), 
								officer.getAddress().getPremises(), 
								officer.getAddress().getAddressLine1(), 
								officer.getAddress().getCountry()));
					} else {
						o.setAddress(null);
					}
					o.setAppointedOn(officer.getAppointedOn());
					o.setName(officer.getName());
					o.setOfficerRole(officer.getOfficerRole());
					if (officer.getResignedOn() == null) {
						officers.add(o);
					}

				}
			} else {
				officers = null;
			}

			company.setTitle(item.getTitle());
			company.setCompanyNumber(item.getCompanyNumber());
			company.setCompanyType(item.getCompanyType());
			company.setCompanyStatus(item.getCompanyStatus());
			company.setDateOfCreation(item.getDateOfCreation());
			company.setAddress(address);
			company.setOfficers(officers);
			if (!onlyActive && !item.getCompanyStatus().equals("active")) {
				details.add(company);
			} else if (onlyActive && item.getCompanyStatus().equals("active")) {
				details.add(company);
			}
		}
		resp.setTotalResults(details.size());
		resp.setItems(details);
		//System.out.println("#################"+resp.toString());

		return resp;
	}

}
