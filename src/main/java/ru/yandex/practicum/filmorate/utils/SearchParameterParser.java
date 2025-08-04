package ru.yandex.practicum.filmorate.utils;

import lombok.NoArgsConstructor;

/**
 * Утилита для парсинга параметров поиска
 */
@NoArgsConstructor
public class SearchParameterParser {

    /**
     * Парсит параметры поиска
     *
     * @param by строка с параметрами поиска
     * @return массив допустимых параметров поиска
     * @throws IllegalArgumentException если параметры содержат недопустимые значения
     */
    public static String[] parseSearchParameters(String by) {
        String[] params = by.split(",");
        for (String param : params) {
            String trimmedParam = param.trim();
            if (!"title".equals(trimmedParam) && !"director".equals(trimmedParam)) {
                throw new IllegalArgumentException("Invalid search parameter: " + trimmedParam +
                                                   ". Allowed values: 'title', 'director'");
            }
        }
        return params;
    }
}