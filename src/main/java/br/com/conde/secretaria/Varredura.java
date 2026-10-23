package br.com.conde.secretaria;

/**
 * Roda uma regra contra TODAS as ordens possiveis em que os candidatos podem
 * chegar.
 *
 * <p>Nao ha sorteio aqui, e e esse o ponto. Simular com numero aleatorio daria
 * "mais ou menos 37 por cento" e esconderia tudo que e pequeno: o limiar otimo
 * de cada tamanho, a diferenca de uma unidade entre duas regras, o empate exato.
 * Varrendo as {@code n!} ordens, cada numero publicado e uma fracao exata.
 *
 * <p>O custo e factorial, entao o teto fica em 10 candidatos (3.628.800 ordens).
 */
public final class Varredura {

    private final int total;

    public Varredura(int total) {
        if (total < 1 || total > 10) {
            throw new IllegalArgumentException("de 1 a 10 candidatos; " + total + " nao cabe");
        }
        this.total = total;
    }

    public int total() {
        return total;
    }

    /** O que uma regra conseguiu em todas as ordens. */
    public record Resultado(int total, long ordens, long acertos, long somaDosPostos) {

        /** Em que fracao das ordens a regra ficou com o melhor candidato. */
        public double chanceDoMelhor() {
            return (double) acertos / ordens;
        }

        /** A colocacao media de quem foi contratado. 1,0 seria acertar sempre. */
        public double postoMedio() {
            return (double) somaDosPostos / ordens;
        }
    }

    public Resultado medir(Regra regra) {
        int[] fila = new int[total];
        for (int i = 0; i < total; i++) {
            fila[i] = i + 1;
        }
        long[] conta = new long[3];
        permutar(fila, 0, regra, conta);
        return new Resultado(total, conta[0], conta[1], conta[2]);
    }

    /** Heap: gera todas as permutacoes trocando elementos no proprio vetor. */
    private void permutar(int[] fila, int k, Regra regra, long[] conta) {
        if (k == fila.length) {
            int escolhido = escolher(fila, regra);
            conta[0]++;
            if (escolhido == 1) {
                conta[1]++;
            }
            conta[2] += escolhido;
            return;
        }
        for (int i = k; i < fila.length; i++) {
            trocar(fila, k, i);
            permutar(fila, k + 1, regra, conta);
            trocar(fila, k, i);
        }
    }

    /**
     * Roda a regra numa ordem de chegada e devolve a colocacao REAL de quem foi
     * contratado (1 = o melhor de todos).
     *
     * <p>O valor em {@code fila[i]} e a colocacao verdadeira do candidato, que a
     * regra nunca ve: ela so recebe a colocacao dele entre os ja entrevistados.
     */
    public int escolher(int[] fila, Regra regra) {
        for (int i = 0; i < fila.length; i++) {
            int postoLocal = 1;
            for (int j = 0; j < i; j++) {
                if (fila[j] < fila[i]) {
                    postoLocal++;
                }
            }
            Entrevista entrevista = new Entrevista(i + 1, fila.length, postoLocal);
            if (regra.aceita(entrevista) || entrevista.ultima()) {
                return fila[i];
            }
        }
        throw new IllegalStateException("fila vazia");
    }

    private void trocar(int[] fila, int i, int j) {
        int guarda = fila[i];
        fila[i] = fila[j];
        fila[j] = guarda;
    }

    public static long fatorial(int n) {
        long produto = 1;
        for (int i = 2; i <= n; i++) {
            produto *= i;
        }
        return produto;
    }
}
