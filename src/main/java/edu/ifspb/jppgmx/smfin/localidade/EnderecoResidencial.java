package edu.ifspb.jppgmx.smfin.localidade;

public record EnderecoResidencial(int id,
                                  Localidade base,
                                  String logradouro,
                                  String codigoLogradouro,
                                  String numero,
                                  String complemento,
                                  String geocampo1,
                                  String geocampo2,
                                  String pontoReferencia,
                                  String cep,
                                  String telefone,
                                  Zona zona
                                  ) {
    public Estado estado() {
        return base.estado();
    }

    public Municipio municipio() {
        return base.municipio();
    }

    public String distrito() {
        return base.distrito();
    }

    public String bairro() {
        return base.bairro();
    }
}
