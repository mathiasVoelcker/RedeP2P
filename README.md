# RedeP2P - Executando o programa com docker

0. Este passo a passo parte do principio que você já tem instalado o docker em sua máquina e já clonou o repositório.

1. No dir RedesP2P, execute o comando -> "sudo docker build -t redes ." e aguarde a confirmação de criação da imagem.
2. Agora em qualquer diretório, execute o comando -> "sudo docker run -it redes", e devera estar em um terminal opt/app/build/libs
3. Para rodar o programa digite java -jar redesP2p-1.0-SNAPSHOT.jar [argumentosOpcionais]
