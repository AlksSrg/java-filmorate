package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    /**
     * Обработчик исключений, связанных с нарушением правил валидации полей.
     *
     * @param ex исключение ValidationException
     * @return сформированный ответ с сообщением об ошибке и статус-код BAD_REQUEST
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработчик исключений, возникающих при неправильной передаче аргументов метода контроллера.
     *
     * @param ex исключение MethodArgumentNotValidException
     * @return сформированный ответ с сообщением об ошибке и статус-код BAD_REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleBadRequest(MethodArgumentNotValidException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработчик исключений, возникающих при попытке обращения к несуществующему ресурсу.
     *
     * @param ex исключение EntityNotFoundException
     * @return сформированный ответ с сообщением об ошибке и статус-код NOT_FOUND
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Обработчик любых других необработанных исключений, приводящих к состоянию внутреннего сбоя сервера.
     *
     * @param ex общее исключение Exception
     * @return сформированный ответ с сообщением об ошибке и статус-код INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, Object>> handleInternalServerError(Exception ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Вспомогательный приватный метод, формирующий стандартизированный JSON-ответ с сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     * @param status  HTTP-статус ответа
     * @return сформированный ответ с телом, содержащим ошибку
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = Collections.singletonMap("message", message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}