# secretaria

O problema da parada ótima resolvido do zero em Java 21, com **todas as ordens de
chegada varridas** — e a regra dos 37 por cento conferida em vez de citada.

A história é conhecida: você entrevista n candidatos, um por vez, em ordem
aleatória. Depois de cada entrevista precisa decidir na hora, e quem é recusado
não volta. A regra famosa diz para observar os primeiros 37% sem contratar
ninguém e depois pegar o primeiro que for melhor que todos eles.

Aqui ela não é citada, é medida. Para cada tamanho, cada limiar é rodado contra
**as n! ordens possíveis**, então todo número é uma fração exata e não "mais ou
menos 37%".

```
mvn -B -ntp verify
mvn -B -ntp exec:java -Dexec.args="limiares"   # (ou otimo, arrependimento)
```

## O que quem decide realmente sabe

A parte mais fácil de errar neste problema é dar informação demais a quem
decide. Quem entrevista **não sabe** que o terceiro candidato é "o sétimo melhor
de dez": sabe só que ele é o melhor dos três vistos até agora, ou o segundo
melhor deles. A classe `Entrevista` carrega exatamente isso, e nada mais.

## Chance de ficar com o melhor, por limiar

| n | k=0 | k=1 | k=2 | k=3 | k=4 | k=5 | k=6 | melhor k |
|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 3 | 33,3% | **50,0%** | 33,3% | | | | | 1 |
| 4 | 25,0% | **45,8%** | 41,7% | 25,0% | | | | 1 |
| 5 | 20,0% | 41,7% | **43,3%** | 35,0% | 20,0% | | | 2 |
| 6 | 16,7% | 38,1% | **42,8%** | 39,2% | 30,0% | 16,7% | | 2 |
| 7 | 14,3% | 35,0% | **41,4%** | 40,7% | 35,2% | 26,2% | 14,3% | 2 |
| 8 | 12,5% | 32,4% | 39,8% | **41,0%** | 38,0% | 31,8% | 23,2% | 3 |
| 9 | 11,1% | 30,2% | 38,2% | **40,6%** | 39,3% | 35,3% | 29,0% | 3 |

Repare nas duas pontas de cada linha: **pegar o primeiro candidato e esperar até
o último dão exatamente a mesma chance**, 1/n. As duas estratégias que parecem
opostas são igualmente ruins, e pelo mesmo motivo: nenhuma das duas usa
informação.

## A fórmula erra o limiar, e não é raro

| n | melhor k | n/e | chance do melhor | chance da fórmula | erra? |
|---:|---:|---:|---:|---:|---|
| 1 | 0 | 0 | 100,0% | 100,0% | - |
| 2 | 0 | 1 | 50,0% | 50,0% | **SIM** |
| 3 | 1 | 1 | 50,0% | 50,0% | - |
| 4 | 1 | 1 | 45,8% | 45,8% | - |
| 5 | 2 | 2 | 43,3% | 43,3% | - |
| 6 | 2 | 2 | 42,8% | 42,8% | - |
| 7 | 2 | 3 | 41,4% | 40,7% | **SIM** |
| 8 | 3 | 3 | 41,0% | 41,0% | - |
| 9 | 3 | 3 | 40,6% | 40,6% | - |
| 10 | 3 | 4 | 39,9% | 39,8% | **SIM** |

**Em 3 dos 10 primeiros tamanhos a fórmula n/e aponta o limiar errado**, e sempre
para o mesmo lado: ela manda observar mais do que devia. Com sete candidatos isso
custa 0,7 ponto de chance.

A fórmula não está errada: ela é um limite, e limites valem quando n é grande.
O detalhe é que ninguém entrevista mil candidatos. Nos tamanhos em que a decisão
realmente acontece, o limiar certo sai da varredura, não da divisão por e.

E repare que o melhor k **cresce aos saltos**: é 1 para n de 3 a 4, 2 para n de 5
a 7, 3 para n de 8 a 10. A chance do melhor limiar cai a cada n e nunca desce de
1/e = 36,8%, que é o valor para onde ela converge por cima.

## Querer o melhor é caro

A regra dos 37% é tudo ou nada: ficar com o segundo melhor vale tanto quanto
ficar com o último. Se o que importa é **contratar alguém bom**, e não
exatamente o melhor, a estratégia ótima é outra:

| n | colocação média com a regra dos 37% | com a regra de posto | acerta o melhor: 37% | acerta o melhor: posto |
|---:|---:|---:|---:|---:|
| 3 | 1,667 | 1,667 | 50,0% | 50,0% |
| 4 | 1,875 | 1,875 | 45,8% | 41,7% |
| 5 | 2,200 | **2,050** | 43,3% | 40,0% |
| 6 | 2,333 | **2,217** | 42,8% | 35,6% |
| 7 | 2,476 | **2,276** | 41,4% | 37,4% |
| 8 | 2,813 | **2,400** | 41,0% | 34,4% |
| 9 | 2,917 | **2,496** | 40,6% | 34,4% |

Com nove candidatos, quem persegue o melhor acaba contratando, em média, o
**2,9º** colocado. Quem mira a colocação média contrata o **2,5º** — e acerta o
melhor bem menos vezes. São duas perguntas diferentes com duas respostas
diferentes, e chamar uma das duas de "a estratégia ótima" esconde isso.

A regra que minimiza a colocação faz uma coisa que a outra nunca faz: ela
**aceita candidato que não é o melhor até agora**. Perto do fim, o segundo melhor
dos oito vistos é bom o bastante.

## A conta e a varredura se conferem

A regra de posto sai de programação dinâmica de trás para frente, e essa conta
não é acreditada: a varredura roda a regra resultante nas n! ordens e os dois
números têm que bater até a nona casa.

```
n=3  conta 1,6667   varredura 1,6667   batem
n=5  conta 2,0500   varredura 2,0500   batem
n=7  conta 2,2762   varredura 2,2762   batem
n=9  conta 2,4960   varredura 2,4960   batem
```

É esse cruzamento que pega erro de índice em recorrência, que é o tipo de bug que
passa em teste de exemplo e some na revisão.

## O que as medidas me corrigiram

1. **"A regra dos 37% é a estratégia ótima."** É ótima para uma pergunta só, e
   para a outra é ruim: em média ela entrega o 2,9º melhor de nove, contra o 2,5º
   da regra de colocação.
2. **"O limiar é n/e."** Em 3 dos 10 primeiros tamanhos não é, e sempre para o
   mesmo lado. O limiar ótimo cresce aos saltos, não de um em um.
3. **"Pegar o primeiro é a pior estratégia possível."** Empata exatamente com
   esperar até o último: as duas dão 1/n. O ruim não é decidir cedo nem decidir
   tarde, é decidir sem usar o que já se viu.
4. **"A regra ótima sempre pega o melhor até agora."** A que maximiza a chance,
   sim. A que minimiza a colocação aceita o segundo melhor perto do fim, e é
   justamente por isso que ela ganha.

## Como está organizado

- `Entrevista`: o que quem decide sabe, e só isso.
- `Regra`: uma estratégia, como função de uma entrevista. Limiar fixo, fração do
  total, limiares por posição.
- `Varredura`: roda uma regra nas n! ordens de chegada. Permutação por troca no
  próprio vetor, sem alocar.
- `Otima`: os dois ótimos. O melhor limiar sai de varredura; a regra de
  colocação, de programação dinâmica conferida pela varredura.
- `Medidor`: as três tabelas acima, rodando no CI nos três sistemas.

35 testes.
