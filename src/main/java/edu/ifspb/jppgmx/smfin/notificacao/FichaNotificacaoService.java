package edu.ifspb.jppgmx.smfin.notificacao;

import edu.ifspb.jppgmx.smfin.paciente.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        validar(ficha);
        return repository.save(ficha);
    }

    public FichaNotificacao update(String id, FichaNotificacao ficha) {
        validar(ficha);
        return repository.update(id, ficha);
    }

    private void validar(FichaNotificacao ficha) {
        if (ficha == null) {
            throw new FichaNotificacaoServiceException("A ficha de notificação é obrigatória.");
        }

        List<String> erros = new ArrayList<>();
        if (ficha.tipo() == null) erros.add("tipo: é obrigatório.");
        if (ficha.agravoDoenca() == null) erros.add("agravoDoenca: é obrigatório.");
        if (ficha.dataNotificacao() == null) erros.add("dataNotificacao: é obrigatória.");
        if (ficha.municipioNotificado() == null) erros.add("municipioNotificado: é obrigatório.");
        if (ficha.unidadeNotificadora() == null) erros.add("unidadeNotificadora: é obrigatória.");
        if (ficha.dataInvestigacao() == null) erros.add("dataInvestigacao: é obrigatória.");

        if (ficha.dataNotificacao() != null && ficha.dataInvestigacao() != null
                && ficha.dataInvestigacao().isBefore(ficha.dataNotificacao())) {
            erros.add("dataInvestigacao: não pode ser anterior à dataNotificacao.");
        }
        if (ficha.dataObito() != null && ficha.dataEncerramento() != null
                && ficha.dataObito().isAfter(ficha.dataEncerramento())) {
            erros.add("dataObito: não pode ser posterior à dataEncerramento.");
        }

        if (ficha.tipo() == TipoNotificacao.INDIVIDUAL) {
            if (ficha.dataSintoma() == null) erros.add("dataSintoma: é obrigatória para notificação individual.");
            validarPaciente(ficha.paciente(), erros);
        } else if (ficha.paciente() != null) {
            erros.add("paciente: deve ser informado somente para notificação individual.");
        }

        if (ficha.classificacaoFinal() == null && ficha.dataEncerramento() != null) {
            erros.add("classificacaoFinal: é obrigatória quando dataEncerramento for informada.");
        }
        if (ficha.classificacaoFinal() != null && ficha.dataEncerramento() == null) {
            erros.add("dataEncerramento: é obrigatória quando classificacaoFinal for informada.");
        }

        if (ficha.classificacaoFinal() == ClassificacaoFinal.CONFIRMADO) {
            if (ficha.autoctone() == null) {
                erros.add("autoctone: é obrigatório para caso confirmado.");
            } else if (ficha.autoctone() == Autoctone.INDETERMINADO
                    && ficha.localProvavelInfeccao() != null) {
                erros.add("localProvavelInfeccao: não deve ser informado quando autoctone for indeterminado.");
            } else if (ficha.autoctone() == Autoctone.NAO
                    && ficha.localProvavelInfeccao() == null) {
                erros.add("localProvavelInfeccao: é obrigatório quando o caso não é autóctone.");
            }
        } else if (ficha.autoctone() != null || ficha.localProvavelInfeccao() != null) {
            erros.add("autoctone e localProvavelInfeccao: só devem ser informados para caso confirmado.");
        }

        if (ficha.evolucaoCaso() == EvolucaoCaso.OBITO_PELO_AGRAVO
                || ficha.evolucaoCaso() == EvolucaoCaso.OBITO_POR_OUTRAS_CAUSAS) {
            if (ficha.dataObito() == null) erros.add("dataObito: é obrigatória quando a evolução indicar óbito.");
        } else if (ficha.dataObito() != null) {
            erros.add("dataObito: só deve ser informada quando a evolução indicar óbito.");
        }

        if (!erros.isEmpty()) {
            throw new FichaNotificacaoServiceException(String.join(" ", erros));
        }
    }

    private void validarPaciente(Paciente paciente, List<String> erros) {
        if (paciente == null) {
            erros.add("paciente: é obrigatório para notificação individual.");
            return;
        }
        if (vazio(paciente.nome())) erros.add("paciente.nome: é obrigatório.");
        if (paciente.sexo() == null) erros.add("paciente.sexo: é obrigatório.");
        if (paciente.dataNascimento() == null
                && (paciente.idade() == null || paciente.idadeUnidade() == null)) {
            erros.add("paciente: informe dataNascimento ou idade e idadeUnidade.");
        }
        if (paciente.dataNascimento() != null
                && (paciente.idade() != null || paciente.idadeUnidade() != null)) {
            erros.add("paciente: dataNascimento e idade não podem ser informados simultaneamente.");
        }
        if (paciente.idade() != null && paciente.idade() < 0) {
            erros.add("paciente.idade: não pode ser negativa.");
        }
        if (paciente.idade() != null && paciente.idadeUnidade() == null) {
            erros.add("paciente.idadeUnidade: é obrigatória quando idade for informada.");
        }
        if (paciente.sexo() == edu.ifspb.jppgmx.smfin.paciente.Sexo.FEMININO
                && paciente.gestante() == null) {
            erros.add("paciente.gestante: é obrigatória para sexo feminino.");
        }
        if (paciente.sexo() != edu.ifspb.jppgmx.smfin.paciente.Sexo.FEMININO
                && paciente.gestante() != null) {
            erros.add("paciente.gestante: não deve ser informada para sexo masculino ou ignorado.");
        }
        if (paciente.enderecoResidencial() == null
                || paciente.enderecoResidencial().base() == null
                || paciente.enderecoResidencial().municipio() == null) {
            erros.add("paciente.enderecoResidencial: município de residência é obrigatório.");
        }
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }

    public static class FichaNotificacaoServiceException extends RuntimeException {
        public FichaNotificacaoServiceException(String message) {
            super(message);
        }

        public FichaNotificacaoServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
