package org.isf.shared.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.isf.utils.exception.OHServiceException;
import org.springframework.http.HttpStatus;

//import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Exception DTO
 * @author antonio
 *
 */
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
	        .collect( Collectors.joining( "," )); 
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		this.stackTrace = sw.toString();
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDebugMessage() {
		return debugMessage;
	}

	public void setDebugMessage(String debugMessage) {
		this.debugMessage = debugMessage;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String trace) {
		this.stackTrace = trace;
	}
	
	
  
}
