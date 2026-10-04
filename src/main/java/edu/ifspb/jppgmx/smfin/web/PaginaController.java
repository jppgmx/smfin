package edu.ifspb.jppgmx.smfin.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("nome", "SMFIN");
        return "index";
    }
}
