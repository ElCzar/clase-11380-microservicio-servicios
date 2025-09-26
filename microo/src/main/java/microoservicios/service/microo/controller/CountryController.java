package microoservicios.service.microo.controller;

import microoservicios.service.microo.entity.Country;
import microoservicios.service.microo.repository.CountryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {

    private final CountryRepository countryRepository;

    public CountryController(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    // Get all countries
    @GetMapping
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }
}
