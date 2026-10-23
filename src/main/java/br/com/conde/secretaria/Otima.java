package br.com.conde.secretaria;

import java.util.HashMap;
import java.util.Map;

/**
 * As duas regras otimas, que nao sao a mesma regra.
 *
 * <p>"Melhor estrategia" depende do que se quer, e aqui as duas respostas sao
 * diferentes de verdade:
 *
 * <ul>
 *   <li><b>Maximizar a chance de ficar com o melhor de todos.</b> Tudo ou nada:
 *       ficar com o segundo melhor vale tanto quanto ficar com o ultimo. Essa e
 *       a regra dos 37 por cento.</li>
 *   <li><b>Minimizar a colocacao esperada de quem for contratado.</b> Aqui o
 *       segundo melhor e quase tao bom quanto o primeiro, e a regra muda.</li>
 * </ul>
 *
 * <p>A conta por programacao dinamica deste arquivo nao e acreditada: a
 * {@link Varredura} roda a regra resultante em todas as ordens e os dois numeros
 * tem que bater. E assim que um erro de indice aparece.
 */
public final class Otima {

    // Achar o melhor limiar de 10 candidatos custa dez varreduras de 3.628.800 ordens.
    // A funcao e pura, entao a resposta fica guardada: sem isso a bateria de testes
    // levava 36 segundos, e tres quartos disso era recalcular a mesma coisa.
    private static final Map<Integer, Integer> MELHORES = new HashMap<>();

    private Otima() {
    }

    /** O limiar k que mais acerta, achado por varredura de todos os k. */
    public static int melhorLimiar(int total) {
        Integer guardado = MELHORES.get(total);
        if (guardado != null) {
            return guardado;
        }
        Varredura varredura = new Varredura(total);
        int melhor = 0;
        long acertos = -1;
        for (int k = 0; k < total; k++) {
            long conta = varredura.medir(Regra.limiar(k)).acertos();
            if (conta > acertos) {
                acertos = conta;
                melhor = k;
            }
        }
        MELHORES.put(total, melhor);
        return melhor;
    }

    /** O limiar que a formula classica manda usar: n dividido por e, arredondado. */
    public static int limiarDaFormula(int total) {
        return (int) Math.round(total / Math.E);
    }

    /**
     * Para cada posicao, a pior colocacao local que ainda vale a pena aceitar
     * quando o objetivo e a colocacao esperada.
     *
     * <p>Programacao dinamica de tras para frente. Na posicao {@code p}, um
     * candidato que e o {@code r}-esimo melhor dos {@code p} vistos vai terminar,
     * em media, na colocacao {@code r * (n + 1) / (p + 1)}. Aceitar vale a pena
     * quando esse numero nao e pior do que o valor de continuar.
     */
    public static int[] limitesDePosto(int total) {
        int[] limite = new int[total + 1];
        limite[total] = total;
        if (total == 1) {
            limite[1] = 1;
            return limite;
        }
        double[] valor = valores(total, limite);
        if (valor.length == 0) {
            throw new IllegalStateException("sem valores");
        }
        return limite;
    }

    /** A colocacao esperada que a regra otima alcanca, pela conta. */
    public static double postoEsperado(int total) {
        if (total == 1) {
            return 1;
        }
        return valores(total, new int[total + 1])[0];
    }

    /**
     * {@code valor[i]} = colocacao esperada depois de recusar as posicoes 1 a i.
     * O ultimo candidato nao pode ser recusado, entao {@code valor[n-1]} e a media
     * das colocacoes possiveis dele.
     */
    private static double[] valores(int total, int[] limite) {
        double[] valor = new double[total];
        valor[total - 1] = (total + 1) / 2.0;
        for (int i = total - 2; i >= 0; i--) {
            int proxima = i + 1;
            double soma = 0;
            int maiorAceito = 0;
            for (int posto = 1; posto <= proxima; posto++) {
                double aceitar = posto * (total + 1.0) / (proxima + 1.0);
                if (aceitar <= valor[proxima]) {
                    soma += aceitar;
                    maiorAceito = posto;
                } else {
                    soma += valor[proxima];
                }
            }
            valor[i] = soma / proxima;
            limite[proxima] = maiorAceito;
        }
        return valor;
    }

    /** A regra que minimiza a colocacao esperada de quem for contratado. */
    public static Regra regraDePosto(int total) {
        int[] limite = limitesDePosto(total);
        return entrevista -> entrevista.postoLocal() <= limite[entrevista.posicao()];
    }
}
