Diogo Pinto 52763 
Francisco Ramalho 53472
Joao Funenga 53504

----------------

** COMANDOS DE COMPILACAO **

- Comando para compilar as classes relativas ao servidor:
javac -d bin src/Server/SeiTchizServer.java src/Server/CatalogoClientes.java src/Server/CatalogoGrupos.java src/Server/Cliente.java src/Server/Grupo.java src/Server/Mensagem.java src/Server/Photo.java


- Comando para compilar a classe relativa ao cliente:
javac -d bin src/Client/SeiTchiz.java

----------------

** COMANDOS DE EXECUCAO **

Ter em atencao, que para ser possivel o uso da aplicacao, e necessario iniciar o servidor primeiro que os clientes.

- Exemplo de comando para execucao do servidor:
Executar ficheiros .class:
java -cp bin -Djava.security.manager -Djava.security.policy==server.policy Server.SeiTchizServer 45678

Executar ficheiro .jar:
java -cp bin -Djava.security.manager -Djava.security.policy==server.policy -jar SeiTchizServer.jar 45678


- Exemplo de comando para execucao do cliente:
Executar ficheiros .class:
java -cp bin -Djava.security.manager -Djava.security.policy==client.policy Client.SeiTchiz 127.0.0.1:45678 userName password123

Executar ficheiro .jar:
java -cp bin -Djava.security.manager -Djava.security.policy==client.policy -jar SeiTchiz.jar 127.0.0.1:45678 userName password123

----------------

Argumentos usados para servidor:
45678 - porto exemplo

Argumentos usados para cliente:
127.0.0.1:45678 - serverAddress
userName - userID
password123 - password

----------------

Limitacoes: 
As fotografias usadas para o comando Post devem estar dentro da pasta "test" na raiz do projeto (devido a permissoes dos policies).
A mensagem enviada para os grupos nao deve conter "%%" (é uma palavra reservada para tratamento de ficheiros).
A pasta bin não pode ser apagada, apenas devem ser apagados os seus conteúdos.


O codigo foi testado na imagem de linux do DI e no Windows 10. 