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
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            resp.setContentType("application/json");
            String pathInfo = req.getPathInfo().substring(1);

            if (pathInfo.length() != 3) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Incorrect currency code entry. The currency must consist of 3 letters");
                return;
            }

            Optional<CurrencyDTO> optionalCurrency = currencyService.findByCode(pathInfo);
            if (optionalCurrency.isPresent()) {
                CurrencyDTO currencyDTO = optionalCurrency.get();
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currencyDTO);
                resp.getWriter().write(json);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Currency not found");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "the database is unavailable now, sorry(");
        }
    }
}
