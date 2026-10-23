package br.com.conde.secretaria;

/**
 * O que quem decide realmente sabe na hora de decidir.
 *
 * <p>Esta classe existe para impedir uma trapaca que e facil de cometer sem
 * perceber: usar a nota absoluta do candidato. Quem entrevista nao sabe que o
 * terceiro candidato e "o setimo melhor de dez"; sabe apenas que ele e **o
 * melhor dos tres vistos ate agora**, ou que e o segundo melhor deles. E so com
 * essa informacao relativa que a decisao pode ser tomada.
 *
 * @param posicao     qual entrevista e esta, de 1 ate o total
 * @param total       quantos candidatos existem ao todo
 * @param postoLocal  a colocacao deste candidato entre os ja vistos (1 = melhor ate agora)
 */
public record Entrevista(int posicao, int total, int postoLocal) {

    /** Este candidato e o melhor de todos os vistos ate agora? */
    public boolean melhorAteAgora() {
        return postoLocal == 1;
    }

    /** E a ultima entrevista: quem recusar aqui fica sem ninguem. */
    public boolean ultima() {
        return posicao == total;
    }

    /** Quantos ja foram vistos, contando este. */
    public int vistos() {
        return posicao;
    }
}
