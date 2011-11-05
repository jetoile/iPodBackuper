Ce projet permet d'extraire les fichiers MP3 d'un IPOD sur le disque dur de l'ordinateur :
- ces fichiers sont présents et accessibles dans un fichier caché de l'IPOD
- ces fichiers ont été renommés lors de l'import
- seules les informations présentes dans les métadonnées des fichiers contient les informations nécessaires à la copie (id3tags)

L'extraction suit le pattern suivant :
- nom du répertoire : <artiste> - <album> /
- nom du fichier : <trackNumber> - <nomChanson>.mp3

Il demande à l'utilisateur :
- le répertoire où se trouvent les fichiers mp3 (une concaténation est automatiquement faite (iPod_Control/Music) 
- le répertoire où copier les fichiers mp3 extraient
- [le nom de l'artiste à extraire]

EXECUTION :
- ant run
- ./run.sh (à faire après compilation via ant dist)

FICHIERS NECESSAIRES A L'EXECUTION :
- répertoire conf
- répertoire lib
- à la racine iPodBackuper.jar


