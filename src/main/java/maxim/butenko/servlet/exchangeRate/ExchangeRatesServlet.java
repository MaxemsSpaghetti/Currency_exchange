package maxim.butenko.servlet.exchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import maxim.butenko.utils.ErrorResponse;
import maxim.butenko.dto.ExchangeRateDTO;
import maxim.butenko.service.ExchangeRateService;
import org.sqlite.SQLiteErrorCode;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            List<ExchangeRateDTO> exchangeRateDTOS = exchangeRateService.findAll();
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeRateDTOS);
            PrintWriter printWriter = resp.getWriter();
            printWriter.write(json);
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "the database is unavailable now, sorry("));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        BigDecimal rate;
        try {
            rate = BigDecimal.valueOf(Double.parseDouble(req.getParameter("rate")));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "rate is required"));
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
            Optional<ExchangeRateDTO> newExchangeRate = exchangeRateService.create(
                    baseCurrencyCode, targetCurrencyCode, rate);
            if (newExchangeRate.isPresent()) {
                ExchangeRateDTO exchangeRateDTO = newExchangeRate.get();
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeRateDTO);
                resp.getWriter().write(json);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                        HttpServletResponse.SC_CONFLICT,
                        "Currency pair with this code already exists"));

            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "the database is unavailable now, sorry("));
            }
        }
    }
}
