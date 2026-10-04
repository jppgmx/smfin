package edu.ifspb.jppgmx.smfin.notificacao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
public class UnidadeController {
    private final UnidadeRepository unidadeRepository;
    private final InvestigadorRepository investigadorRepository;

    public UnidadeController(UnidadeRepository unidadeRepository, InvestigadorRepository investigadorRepository) {
        this.unidadeRepository = unidadeRepository;
        this.investigadorRepository = investigadorRepository;
    }

    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<Unidade>> listarUnidades() {
        List<Unidade> unidades = unidadeRepository.findAll();
        return ResponseEntity.ok(unidades);
    }

    @GetMapping("/por-municipio")
    public ResponseEntity<List<Unidade>> listarUnidadesPorMunicipio(
            @RequestParam Integer municipio) {
        return ResponseEntity.ok(unidadeRepository.findByMunicipio(municipio));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Unidade> buscarPorId(@PathVariable String id) {
        Unidade unidade = unidadeRepository.findByCodigo(id);
        if (unidade == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(unidade);
    }

    @GetMapping("/{id}/investigadores")
    public ResponseEntity<List<Investigador>> listarInvestigadoresPorUnidade(@PathVariable String id) {
        Unidade unidade = unidadeRepository.findByCodigo(id);
        if (unidade == null) {
            return ResponseEntity.notFound().build();
        }

        List<Investigador> investigadores = unidadeRepository.findInvestigadoresByUnidade(unidade);
        return ResponseEntity.ok(investigadores);
    }

    @GetMapping("/{id}/investigadores/{investigadorId}")
    public ResponseEntity<Investigador> buscarInvestigadorPorId(@PathVariable String id, @PathVariable Integer investigadorId) {
        Unidade unidade = unidadeRepository.findByCodigo(id);
        if (unidade == null) {
            return ResponseEntity.notFound().build();
        }

        Investigador investigador = investigadorRepository.fromUnidade(unidade, investigadorId);
        if (investigador == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(investigador);
    }
}