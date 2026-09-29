package edu.ifspb.jppgmx.smfin;

import edu.ifspb.jppgmx.smfin.localidade.IbgeService;
import edu.ifspb.jppgmx.smfin.notificacao.AgravoDoencaController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IbgeService.IbgeServiceException.class)
    public ProblemDetail handleIbgeServiceException(IbgeService.IbgeServiceException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Erro no serviço IBGE");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("timestamp", System.currentTimeMillis());
        return problemDetail;
    }

    @ExceptionHandler(AgravoDoencaController.AgravoDoencaException.class)
    public ProblemDetail handleAgravoDoencaException(AgravoDoencaController.AgravoDoencaException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Erro no serviço Agravo/Doença");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("timestamp", System.currentTimeMillis());
        return problemDetail;
    }
}
