package maxim.butenko.servlet.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import maxim.butenko.service.CurrencyService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        var pathInfo = req.getPathInfo().substring(1);
        currencyService.findByCode(pathInfo).ifPresent(currency -> {
            try {
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currency);
                resp.getWriter().write(json);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
