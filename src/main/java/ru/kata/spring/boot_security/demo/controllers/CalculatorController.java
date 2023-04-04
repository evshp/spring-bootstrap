package ru.kata.spring.boot_security.demo.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalculatorController {



    @GetMapping("/calculator")
    public String CalcController(@RequestParam(value = "a", required = false) Integer a,
                                @RequestParam(value = "b", required = false) Integer b,
                                @RequestParam(value = "operation", required = false) String operation,
                                Model model) {
        double result = 0;

        if (a != null & b != null) {

            switch (operation) {
                case "multiply":
                    result = a * b;
                    break;
                case "addition":
                    result = a + b;
                    break;
                case "subtraction":
                    result = a - b;
                    break;
                case "division":
                    result = a / b;
                    break;

                default:
                    result = 0;
                    break;
            }

            model.addAttribute("answer", "Ответ равен = " + result);
            return "second/calculator";
        }

        model.addAttribute("answer", "0");
        return "second/calculator";

    }

}
