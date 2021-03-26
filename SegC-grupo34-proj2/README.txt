
Projeto 2 em trabalho, vai sendo atualizado.

Diogo Pinto 52763 
Francisco Ramalho 53472
Joao Funenga 53504

----------------

** COMANDOS DE COMPILACAO **

- Comando para compilar as classes relativas ao servidor:
javac -d bin src/Server/SeiTchizServer.java src/Server/CatalogoClientes.java src/Server/CatalogoGrupos.java src/Server/Cliente.java src/Server/Grupo.java src/Server/Mensagem.java src/Server/Photo.java src/Server/Autenticacao.java


- Comando para compilar a classe relativa ao cliente:
javac -d bin src/Client/SeiTchiz.java

----------------

** COMANDOS DE EXECUCAO **

Ter em atencao, que para ser possivel o uso da aplicacao, e necessario iniciar o servidor primeiro que os clientes.

- Exemplo de comando para execucao do servidor:
Executar ficheiros .class: 
java -cp bin -Djava.security.manager -Djava.security.policy==server.policy Server.SeiTchizServer 45678 keystore keystore-password
java -cp bin -Djava.security.manager -Djava.security.policy==server.policy Server.SeiTchizServer 45678 servidor servidor

Executar ficheiro .jar:
java -cp bin -Djava.security.manager -Djava.security.policy==server.policy -jar SeiTchizServer.jar 45678 servidor servidor


- Exemplo de comando para execucao do cliente:
Executar ficheiros .class:
java -cp bin -Djava.security.manager -Djava.security.policy==client.policy Client.SeiTchiz 127.0.0.1:45678 truststore keystore keystore-password clientid

java -cp bin -Djava.security.manager -Djava.security.policy==client.policy Client.SeiTchiz 127.0.0.1:45678 truststore_client clientid clientid clientid
java -cp bin -Djava.security.manager -Djava.security.policy==client.policy Client.SeiTchiz 127.0.0.1:45678 truststore_client manelteste manelteste manelteste
java -cp bin -Djava.security.manager -Djava.security.policy==client.policy Client.SeiTchiz 127.0.0.1:45678 truststore_client mantorras mantorras mantorras


Executar ficheiro .jar:
java -cp bin -Djava.security.manager -Djava.security.policy==client.policy -jar SeiTchiz.jar 127.0.0.1:45678 truststore_client clientid clientid clientid

----------------

Argumentos usados para servidor:
45678 - porto exemplo
keystore - Par de chaves do servidor
keystore-password - Password da keystore

Argumentos usados para cliente:
127.0.0.1:45678 - serverAddress
truststore - Certificado de chave pública do servidor
keystore - Par de chaves do clientID
keystore-password - Password da keystore
clientId - Utilizador local

----------------

Limitacoes: 
As fotografias usadas para o comando Post devem estar dentro da pasta "test" na raiz do projeto (devido a permissoes dos policies).
A mensagem enviada para os grupos nao deve conter "%%" (é uma palavra reservada para tratamento de ficheiros).
A pasta bin não pode ser apagada, apenas devem ser apagados os seus conteúdos.
O nome do utilizador ou do grupo nao devem conter ":" (é um caracter reservado para tratamento de ficheiros).

-----------------

Observações
As pastas wall e data (incluindo as subpastas) podem ser apagadas antes de correr o código. Caso existam ao iniciar o programa, serão carregados os
dados existentes. Caso estas pastas tenham sido apagadas, são geradas automaticamente e a informação acerca dos clientes será "apagada".
O codigo foi testado na imagem de linux do DI e no Windows 10. 

----------------
Gerar chave privada e publica

keytool -genkeypair -alias servidor -keyalg RSA -keysize 2048 -storetype JCEKS -keystore servidor

keytool -genkeypair -alias mantorras -keyalg RSA -keysize 2048 -storetype JCEKS -keystore mantorras

keytool -genkeypair -alias ALIASDACHAVE r -keyalg RSA -keysize 2048 -storetype JCEKS -keystore NOMEFICHEIROKEYSTORE
----------------

A truststore do cliente e do servidor têm as duas a mesma password: "servidor"
