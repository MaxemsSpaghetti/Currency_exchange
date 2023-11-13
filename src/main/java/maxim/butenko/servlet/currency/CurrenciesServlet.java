package maxim.butenko.servlet.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import maxim.butenko.ErrorResponse;
import maxim.butenko.dto.CurrencyDTO;
import maxim.butenko.service.CurrencyService;
import org.sqlite.SQLiteErrorCode;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            List<CurrencyDTO> currencies = currencyService.findAll();

            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currencies);
            resp.getWriter().write(json);
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "the database is unavailable now, sorry("));
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        req.setCharacterEncoding("UTF-8");

        String fullName = req.getParameter("fullName");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        if (fullName.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "fullName is required"));
            return;

        } else if (code.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "code is required"));
            return;

        } else if (code.length() != 3 || !code.matches("[A-Z]{3}")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "Incorrect currency code entry. " +
                            "Currency code must consist of 3 letters in uppercase"));
            return;

        } else if (sign.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "sign is required"));
            return;
        }

        try {
            Optional<CurrencyDTO> newCurrency = currencyService.create(fullName, code, sign);
            if (newCurrency.isPresent()) {
                CurrencyDTO currencyDTO = newCurrency.get();
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currencyDTO);
                resp.getWriter().write(json);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse(HttpServletResponse.SC_CONFLICT,
                        "Currency with this code already exists"));

            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "the database is unavailable now, sorry("));
            }
        }
    }
}
