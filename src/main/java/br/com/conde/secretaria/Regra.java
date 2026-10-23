package br.com.conde.secretaria;

/**
 * Uma estrategia de parada: olhando so o que ja viu, aceita ou recusa.
 *
 * <p>A regra nao recebe a fila inteira nem sabe o que vem depois. Recebe uma
 * {@link Entrevista} por vez, na ordem, e responde sim ou nao. Se disser nao
 * para todos, fica com o ultimo, que e o que acontece na vida real: a vaga
 * precisa ser preenchida.
 */
@FunctionalInterface
public interface Regra {

    boolean aceita(Entrevista entrevista);

    /**
     * A regra classica: observa os primeiros {@code k} sem aceitar ninguem e
     * depois pega o primeiro que for melhor que todos eles.
     *
     * <p>Com {@code k = 0} ela aceita o primeiro candidato. Com {@code k = n-1}
     * ela so pode aceitar o ultimo.
     */
    static Regra limiar(int k) {
        return entrevista -> entrevista.posicao() > k && entrevista.melhorAteAgora();
    }

    /**
     * Limiar em fracao do total, arredondado para baixo. E assim que a "regra dos
     * 37 por cento" costuma ser escrita.
     */
    static Regra fracao(double parte, int total) {
        return limiar((int) Math.floor(parte * total));
    }

    /**
     * Aceita quando o candidato e o melhor ate agora E ja passou da posicao em que
     * o custo de continuar supera o ganho, posicao por posicao.
     *
     * <p>Esta e a familia de regras que muda de limiar conforme a entrevista
     * avanca, em vez de usar um corte unico. Os limiares vem de fora: a classe
     * {@link Otima} calcula os dela por programacao dinamica.
     */
    static Regra porPosicao(boolean[] aceitarSeMelhor) {
        return entrevista -> entrevista.melhorAteAgora()
                && aceitarSeMelhor[entrevista.posicao() - 1];
    }

    /** Aceita o primeiro candidato que aparecer. */
    static Regra primeiro() {
        return limiar(0);
    }

    /** Nunca aceita: fica sempre com o ultimo da fila. */
    static Regra ultimo() {
        return entrevista -> false;
    }
}
