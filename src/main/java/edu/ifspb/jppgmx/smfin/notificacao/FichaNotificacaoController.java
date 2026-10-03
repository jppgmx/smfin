package edu.ifspb.jppgmx.smfin.notificacao;

import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/notificacao")
public class FichaNotificacaoController {
    private final FichaNotificacaoService service;

    public FichaNotificacaoController(FichaNotificacaoService service) {
        this.service = service;
    }

    @GetMapping(path = {"", "/"})
    public ResponseEntity<Page<FichaNotificacao>> listar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer tipo,
            @RequestParam(required = false) String agravo,
            @RequestParam(required = false) String unidade,
            @RequestParam(required = false) Integer municipio,
            @RequestParam(required = false) LocalDate dataNotificacaoInicio,
            @RequestParam(required = false) LocalDate dataNotificacaoFim,
            @RequestParam(required = false) Boolean duplicata,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        var filtro = new FichaNotificacaoFiltro(q, tipo, agravo, unidade, municipio,
                dataNotificacaoInicio, dataNotificacaoFim, duplicata);
        return ResponseEntity.ok(service.listar(filtro, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FichaNotificacao> buscarPorId(@PathVariable String id) {
        FichaNotificacao ficha = service.findById(id);
        if (ficha == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ficha);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FichaNotificacao> atualizar(
            @PathVariable String id, @RequestBody FichaNotificacao ficha) {
        FichaNotificacao atualizada = service.update(id, ficha);
        if (atualizada == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        if (!service.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
