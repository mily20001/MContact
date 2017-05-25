# MContact communicator 
It's a peer-to-peer online communicator, which uses end to end encryption. It was created as university project.

## How it works?
* You and your partner have to launch application and at least one of you have to open port on his router
* Set your name in app and choose proper port number on which app will be waiting for new connections
* Click connect, enter your partner's url or ip and port
* Wait a moment for key exchange. App sends 128bit AES key encrypted by 2048bit RSA key making your connection secure
* Now you can talk with your partner. You can also see connection details such as AES hash to make sure that no one is spying your connection
* Red frame around message means that it was not delivered (yet), green one means that everything is ok