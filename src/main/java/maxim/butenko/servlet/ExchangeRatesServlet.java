package maxim.butenko.servlet;

import maxim.butenko.service.ExchangeRateService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var currencyId = Long.valueOf(req.getParameter("currencyId"));
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (var printWriter = resp.getWriter()) {
            printWriter.write("<h1>Exchange Rates</h1>");
            printWriter.write("<ul>");
            exchangeRateService.findByCurrencyId(currencyId).forEach(exchangeRateDTO -> {
                printWriter.write("<li>");
                printWriter.write(exchangeRateDTO.getId() + "/n "
                        + exchangeRateDTO.getBaseCurrencyId() + "/n "
                        + exchangeRateDTO.getTargetCurrencyId() + "/n "
                        + exchangeRateDTO.getRate());
            });
            printWriter.write("</ul>");
        }
    }
}
