package edu.ifspb.jppgmx.smfin.misc;

import edu.ifspb.jppgmx.smfin.DominioCodificado;
import edu.ifspb.jppgmx.smfin.localidade.Zona;
import edu.ifspb.jppgmx.smfin.notificacao.Autoctone;
import edu.ifspb.jppgmx.smfin.notificacao.ClassificacaoFinal;
import edu.ifspb.jppgmx.smfin.notificacao.CriterioConfirmacao;
import edu.ifspb.jppgmx.smfin.notificacao.DoencaRelacionadaTrabalho;
import edu.ifspb.jppgmx.smfin.notificacao.EvolucaoCaso;
import edu.ifspb.jppgmx.smfin.notificacao.TipoNotificacao;
import edu.ifspb.jppgmx.smfin.notificacao.Unidade;
import edu.ifspb.jppgmx.smfin.paciente.Escolaridade;
import edu.ifspb.jppgmx.smfin.paciente.Gestante;
import edu.ifspb.jppgmx.smfin.paciente.IdadeUnidade;
import edu.ifspb.jppgmx.smfin.paciente.RacaCor;
import edu.ifspb.jppgmx.smfin.paciente.Sexo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

import java.util.List;

@RestController
@RequestMapping("/api/enums")
public class EnumsController {

    @GetMapping("/zonas")
    public List<JsonNode> listarZonas() {
        return listar(Zona.values());
    }

    @GetMapping("/sexo")
    public List<JsonNode> listarSexo() {
        return listar(Sexo.values());
    }

    @GetMapping("/escolaridades")
    public List<JsonNode> listarEscolaridades() {
        return listar(Escolaridade.values());
    }

    @GetMapping("/gestantes")
    public List<JsonNode> listarGestantes() {
        return listar(Gestante.values());
    }

    @GetMapping("/idades-unidade")
    public List<JsonNode> listarIdadesUnidade() {
        return listar(IdadeUnidade.values());
    }

    @GetMapping("/racas-cor")
    public List<JsonNode> listarRacasCor() {
        return listar(RacaCor.values());
    }

    @GetMapping("/autoctones")
    public List<JsonNode> listarAutoctones() {
        return listar(Autoctone.values());
    }

    @GetMapping("/classificacoes-finais")
    public List<JsonNode> listarClassificacoesFinais() {
        return listar(ClassificacaoFinal.values());
    }

    @GetMapping("/criterios-confirmacao")
    public List<JsonNode> listarCriteriosConfirmacao() {
        return listar(CriterioConfirmacao.values());
    }

    @GetMapping("/doencas-relacionadas-trabalho")
    public List<JsonNode> listarDoencasRelacionadasTrabalho() {
        return listar(DoencaRelacionadaTrabalho.values());
    }

    @GetMapping("/evolucoes-caso")
    public List<JsonNode> listarEvolucoesCaso() {
        return listar(EvolucaoCaso.values());
    }

    @GetMapping("/tipos-notificacao")
    public List<JsonNode> listarTiposNotificacao() {
        return listar(TipoNotificacao.values());
    }

    @GetMapping("/tipos-unidade")
    public List<JsonNode> listarTiposUnidade() {
        return listar(Unidade.Tipo.values());
    }

    private static <E extends Enum<E> & DominioCodificado<?>> List<JsonNode> listar(E[] valores) {
        return java.util.Arrays.stream(valores)
            .map(DominioCodificado::toJsonNode)
            .toList();
    }
}
