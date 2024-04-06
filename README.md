# SecurityProxyLDAPRetriever ver1.0 by MrArni_ZIO - Released
### Description
Tool atto a scaricare un file csv in base al config.json e la chiave "LDAPGroups", cioè: avendo configurato LDAPGroups, <br>
il tool va fetchare per la classe indicata nella `query` le sicurezze impostate. Esempio: ho configurato nel LDAPGroups, <br>
Un distinguishedName: `COOKER/CN=COOKER,OU=KITCHEN,OU=Groups,DC=HOME,DC=ORG`, il tool a sua volta va a vedere per gli oggetti della classe, <br> 
se tale distinguishedName è censito. Se sì, allora poi verrà prodotto un CSV file in cui è indicato il dN a fianco l'oggetto. 
Potete liberamente cambiare le chiavi su json, facendolo puntare all'ambiente necessario e cambiare la query alla classe d'interesse.
#### _Usage_
`java -jar path\filename.jar path\config.json`.<br>
Per qualsiasi bug, feature request - non esitate a chiamarmi, messagarmi.
