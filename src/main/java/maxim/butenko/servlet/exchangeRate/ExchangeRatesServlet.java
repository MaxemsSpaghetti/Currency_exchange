package maxim.butenko.servlet.exchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import maxim.butenko.dto.ExchangeRateDTO;
import maxim.butenko.service.ExchangeRateService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        List<ExchangeRateDTO> exchangeRateDTOS = exchangeRateService.findAll();
        var json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeRateDTOS);
        try (var printWriter = resp.getWriter()) {
            printWriter.write(json);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        Double rate = Double.valueOf(req.getParameter("rate"));

        Optional<ExchangeRateDTO> newExchangeRate = exchangeRateService.create(baseCurrencyCode, targetCurrencyCode, rate);
        if (newExchangeRate.isPresent()) {
            var exchangeRateDTO = newExchangeRate.get();
            var json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeRateDTO);
            try (var printWriter = resp.getWriter()) {
                printWriter.write(json);
            }
        }
    }
}
