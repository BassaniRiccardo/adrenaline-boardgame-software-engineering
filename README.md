# Prova Finale Ingegneria del Software 2019
## Gruppo AM43

- ###   10529686    Marco Bagatella ([@marcobaga](https://github.com/marcobaga))<br>marco.bagatella@mail.polimi.it
- ###   10497506    Riccardo Bassani ([@BassaniRiccardo](https://github.com/BassaniRiccardo))<br>riccardo1.bassani@mail.polimi.it
- ###   10503657    Davide Aldè ([@davidealde](https://github.com/davidealde))<br>davide.alde@mail.polimi.it

| Functionality | State |
|:-----------------------|:------------------------------------:|
| Basic rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Complete rules | [![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#) |
| Socket | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| RMI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| GUI | [![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#) |
| CLI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Multiple games | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Persistence | [![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#) |
| Domination or Towers modes | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |
| Terminator | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |

<!--
[![RED](https://placehold.it/15/f03c15/f03c15)](#)
[![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#)
[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)RED
-->

###Setup:
- The server uses port 3994 for RMI and 4198 for TCP.
- Server parameters must be set in resources/server.properties (IP and ports).
- Client parameters can be either set in resources/client.properties or you can use

    java -jar client.jar [serverIP] [serverPort] [clientIP] 