package maxim.butenko.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import maxim.butenko.utils.ErrorResponse;
import maxim.butenko.model.CurrencyExchange;
import maxim.butenko.service.CurrencyExchangeService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/exchange")
public class CurrencyExchangeServlet extends HttpServlet {

    private final CurrencyExchangeService currencyExchangeService = CurrencyExchangeService.getInstance();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        BigDecimal amount;

        try {
            amount = BigDecimal.valueOf(Double.parseDouble(req.getParameter("amount")));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "amount is required"));
            return;

        }
        if (baseCurrencyCode.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "baseCurrencyCode is required"));
            return;

        } else if (targetCurrencyCode.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "targetCurrencyCode is required"));
            return;

        } else if (baseCurrencyCode.equals(targetCurrencyCode)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "baseCurrencyCode and targetCurrencyCode cannot be the same"));
            return;
        }

        try {
            Optional<CurrencyExchange> convertedCurrency = currencyExchangeService
                    .convertCurrency(baseCurrencyCode, targetCurrencyCode, amount);
            if (convertedCurrency.isPresent()) {
                CurrencyExchange currencyExchange = convertedCurrency.get();
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currencyExchange);
                resp.getWriter().write(json);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                        HttpServletResponse.SC_NOT_FOUND,
                        "There are no exchange rates " +
                                "for the specified currencies in the list of exchange rates"));
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "the database is unavailable now, sorry("));
        }
    }
}
