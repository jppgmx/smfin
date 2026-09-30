package edu.ifspb.jppgmx.smfin.notificacao;

import org.springframework.stereotype.Service;

@Service
public class FichaNotificacaoService {
    private final FichaNotificacaoRepository repository;

    public FichaNotificacaoService(FichaNotificacaoRepository repository) {
        this.repository = repository;
    }

    public boolean excluir(String id) {
        return repository.deleteById(id);
    }
}
