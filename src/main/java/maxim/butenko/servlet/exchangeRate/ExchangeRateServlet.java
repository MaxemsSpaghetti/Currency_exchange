package maxim.butenko.servlet.exchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import maxim.butenko.ErrorResponse;
import maxim.butenko.dto.ExchangeRateDTO;
import maxim.butenko.service.ExchangeRateService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();

        if (pathInfo.length() != 7 || !pathInfo.matches("[A-Z/]{7}")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Incorrect currency code entry. " +
                            "Each currency must consist of 3 letters in uppercase"));
            return;
        }

        String baseCurrencyCode = pathInfo.substring(1, 4);
        String targetCurrencyCode = pathInfo.substring(4);

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "baseCurrencyCode and targetCurrencyCode cannot be the same"));
            return;
        }
        try {
            Optional<ExchangeRateDTO> exchangeRateByCodes = exchangeRateService.findByCodes(
                    baseCurrencyCode, targetCurrencyCode);
            if (exchangeRateByCodes.isPresent()) {
                ExchangeRateDTO exchangeRate = exchangeRateByCodes.get();
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeRate);
                resp.getWriter().write(json);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Exchange rate for the pair was not found"));
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "the database is unavailable now, sorry("));
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();

        String baseCurrencyCode = pathInfo.substring(1, 4);
        String targetCurrencyCode = pathInfo.substring(4, 7);
        Double rate;

        try {
            rate = Double.valueOf(req.getParameter("rate"));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "rate is required"));
            return;
        }

        if (rate < 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "rate must be positive"));
            return;

        } else if (baseCurrencyCode.equals(targetCurrencyCode)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "baseCurrencyCode and targetCurrencyCode cannot be the same"));
            return;
        }

        try {
            Optional<ExchangeRateDTO> updateExchangeRate = exchangeRateService.update(
                    baseCurrencyCode, targetCurrencyCode, rate);
            if (updateExchangeRate.isPresent()) {
                ExchangeRateDTO exchangeRate = updateExchangeRate.get();
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeRate);
                resp.getWriter().write(json);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Currency pair is missing in the database"));
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "the database is unavailable now, sorry("));
        }
    }
}
