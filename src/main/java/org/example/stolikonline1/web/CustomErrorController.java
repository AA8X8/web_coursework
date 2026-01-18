package org.example.stolikonline1.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorTitle", "Страница не найдена");
                model.addAttribute("errorMessage", "Запрашиваемая страница не существует.");
                model.addAttribute("errorCode", "404");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("errorTitle", "Доступ запрещен");
                model.addAttribute("errorMessage", "У вас нет прав для доступа к этой странице.");
                model.addAttribute("errorCode", "403");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorTitle", "Внутренняя ошибка сервера");
                model.addAttribute("errorMessage", "Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже.");
                model.addAttribute("errorCode", "500");
            } else {
                model.addAttribute("errorTitle", "Ошибка");
                model.addAttribute("errorMessage", "Произошла ошибка.");
                model.addAttribute("errorCode", statusCode);
            }
        } else {
            model.addAttribute("errorTitle", "Неизвестная ошибка");
            model.addAttribute("errorMessage", "Произошла неизвестная ошибка.");
            model.addAttribute("errorCode", "???");
        }

        return "error/custom-error";
    }
}