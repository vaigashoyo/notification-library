package com.notification.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ErrorCode - Catálogo de códigos de error")
class ErrorCodeTest {

    @Nested
    @DisplayName("Unicidad de códigos")
    class UnicidadDeCodigos {

        @Test
        @DisplayName("Todos los códigos deben ser únicos")
        void todosLosCodigosDebenSerUnicos() {
            ErrorCode[] values = ErrorCode.values();
            Set<String> codigosUnicos = Arrays.stream(values)
                    .map(ErrorCode::getCode)
                    .collect(Collectors.toSet());

            assertEquals(values.length, codigosUnicos.size(),
                    "Existen códigos de error duplicados");
        }
    }

    @Nested
    @DisplayName("Formato de códigos")
    class FormatoDeCodigos {

        @Test
        @DisplayName("Todos los códigos deben tener formato correcto (V/S/C seguido de 3 dígitos)")
        void todosLosCodigosDebenTenerFormatoCorrecto() {
            for (ErrorCode errorCode : ErrorCode.values()) {
                String code = errorCode.getCode();
                assertNotNull(code, "El código no debe ser nulo para " + errorCode.name());
                assertTrue(code.matches("^[VSC]\\d{3}$"),
                        "El código '%s' no cumple el formato esperado (V/S/C + 3 dígitos)".formatted(code));
            }
        }
    }

    @Nested
    @DisplayName("Formato toString")
    class FormatoToString {

        @Test
        @DisplayName("toString debe retornar formato [CODIGO] descripcion")
        void toStringDebeRetornarFormatoCorrecto() {
            for (ErrorCode errorCode : ErrorCode.values()) {
                String resultado = errorCode.toString();
                String esperado = "[%s] %s".formatted(errorCode.getCode(), errorCode.getDescription());
                assertEquals(esperado, resultado,
                        "El toString de %s no tiene el formato esperado".formatted(errorCode.name()));
            }
        }
    }

    @Nested
    @DisplayName("Existencia de códigos requeridos")
    class ExistenciaDeCodigosRequeridos {

        @Test
        @DisplayName("Deben existir los códigos de validación V001 a V010")
        void debenExistirCodigosDeValidacion() {
            Set<String> codigos = Arrays.stream(ErrorCode.values())
                    .map(ErrorCode::getCode)
                    .collect(Collectors.toSet());

            for (int i = 1; i <= 10; i++) {
                String codigoEsperado = "V%03d".formatted(i);
                assertTrue(codigos.contains(codigoEsperado),
                        "Falta el código de validación: " + codigoEsperado);
            }
        }

        @Test
        @DisplayName("Deben existir los códigos de envío S001 a S006")
        void debenExistirCodigosDeEnvio() {
            Set<String> codigos = Arrays.stream(ErrorCode.values())
                    .map(ErrorCode::getCode)
                    .collect(Collectors.toSet());

            for (int i = 1; i <= 6; i++) {
                String codigoEsperado = "S%03d".formatted(i);
                assertTrue(codigos.contains(codigoEsperado),
                        "Falta el código de envío: " + codigoEsperado);
            }
        }

        @Test
        @DisplayName("Deben existir los códigos de configuración C001 a C002")
        void debenExistirCodigosDeConfiguracion() {
            Set<String> codigos = Arrays.stream(ErrorCode.values())
                    .map(ErrorCode::getCode)
                    .collect(Collectors.toSet());

            for (int i = 1; i <= 2; i++) {
                String codigoEsperado = "C%03d".formatted(i);
                assertTrue(codigos.contains(codigoEsperado),
                        "Falta el código de configuración: " + codigoEsperado);
            }
        }
    }
}
