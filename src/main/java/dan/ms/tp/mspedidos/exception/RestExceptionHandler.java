package dan.ms.tp.mspedidos.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dan.ms.tp.mspedidos.exception.response.ApiValidationErrorResponse;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler {
    private static String ValidationHeaderMessage = "Error de Validación";
    

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiValidationErrorResponse> apiValidationError(MethodArgumentNotValidException e){
        HttpStatus returnStatus = HttpStatus.BAD_REQUEST; 
        List<String> errors = e.getBindingResult().getFieldErrors()
            .stream()
            .map(f ->  f.getField().replaceAll("\\[\\d+\\]", "") + ": " + f.getDefaultMessage())
            .collect(Collectors.toList());
            ApiValidationErrorResponse apiErrorInfo = new ApiValidationErrorResponse();

        apiErrorInfo.title = ValidationHeaderMessage;
        apiErrorInfo.status = returnStatus.value();
        apiErrorInfo.errors = errors;

        ResponseEntity<ApiValidationErrorResponse> response = new ResponseEntity<>(apiErrorInfo, returnStatus);

        return response;
    }
}
