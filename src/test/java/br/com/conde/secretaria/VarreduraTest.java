package br.com.conde.secretaria;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A varredura de todas as ordens")
class VarreduraTest {

    @Test
    @DisplayName("visita exatamente n! ordens, sem repetir nem faltar")
    void visitaTodasAsOrdens() {
        for (int n = 1; n <= 8; n++) {
            Varredura.Resultado resultado = new Varredura(n).medir(Regra.primeiro());

            assertThat(resultado.ordens())
                    .describedAs("n = %d", n)
                    .isEqualTo(Varredura.fatorial(n));
        }
    }

    @Test
    @DisplayName("quem recusa todos fica com o ultimo da fila")
    void recusarTudoFicaComOUltimo() {
        Varredura varredura = new Varredura(4);

        assertThat(varredura.escolher(new int[]{3, 1, 4, 2}, Regra.ultimo())).isEqualTo(2);
        assertThat(varredura.escolher(new int[]{2, 4, 3, 1}, Regra.ultimo())).isEqualTo(1);
    }

    @Test
    @DisplayName("aceitar o primeiro devolve o primeiro, qualquer que seja ele")
    void aceitarOPrimeiro() {
        Varredura varredura = new Varredura(4);

        assertThat(varredura.escolher(new int[]{3, 1, 4, 2}, Regra.primeiro())).isEqualTo(3);
        assertThat(varredura.escolher(new int[]{1, 2, 3, 4}, Regra.primeiro())).isEqualTo(1);
    }

    @Test
    @DisplayName("a regra ve a colocacao LOCAL, nunca a verdadeira")
    void soVeOQueJaPassou() {
        Varredura varredura = new Varredura(4);
        int[] vistos = new int[5];

        varredura.escolher(new int[]{3, 1, 4, 2}, entrevista -> {
            vistos[entrevista.posicao()] = entrevista.postoLocal();
            return false;
        });

        // 3 e o melhor de um; 1 e melhor que o 3; 4 e o pior dos tres; 2 e o segundo dos quatro
        assertThat(vistos[1]).isEqualTo(1);
        assertThat(vistos[2]).isEqualTo(1);
        assertThat(vistos[3]).isEqualTo(3);
        assertThat(vistos[4]).isEqualTo(2);
    }

    @Test
    @DisplayName("a soma das colocacoes de quem recusa tudo e a media de todas as colocacoes")
    void recusarTudoDaAMedia() {
        for (int n = 2; n <= 7; n++) {
            Varredura.Resultado resultado = new Varredura(n).medir(Regra.ultimo());

            assertThat(resultado.postoMedio())
                    .describedAs("n = %d", n)
                    .isEqualTo((n + 1) / 2.0);
        }
    }

    @Test
    @DisplayName("mais de dez candidatos nao cabe numa varredura exata, e o construtor avisa")
    void tetoDeDez() {
        assertThatThrownBy(() -> new Varredura(11))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("de 1 a 10");
        assertThatThrownBy(() -> new Varredura(0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
