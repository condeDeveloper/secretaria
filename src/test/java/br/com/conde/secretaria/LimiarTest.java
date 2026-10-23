package br.com.conde.secretaria;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("A regra de limiar")
class LimiarTest {

    private double chance(int n, int k) {
        return new Varredura(n).medir(Regra.limiar(k)).chanceDoMelhor();
    }

    @Test
    @DisplayName("as duas pontas sao igualmente ruins: pegar o primeiro ou esperar o ultimo da 1/n")
    void asDuasPontasEmpatam() {
        for (int n = 2; n <= 8; n++) {
            assertThat(chance(n, 0))
                    .describedAs("pegar o primeiro, n = %d", n)
                    .isCloseTo(1.0 / n, within(1e-12));
            assertThat(chance(n, n - 1))
                    .describedAs("esperar o ultimo, n = %d", n)
                    .isCloseTo(1.0 / n, within(1e-12));
        }
    }

    @Test
    @DisplayName("entre as pontas a chance sobe e desce uma vez so: ha um pico")
    void aCurvaTemUmPicoSo() {
        for (int n = 4; n <= 8; n++) {
            int melhor = Otima.melhorLimiar(n);

            for (int k = 1; k <= melhor; k++) {
                assertThat(chance(n, k))
                        .describedAs("n = %d, subindo em k = %d", n, k)
                        .isGreaterThanOrEqualTo(chance(n, k - 1));
            }
            for (int k = melhor + 1; k < n; k++) {
                assertThat(chance(n, k))
                        .describedAs("n = %d, descendo em k = %d", n, k)
                        .isLessThanOrEqualTo(chance(n, k - 1));
            }
        }
    }

    @Test
    @DisplayName("a chance do melhor limiar cai com n e nao desce de 1/e")
    void caiEmDirecaoAUmSobreE() {
        double anterior = 1;
        for (int n = 3; n <= 10; n++) {
            double melhor = chance(n, Otima.melhorLimiar(n));

            assertThat(melhor).isLessThanOrEqualTo(anterior);
            assertThat(melhor).isGreaterThan(1 / Math.E);
            anterior = melhor;
        }
    }

    @Test
    @DisplayName("com tres candidatos a melhor estrategia acerta metade das vezes, exatamente")
    void comTresMetadeDasVezes() {
        Varredura.Resultado resultado = new Varredura(3).medir(Regra.limiar(1));

        assertThat(resultado.ordens()).isEqualTo(6);
        assertThat(resultado.acertos()).isEqualTo(3);
        assertThat(resultado.chanceDoMelhor()).isEqualTo(0.5);
    }

    @Test
    @DisplayName("a regra escrita como fracao do total e a mesma coisa que o limiar")
    void fracaoEOMesmoQueLimiar() {
        for (int n = 4; n <= 8; n++) {
            double porFracao = new Varredura(n).medir(Regra.fracao(0.5, n)).chanceDoMelhor();
            double porLimiar = chance(n, n / 2);

            assertThat(porFracao).isEqualTo(porLimiar);
        }
    }
}
