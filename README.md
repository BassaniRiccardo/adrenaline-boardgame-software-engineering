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

**Setup:**
- The server uses port 3994 for RMI and 4198 for TCP.
- Client parameters can be set at launch:

    java -jar client.jar [serverIP] [serverPort] [clientIP]
     
- If no args are provided, the client will try to read a client.properties file in the same folder as the jar.
- If this file is not available, default parameters will be loaded from those contained in resources/client.properties before building the jar.

- Server properties (IPs and ports) must be set in a server.property file, which can be either in the same folder as the jar file, or in /resources before building the jar.
The server will first check for properties in the same folder.