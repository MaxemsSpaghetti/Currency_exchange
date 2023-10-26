package maxim.butenko.servlet;

import maxim.butenko.service.CurrencyService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try(var printWriter = resp.getWriter()) {
            printWriter.write("<h1>Currencies</h1>");
            printWriter.write("<ul>");
            currencyService.findAll().forEach(currencyDTO -> {
                        printWriter.write("<li>");
                        printWriter.write(currencyDTO.getId() + "/n "
                                + currencyDTO.getCode() + "/n "
                                + currencyDTO.getFullName() + "/n "
                                + currencyDTO.getSign());
                    });
            printWriter.write("</ul>");
        }
    }
}
