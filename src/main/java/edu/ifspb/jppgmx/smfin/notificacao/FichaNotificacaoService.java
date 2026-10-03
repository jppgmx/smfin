package edu.ifspb.jppgmx.smfin.notificacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FichaNotificacaoService {
    private final FichaNotificacaoRepository repository;

    public FichaNotificacaoService(FichaNotificacaoRepository repository) {
        this.repository = repository;
    }

    public boolean delete(String id) {
        return repository.deleteById(id);
    }

    public FichaNotificacao findById(String id) {
        return repository.findById(id);
    }

    public Page<FichaNotificacao> listar(FichaNotificacaoFiltro filtro, Pageable pageable) {
        return repository.findAll(filtro, pageable);
    }

    public FichaNotificacao save(FichaNotificacao ficha) {
        // TODO: Validar os campos conforme as instruções.

        return repository.save(ficha);
    }

    public FichaNotificacao update(String id, FichaNotificacao ficha) {
        return repository.update(id, ficha);
    }

    public static class FichaNotificacaoServiceException extends Exception {
        public FichaNotificacaoServiceException(String message) {
            super(message);
        }

        public FichaNotificacaoServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
