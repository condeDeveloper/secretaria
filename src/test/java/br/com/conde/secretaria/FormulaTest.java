package br.com.conde.secretaria;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A regra dos 37 por cento e um limite, nao uma receita. Nos tamanhos em que
 * alguem de fato entrevista candidatos, ela erra o limiar com frequencia.
 */
@DisplayName("A formula n/e contra a varredura")
class FormulaTest {

    private int formula(int n) {
        return Math.min(Otima.limiarDaFormula(n), n - 1);
    }

    @Test
    @DisplayName("a formula erra o limiar em 2, 7 e 10, e acerta nos outros ate dez")
    void ondeAFormulaErra() {
        List<Integer> erros = new ArrayList<>();
        for (int n = 1; n <= 10; n++) {
            if (formula(n) != Otima.melhorLimiar(n)) {
                erros.add(n);
            }
        }

        assertThat(erros).containsExactly(2, 7, 10);
    }

    @Test
    @DisplayName("quando erra, erra para cima: manda observar mais do que devia")
    void erraParaCima() {
        for (int n : new int[]{2, 7, 10}) {
            assertThat(formula(n))
                    .describedAs("n = %d", n)
                    .isGreaterThan(Otima.melhorLimiar(n));
        }
    }

    @Test
    @DisplayName("o preco de seguir a formula em sete candidatos e quase um ponto de chance")
    void oPrecoEmSete() {
        Varredura varredura = new Varredura(7);
        double comFormula = varredura.medir(Regra.limiar(formula(7))).chanceDoMelhor();
        double comMelhor = varredura.medir(Regra.limiar(Otima.melhorLimiar(7))).chanceDoMelhor();

        assertThat(100 * comMelhor - 100 * comFormula).isBetween(0.6, 0.8);
    }

    @Test
    @DisplayName("em dois candidatos a formula manda observar um e perder sempre o segundo")
    void emDoisAFormulaEhAbsurda() {
        assertThat(Otima.limiarDaFormula(2)).isEqualTo(1);
        assertThat(new Varredura(2).medir(Regra.limiar(1)).chanceDoMelhor()).isEqualTo(0.5);
        assertThat(new Varredura(2).medir(Regra.limiar(0)).chanceDoMelhor()).isEqualTo(0.5);
    }

    @Test
    @DisplayName("o limiar otimo cresce com n, mas nao de um em um")
    void oLimiarCresceAosSaltos() {
        int[] melhores = new int[11];
        for (int n = 1; n <= 10; n++) {
            melhores[n] = Otima.melhorLimiar(n);
        }

        assertThat(melhores[3]).isEqualTo(melhores[4]);
        assertThat(melhores[5]).isEqualTo(melhores[6]).isEqualTo(melhores[7]);
        assertThat(melhores[8]).isEqualTo(melhores[9]).isEqualTo(melhores[10]);
        for (int n = 2; n <= 10; n++) {
            assertThat(melhores[n]).isGreaterThanOrEqualTo(melhores[n - 1]);
        }
    }
}
