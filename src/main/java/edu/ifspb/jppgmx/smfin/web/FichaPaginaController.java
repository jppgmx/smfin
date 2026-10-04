package edu.ifspb.jppgmx.smfin.web;

import edu.ifspb.jppgmx.smfin.notificacao.FichaNotificacao;
import edu.ifspb.jppgmx.smfin.notificacao.FichaNotificacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FichaPaginaController {
    private final FichaNotificacaoService service;

    public FichaPaginaController(FichaNotificacaoService service) {
        this.service = service;
    }

    @GetMapping("/notificacao/nova")
    public String nova(Model model) {
        model.addAttribute("modo", "criar");
        model.addAttribute("fichaId", "");
        return "ficha";
    }

    @GetMapping("/notificacao/{id}/editar")
    public String editar(@PathVariable String id, Model model) {
        FichaNotificacao ficha = service.findById(id);
        if (ficha == null) {
            return "redirect:/";
        }
        model.addAttribute("modo", "editar");
        model.addAttribute("fichaId", id);
        return "ficha";
    }
}
