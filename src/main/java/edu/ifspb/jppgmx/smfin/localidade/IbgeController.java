package edu.ifspb.jppgmx.smfin.localidade;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/api/ibge")
public class IbgeController {
    private final IbgeService ibgeService;

    public IbgeController(IbgeService ibgeService) {
        this.ibgeService = ibgeService;
    }

    @GetMapping("/estados")
    public List<Estado> listarEstados() throws IbgeService.IbgeServiceException {
        return ibgeService.listarEstados();
    }

    @GetMapping("/estados/{uf}")
    public Estado buscarEstado(@PathVariable String uf) throws IbgeService.IbgeServiceException {
        return ibgeService.buscarEstado(uf);
    }

    @GetMapping("/estados/{uf}/municipios")
    public List<Municipio> listarMunicipios(@PathVariable String uf) throws IbgeService.IbgeServiceException {
        return ibgeService.listarMunicipios(
            ibgeService.buscarEstado(uf)
        );
    }
}
