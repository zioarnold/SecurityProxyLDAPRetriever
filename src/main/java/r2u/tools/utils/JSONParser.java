package r2u.tools.utils;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import r2u.tools.config.Configurator;
import r2u.tools.conn.FNConnector;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSONParser {
    private static final Logger logger = Logger.getLogger(JSONParser.class.getName());

    public void parseJson(String json) {
        URI uri = Paths.get(json).toUri();
        JSONObject jsonObject;
        try {
            jsonObject = getJSON(new URI(uri.toString()).toURL());
        } catch (IOException | URISyntaxException e) {
            logger.error("Unable initialize jsonObject", e);
            throw new RuntimeException(e);
        }
        String sourceCPE = jsonObject.getString("sourceCPE"),
                sourceCPEObjectStore = jsonObject.getString("sourceCPEObjectStore"),
                sourceCPEUsername = jsonObject.getString("sourceCPEUsername"),
                sourceCPEPassword = jsonObject.getString("sourceCPEPassword"),
                jaasStanzaName = jsonObject.getString("jaasStanzaName"),
                query = jsonObject.getString("query");

        HashMap<String, String> ldapGroups = convertArrayList2HashMap(
                listObject2ListString(
                        jsonObject.getJSONArray("LDAPGroups").toList()
                )
        );

        if (sourceCPE.isEmpty()) {
            logger.error("SourceCPE is empty. Aborting!");
            System.exit(-1);
        }
        if (sourceCPEObjectStore.isEmpty()) {
            logger.error("sourceCPEObjectStore is empty. Aborting!");
            System.exit(-1);
        }
        if (sourceCPEUsername.isEmpty()) {
            logger.error("sourceCPEUsername is empty. Aborting!");
            System.exit(-1);
        }
        if (sourceCPEPassword.isEmpty()) {
            logger.error("sourceCPEPassword is empty. Aborting!");
            System.exit(-1);
        }
        if (jaasStanzaName.isEmpty()) {
            logger.error("jaasStanzaName is empty. Aborting!");
            System.exit(-1);
        }
        if (ldapGroups.isEmpty()) {
            logger.error("ldapGroups is empty. Aborting!");
            System.exit(-1);
        }

        Configurator instance = Configurator.getInstance();
        instance.setUriSource(sourceCPE);
        instance.setSourceCPEObjectStore(sourceCPEObjectStore);
        instance.setSourceCPEUsername(sourceCPEUsername);
        instance.setSourceCPEPassword(sourceCPEPassword);
        instance.setJaasStanzaName(jaasStanzaName);
        instance.setQuery(query);
        instance.setLdapGroups(ldapGroups);
        FNConnector fnConnector = new FNConnector();
        fnConnector.initWork();
    }

    private static JSONObject getJSON(URL url) throws IOException {
        String string = IOUtils.toString(url, StandardCharsets.UTF_8);
        return new JSONObject(string);
    }

    private static ArrayList<String> listObject2ListString(List<Object> ldap) {
        ArrayList<String> ldapGroups = new ArrayList<>();
        //Converto oggetti in stringhe
        for (Object secProxy : ldap) {
            ldapGroups.add(secProxy.toString());
        }
        return ldapGroups;
    }

    private static HashMap<String, String> convertArrayList2HashMap(ArrayList<String> ldapGroupsArrayList) {
        HashMap<String, String> ldapGroupHashMap = new HashMap<>();
        for (String ldap : ldapGroupsArrayList) {
            ldapGroupHashMap.put(ldap.split("/")[1], ldap.split("/")[0]);
        }
        return ldapGroupHashMap;
    }
}
