package com.charlie.truproxy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/company")
public class CompanyController {
    
	private final TruProxyService truProxyService;
	
	public CompanyController(TruProxyService service) {
		truProxyService = service;
	}
    
    @PostMapping(path = "/search-proper", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CompanyResponse> searchCompany(
            @RequestBody CompanySearchRequest request,
            @RequestHeader(name = "x-api-key") String apiKey,
            @RequestParam(name = "onlyActive", defaultValue = "true") boolean onlyActive) {
        
        CompanyResponse response = truProxyService.searchCompany(request, apiKey, onlyActive);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping(path = "/search", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CompanyResponse> searchDBCompany(
            @RequestBody CompanySearchRequest request,
            @RequestHeader(name = "x-api-key") String apiKey,
            @RequestParam(name = "onlyActive", defaultValue = "true") boolean onlyActive) {
        
        CompanyResponse response = truProxyService.searchDBCompany(request, apiKey, onlyActive);
        return ResponseEntity.ok(response);
    }
    @PostMapping(path = "/put", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CompanyResponse> putCompany(
            @RequestBody CompanyRequest request,
            @RequestHeader(name = "x-api-key") String apiKey) {
        
        CompanyResponse response = truProxyService.putCompany(request, apiKey);
        return ResponseEntity.ok(response);
    }
}
