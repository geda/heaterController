package ch.creasystem.heater.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ch.creasystem.heater.HeaterService;

@ControllerAdvice
public class HeaterControllerAdvice {
	static final Logger LOG = LoggerFactory.getLogger(HeaterService.class);

	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<String> defaultErrorHandler(HttpServletRequest request, Exception e) {
		LOG.error("UnhandledException has occured: {}", e);
		return new ResponseEntity<String>("A general error has occured. Please try again.",
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class })
	public ResponseEntity<List<String>> handleValidationException(MethodArgumentNotValidException pe) {
		List<String> errors = tranformObjectErrors(messageSource, pe.getBindingResult().getAllErrors());
		return new ResponseEntity<List<String>>(errors, HttpStatus.BAD_REQUEST);
	}

	private static List<String> tranformObjectErrors(MessageSource messageSource, List<ObjectError> objectErrors) {
		Locale locale = LocaleContextHolder.getLocale();
		List<String> errors = new ArrayList<String>();
		for (ObjectError objectError : objectErrors) {
			if (objectError instanceof FieldError) {
				String fieldLabelKey = objectError.getObjectName() + "." + ((FieldError) objectError).getField();
				errors.add(messageSource.getMessage(fieldLabelKey, null, locale) + ": "
						+ messageSource.getMessage(objectError, locale));
			} else {
				errors.add(messageSource.getMessage(objectError, locale));
			}
		}
		return errors;
	}
}
