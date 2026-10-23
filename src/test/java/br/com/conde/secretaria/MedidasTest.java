package br.com.conde.secretaria;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/** Os numeros publicados no README, presos em teste. */
@DisplayName("Os numeros do README")
class MedidasTest {

    private double pct(int n, int k) {
        return Math.round(1000 * new Varredura(n).medir(Regra.limiar(k)).chanceDoMelhor()) / 10.0;
    }

    @Test
    @DisplayName("tabela dos limiares: a linha de cinco candidatos")
    void linhaDeCinco() {
        assertThat(pct(5, 0)).isEqualTo(20.0);
        assertThat(pct(5, 1)).isEqualTo(41.7);
        assertThat(pct(5, 2)).isEqualTo(43.3);
        assertThat(pct(5, 3)).isEqualTo(35.0);
        assertThat(pct(5, 4)).isEqualTo(20.0);
    }

    @Test
    @DisplayName("tabela dos limiares: a linha de sete e a de nove")
    void linhasDeSeteEDeNove() {
        assertThat(pct(7, 2)).isEqualTo(41.4);
        assertThat(pct(7, 3)).isEqualTo(40.7);
        assertThat(pct(9, 3)).isEqualTo(40.6);
        assertThat(pct(9, 4)).isEqualTo(39.3);
    }

    @Test
    @DisplayName("os melhores limiares de 1 a 10")
    void melhoresLimiares() {
        int[] esperado = {0, 0, 0, 1, 1, 2, 2, 2, 3, 3, 3};

        for (int n = 1; n <= 10; n++) {
            assertThat(Otima.melhorLimiar(n))
                    .describedAs("n = %d", n)
                    .isEqualTo(esperado[n]);
        }
    }

    @Test
    @DisplayName("tabela do arrependimento: colocacao media das duas regras")
    void tabelaDoArrependimento() {
        assertThat(posto(3, true)).isCloseTo(1.667, within(0.001));
        assertThat(posto(3, false)).isCloseTo(1.667, within(0.001));
        assertThat(posto(5, true)).isCloseTo(2.200, within(0.001));
        assertThat(posto(5, false)).isCloseTo(2.050, within(0.001));
        assertThat(posto(9, true)).isCloseTo(2.917, within(0.001));
        assertThat(posto(9, false)).isCloseTo(2.496, within(0.001));
    }

    @Test
    @DisplayName("a colocacao esperada otima de nove candidatos e 2,496")
    void postoEsperadoDeNove() {
        assertThat(Otima.postoEsperado(9)).isCloseTo(2.496, within(0.001));
        assertThat(Otima.postoEsperado(3)).isCloseTo(5.0 / 3, within(1e-9));
    }

    @Test
    @DisplayName("o medidor roda inteiro sem lancar")
    void medidorRoda() {
        Medidor.limiares();
        Medidor.otimo();
        Medidor.arrependimento();
    }

    private double posto(int n, boolean comLimiar) {
        Regra regra = comLimiar ? Regra.limiar(Otima.melhorLimiar(n)) : Otima.regraDePosto(n);
        return new Varredura(n).medir(regra).postoMedio();
    }
}
