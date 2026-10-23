package br.com.conde.secretaria;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("O que quem decide sabe")
class EntrevistaTest {

    @Test
    @DisplayName("melhor ate agora e colocacao local igual a um, e nada mais")
    void melhorAteAgora() {
        assertThat(new Entrevista(3, 10, 1).melhorAteAgora()).isTrue();
        assertThat(new Entrevista(3, 10, 2).melhorAteAgora()).isFalse();
        assertThat(new Entrevista(1, 10, 1).melhorAteAgora()).isTrue();
    }

    @Test
    @DisplayName("a ultima entrevista e quando a posicao chega no total")
    void ultima() {
        assertThat(new Entrevista(10, 10, 4).ultima()).isTrue();
        assertThat(new Entrevista(9, 10, 1).ultima()).isFalse();
    }

    @Test
    @DisplayName("o primeiro candidato e sempre o melhor ate agora, e isso nao diz nada")
    void oPrimeiroEhSempreOMelhor() {
        Varredura varredura = new Varredura(5);
        int[][] filas = {{1, 2, 3, 4, 5}, {5, 4, 3, 2, 1}, {3, 1, 5, 2, 4}};

        for (int[] fila : filas) {
            boolean[] visto = new boolean[1];
            varredura.escolher(fila, entrevista -> {
                if (entrevista.posicao() == 1) {
                    visto[0] = entrevista.melhorAteAgora();
                }
                return false;
            });

            assertThat(visto[0]).isTrue();
        }
    }

    @Test
    @DisplayName("a colocacao local nunca passa do numero de vistos")
    void aColocacaoLocalCabeNosVistos() {
        Varredura varredura = new Varredura(6);

        varredura.escolher(new int[]{4, 6, 2, 5, 1, 3}, entrevista -> {
            assertThat(entrevista.postoLocal()).isBetween(1, entrevista.vistos());
            return false;
        });
    }

    @Test
    @DisplayName("na fila ja ordenada todo candidato e o melhor ate agora")
    void filaCrescenteEmQualidade() {
        Varredura varredura = new Varredura(5);
        int[] melhores = new int[1];

        varredura.escolher(new int[]{5, 4, 3, 2, 1}, entrevista -> {
            if (entrevista.melhorAteAgora()) {
                melhores[0]++;
            }
            return false;
        });

        assertThat(melhores[0]).isEqualTo(5);
    }

    @Test
    @DisplayName("na fila do pior para o melhor so o primeiro e o melhor ate agora")
    void filaDecrescenteEmQualidade() {
        Varredura varredura = new Varredura(5);
        int[] melhores = new int[1];

        varredura.escolher(new int[]{1, 2, 3, 4, 5}, entrevista -> {
            if (entrevista.melhorAteAgora()) {
                melhores[0]++;
            }
            return false;
        });

        assertThat(melhores[0]).isEqualTo(1);
    }
}
