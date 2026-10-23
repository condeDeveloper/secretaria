package br.com.conde.secretaria;

import java.util.Locale;

/**
 * As tabelas do README. Uso:
 * {@code mvn exec:java -Dexec.args="limiares"} (ou {@code otimo}, {@code arrependimento}).
 */
public final class Medidor {

    private Medidor() {
    }

    public static void main(String[] args) {
        String tabela = args.length > 0 ? args[0] : "tudo";
        boolean tudo = tabela.equals("tudo");
        if (tudo || tabela.equals("limiares")) {
            limiares();
        }
        if (tudo || tabela.equals("otimo")) {
            otimo();
        }
        if (tudo || tabela.equals("arrependimento")) {
            arrependimento();
        }
    }

    /** Cada limiar, em cada tamanho, com a chance exata de ficar com o melhor. */
    public static void limiares() {
        System.out.println();
        System.out.println("LIMIARES  (chance de ficar com o melhor de todos, por k)");
        System.out.print(String.format(Locale.ROOT, "%-4s", "n"));
        for (int k = 0; k <= 6; k++) {
            System.out.print(String.format(Locale.ROOT, " %8s", "k=" + k));
        }
        System.out.println("   melhor k");
        for (int n = 3; n <= 9; n++) {
            Varredura varredura = new Varredura(n);
            StringBuilder linha = new StringBuilder(String.format(Locale.ROOT, "%-4d", n));
            for (int k = 0; k <= 6; k++) {
                if (k >= n) {
                    linha.append(String.format(Locale.ROOT, " %8s", ""));
                    continue;
                }
                double chance = varredura.medir(Regra.limiar(k)).chanceDoMelhor();
                linha.append(String.format(Locale.ROOT, " %7.1f%%", 100 * chance));
            }
            linha.append(String.format(Locale.ROOT, "   %d", Otima.melhorLimiar(n)));
            System.out.println(linha);
        }
        System.out.println();
    }

    /** O limiar otimo contra o que a formula manda. */
    public static void otimo() {
        System.out.println();
        System.out.println("OTIMO  (o limiar que mais acerta contra o da formula n/e)");
        System.out.printf(Locale.ROOT, "%-4s %10s %10s %12s %12s %10s%n",
                "n", "melhor k", "n/e", "chance do k", "chance n/e", "n/e erra?");
        for (int n = 1; n <= 10; n++) {
            Varredura varredura = new Varredura(n);
            int melhor = Otima.melhorLimiar(n);
            int formula = Math.min(Otima.limiarDaFormula(n), n - 1);
            double comMelhor = varredura.medir(Regra.limiar(melhor)).chanceDoMelhor();
            double comFormula = varredura.medir(Regra.limiar(formula)).chanceDoMelhor();
            System.out.printf(Locale.ROOT, "%-4d %10d %10d %11.1f%% %11.1f%% %10s%n",
                    n, melhor, formula, 100 * comMelhor, 100 * comFormula,
                    melhor == formula ? "-" : "SIM");
        }
        System.out.println();
    }

    /** O preco de querer o melhor: a colocacao de quem acaba contratado. */
    public static void arrependimento() {
        System.out.println();
        System.out.println("ARREPENDIMENTO  (colocacao media de quem foi contratado)");
        System.out.printf(Locale.ROOT, "%-4s %14s %14s %14s %14s%n",
                "n", "regra 37%", "regra de posto", "chance 37%", "chance posto");
        for (int n = 3; n <= 9; n++) {
            Varredura varredura = new Varredura(n);
            Varredura.Resultado trintaESete = varredura.medir(Regra.limiar(Otima.melhorLimiar(n)));
            Varredura.Resultado posto = varredura.medir(Otima.regraDePosto(n));
            System.out.printf(Locale.ROOT, "%-4d %14.3f %14.3f %13.1f%% %13.1f%%%n",
                    n, trintaESete.postoMedio(), posto.postoMedio(),
                    100 * trintaESete.chanceDoMelhor(), 100 * posto.chanceDoMelhor());
        }
        System.out.println();
        System.out.println("a conta por programacao dinamica, conferida pela varredura:");
        for (int n = 3; n <= 9; n++) {
            double pelaConta = Otima.postoEsperado(n);
            double pelaVarredura = new Varredura(n).medir(Otima.regraDePosto(n)).postoMedio();
            System.out.printf(Locale.ROOT, "  n=%d  conta %.4f   varredura %.4f   %s%n",
                    n, pelaConta, pelaVarredura,
                    Math.abs(pelaConta - pelaVarredura) < 1e-9 ? "batem" : "NAO BATEM");
        }
        System.out.println();
    }
}
