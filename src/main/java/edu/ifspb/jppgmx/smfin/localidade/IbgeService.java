package edu.ifspb.jppgmx.smfin.localidade;

import edu.ifspb.jppgmx.smfin.JsonUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Service
public final class IbgeService {
    private final RestClient client;

    public IbgeService() {
        this.client = RestClient.builder().build();
    }

    public Estado buscarEstado(String sigla) throws IbgeServiceException {
        final String url = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/{uf}";

        try {
            var response = fetchJson(url, sigla);

            if (!JsonUtils.isValidObject(response)) {
                throw new IbgeServiceException(
                        String.format("Estado com sigla '%s' não encontrado.", sigla)
                );
            }

            return Estado.parse(response);
        } catch (final JsonNodeException | NullPointerException e) {
            throw new IbgeServiceException(
                String.format("Erro ao processar a resposta do IBGE para o estado com sigla '%s'.", sigla), e
            );
        }
    }

    public List<Estado> listarEstados() throws IbgeServiceException {
        final String url = "https://servicodados.ibge.gov.br/api/v1/localidades/estados";

        try {
            var response = fetchJson(url);

            if (!JsonUtils.isValidArray(response) || response.asArray().isEmpty()) {
                throw new IbgeServiceException("Nenhum estado encontrado.");
            }

            return response.asArray()
                    .values()
                    .stream()
                    .map(Estado::parse)
                    .toList();
        } catch (final JsonNodeException | NullPointerException e) {
            throw new IbgeServiceException("Erro ao processar a resposta do IBGE.", e);
        }
    }

    public List<Municipio> listarMunicipios(Estado estado) throws IbgeServiceException {
        final String url = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/{uf}/municipios";

        try {
            var response = fetchJson(url, estado.sigla());

            if (!JsonUtils.isValidArray(response) || response.asArray().isEmpty()) {
                throw new IbgeServiceException(
                        String.format("Nenhum município encontrado para o estado '%s'.", estado.sigla())
                );
            }

            return response.asArray()
                    .values()
                    .stream()
                    .map(node -> Municipio.parse(node, estado))
                    .toList();
        } catch (final JsonNodeException | NullPointerException e) {
            throw new IbgeServiceException(
                String.format("Erro ao processar a resposta do IBGE para os municípios do estado '%s'.", estado.sigla()), e
            );
        }
    }

    private JsonNode fetchJson(String url, Object... uriVariables) throws IbgeServiceException {
        try {
            return client.get()
                    .uri(url, uriVariables)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (Exception e) {
            throw new IbgeServiceException("Erro ao buscar dados do IBGE.", e);
        }
    }

    public static class IbgeServiceException extends Exception {
        public IbgeServiceException(String message) {
            super(message);
        }

        public IbgeServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
