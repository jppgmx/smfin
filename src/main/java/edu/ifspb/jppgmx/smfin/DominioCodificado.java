package edu.ifspb.jppgmx.smfin;

public interface DominioCodificado<C> {
    C getCodigo();
    String getDescricao();
    default String formatarDominio() {
        return String.format("%s - %s", getCodigo().toString(), getDescricao());
    }
}
