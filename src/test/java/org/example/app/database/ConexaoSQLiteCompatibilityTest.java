package org.example.app.database;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ConexaoSQLiteCompatibilityTest {

    @Test
    void conexaoSqliteNaoDeveTerConfiguracaoDeBancoIndependente() {
        assertFalse(Arrays.stream(ConexaoSQLite.class.getDeclaredFields())
            .anyMatch(field -> field.getName().equals("URL")));
    }
}
