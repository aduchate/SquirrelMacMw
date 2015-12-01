package org.taktik.squirrel.mac.mw.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NO_CONTENT, reason="No update available")
public class NoContentAvailableException extends Exception {

}
