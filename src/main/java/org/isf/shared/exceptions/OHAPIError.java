package org.isf.shared.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import org.isf.utils.exception.OHServiceException;
import org.springframework.http.HttpStatus;

//import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Exception DTO
 *
 * @author antonio
 */
@Getter
@Setter
public class OHAPIError {
    private HttpStatus status;
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private String message;
    private String debugMessage;
    private String stackTrace;
    private LocalDateTime timestamp;

    public OHAPIError(HttpStatus status, OHServiceException ex) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = ex.getMessages().get(0).getMessage();
        this.debugMessage = ex.getMessages()
                .stream()
                .map(em -> em.getMessage())
                .collect(Collectors.joining(","));
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        this.stackTrace = sw.toString();
    }

}
