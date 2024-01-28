package hu.bearmaster.springtutorial.boot.security.controller;

import hu.bearmaster.springtutorial.boot.security.model.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public String handleNotFound(NotFoundException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/4xx";
    }
}
