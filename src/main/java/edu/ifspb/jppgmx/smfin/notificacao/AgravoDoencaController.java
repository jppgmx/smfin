package edu.ifspb.jppgmx.smfin.notificacao;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agravo-doenca")
public class AgravoDoencaController {
    private final AgravoDoencaRepository repository;

    public AgravoDoencaController(AgravoDoencaRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = {"", "/"})
    public Iterable<AgravoDoenca> listar() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            throw new AgravoDoencaException("Erro ao listar Agravos/Doenças", e);
        }
    }

    @GetMapping("/{cid10}")
    public AgravoDoenca buscar(@PathVariable String cid10) {
        try {
            var res =  repository.findByCid10(cid10);
            if (res == null) {
                throw new AgravoDoencaException("Agravo/Doença de CID-10: " + cid10 + " não encontrado");
            }
            return res;
        } catch (Exception e) {
            throw new AgravoDoencaException("Erro ao buscar Agravo/Doença de CID-10: " + cid10, e);
        }
    }

    public static class AgravoDoencaException extends RuntimeException {
        public AgravoDoencaException(String message) {
            super(message);
        }

        public AgravoDoencaException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
