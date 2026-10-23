package br.com.conde.secretaria;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * As duas regras otimas nao sao a mesma, e nenhuma das duas e "a melhor".
 * Depende do que se esta perguntando.
 */
@DisplayName("Querer o melhor contra querer um bom")
class PostoTest {

    @Test
    @DisplayName("a conta por programacao dinamica bate com a varredura, em todo n")
    void aContaBateComAVarredura() {
        for (int n = 1; n <= 9; n++) {
            double pelaConta = Otima.postoEsperado(n);
            double pelaVarredura = new Varredura(n).medir(Otima.regraDePosto(n)).postoMedio();

            assertThat(pelaVarredura)
                    .describedAs("n = %d", n)
                    .isCloseTo(pelaConta, within(1e-9));
        }
    }

    @Test
    @DisplayName("a regra de posto contrata gente melhor, em media, que a regra dos 37%")
    void aRegraDePostoContrataMelhor() {
        for (int n = 5; n <= 9; n++) {
            Varredura varredura = new Varredura(n);
            double comLimiar = varredura.medir(Regra.limiar(Otima.melhorLimiar(n))).postoMedio();
            double comPosto = varredura.medir(Otima.regraDePosto(n)).postoMedio();

            assertThat(comPosto)
                    .describedAs("n = %d", n)
                    .isLessThan(comLimiar);
        }
    }

    @Test
    @DisplayName("e mesmo assim acerta o melhor MENOS vezes: sao objetivos diferentes")
    void eAcertaOMelhorMenosVezes() {
        for (int n = 5; n <= 9; n++) {
            Varredura varredura = new Varredura(n);
            double comLimiar = varredura.medir(Regra.limiar(Otima.melhorLimiar(n))).chanceDoMelhor();
            double comPosto = varredura.medir(Otima.regraDePosto(n)).chanceDoMelhor();

            assertThat(comPosto)
                    .describedAs("n = %d", n)
                    .isLessThan(comLimiar);
        }
    }

    @Test
    @DisplayName("nenhuma regra bate a de posto em colocacao media: ela e otima mesmo")
    void aRegraDePostoEOtima() {
        int n = 7;
        Varredura varredura = new Varredura(n);
        double otimo = varredura.medir(Otima.regraDePosto(n)).postoMedio();

        for (int k = 0; k < n; k++) {
            assertThat(varredura.medir(Regra.limiar(k)).postoMedio())
                    .describedAs("limiar %d bateu a regra otima", k)
                    .isGreaterThanOrEqualTo(otimo - 1e-12);
        }
        assertThat(varredura.medir(Regra.primeiro()).postoMedio()).isGreaterThan(otimo);
        assertThat(varredura.medir(Regra.ultimo()).postoMedio()).isGreaterThan(otimo);
    }

    @Test
    @DisplayName("com tres candidatos as duas regras sao a mesma, e so a partir de quatro divergem")
    void comTresElasCoincidem() {
        assertThat(new Varredura(3).medir(Otima.regraDePosto(3)).postoMedio())
                .isEqualTo(new Varredura(3).medir(Regra.limiar(1)).postoMedio());

        assertThat(new Varredura(5).medir(Otima.regraDePosto(5)).postoMedio())
                .isNotEqualTo(new Varredura(5).medir(Regra.limiar(2)).postoMedio());
    }

    @Test
    @DisplayName("a regra de posto aceita candidato que NAO e o melhor ate agora, e a outra nunca")
    void aceitaOSegundoMelhor() {
        int[] limite = Otima.limitesDePosto(8);

        assertThat(limite[8]).isEqualTo(8);
        int maior = 0;
        for (int posicao = 1; posicao < limite.length - 1; posicao++) {
            maior = Math.max(maior, limite[posicao]);
        }
        assertThat(maior).isGreaterThanOrEqualTo(2);
        assertThat(limite[1]).isZero();
    }

    @Test
    @DisplayName("na ultima entrevista aceita qualquer um: nao ha mais escolha")
    void naUltimaAceitaQualquerUm() {
        for (int n = 2; n <= 9; n++) {
            assertThat(Otima.limitesDePosto(n)[n])
                    .describedAs("n = %d", n)
                    .isEqualTo(n);
        }
    }
}
