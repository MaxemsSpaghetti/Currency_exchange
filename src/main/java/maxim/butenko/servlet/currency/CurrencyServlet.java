package maxim.butenko.servlet.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import maxim.butenko.ErrorResponse;
import maxim.butenko.dto.CurrencyDTO;
import maxim.butenko.service.CurrencyService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo().substring(1);

        if (pathInfo.length() != 3 || !pathInfo.matches("[A-Z]{3}")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Incorrect currency code entry. The currency must consist of 3 letters in uppercase"));
            return;
        }

        try {
            Optional<CurrencyDTO> optionalCurrency = currencyService.findByCode(pathInfo);
            if (optionalCurrency.isPresent()) {
                CurrencyDTO currencyDTO = optionalCurrency.get();
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currencyDTO);
                resp.getWriter().write(json);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Currency not found"));
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "the database is unavailable now, sorry("));
        }
    }
}
