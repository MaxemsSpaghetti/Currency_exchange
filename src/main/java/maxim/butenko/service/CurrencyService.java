package maxim.butenko.service;

import maxim.butenko.dto.CurrencyDTO;

import java.util.List;

public interface CurrencyService {

    List<CurrencyDTO> findAll();
}
