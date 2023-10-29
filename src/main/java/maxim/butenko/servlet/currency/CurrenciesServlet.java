package maxim.butenko.servlet.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import maxim.butenko.dto.CurrencyDTO;
import maxim.butenko.service.CurrencyService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;


@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        List<CurrencyDTO> currencies = currencyService.findAll();

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currencies);

        try (var printWriter = resp.getWriter()) {
            printWriter.write(json);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");

        String fullName = req.getParameter("fullName");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        Optional<CurrencyDTO> newCurrency = currencyService.create(fullName, code, sign);
        if (newCurrency.isPresent()) {
            var currencyDTO = newCurrency.get();
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currencyDTO);
            try (var printWriter = resp.getWriter()) {
                printWriter.write(json);
            }
        }
    }
}
