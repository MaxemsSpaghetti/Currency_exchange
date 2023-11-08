package maxim.butenko.servlet.exchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import maxim.butenko.service.ExchangeRateService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        var baseCurrencyCode = req.getPathInfo().substring(1, 4);
        var targetCurrencyCode = req.getPathInfo().substring(4);

        exchangeRateService.findByCodes(baseCurrencyCode, targetCurrencyCode).ifPresent(exchangeRate -> {
            try {
                var json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeRate);
                resp.getWriter().write(json);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        var baseCurrencyCode = req.getPathInfo().substring(1, 4);
        var targetCurrencyCode = req.getPathInfo().substring(4);
        var rate = Double.valueOf(req.getParameter("rate"));

        var updateExchangeRate = exchangeRateService.update(baseCurrencyCode, targetCurrencyCode, rate);
        if (updateExchangeRate.isPresent()) {
            var exchangeRate = updateExchangeRate.get();
            var json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeRate);
            try (var printWriter = resp.getWriter()) {
                printWriter.write(json);
            }
        }
    }
}
