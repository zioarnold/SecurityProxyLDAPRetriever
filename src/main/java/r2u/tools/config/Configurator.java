package r2u.tools.config;

import com.filenet.api.core.ObjectStore;

import java.util.ArrayList;
import java.util.HashMap;

public class Configurator {
    private static Configurator instance = null;
    private String uriSource;
    private String sourceCPEObjectStore;
    private String sourceCPEUsername;
    private String sourceCPEPassword;
    private String jaasStanzaName;
    private String query;
    private String phase;
    private HashMap<String, String> ldapGroups;
    private ArrayList<String> ldapGroupToAdd;
    private ObjectStore objectStore;
    private ArrayList<String> groupLookup;

    private Configurator() {

    }

    public static synchronized Configurator getInstance() {
        if (instance == null) {
            instance = new Configurator();
        }
        return instance;
    }

    public String getUriSource() {
        return uriSource;
    }

    public void setUriSource(String uriSource) {
        this.uriSource = uriSource;
    }

    public String getSourceCPEObjectStore() {
        return sourceCPEObjectStore;
    }

    public void setSourceCPEObjectStore(String sourceCPEObjectStore) {
        this.sourceCPEObjectStore = sourceCPEObjectStore;
    }

    public String getSourceCPEUsername() {
        return sourceCPEUsername;
    }

    public void setSourceCPEUsername(String sourceCPEUsername) {
        this.sourceCPEUsername = sourceCPEUsername;
    }

    public String getSourceCPEPassword() {
        return sourceCPEPassword;
    }

    public void setSourceCPEPassword(String sourceCPEPassword) {
        this.sourceCPEPassword = sourceCPEPassword;
    }

    public String getJaasStanzaName() {
        return jaasStanzaName;
    }

    public void setJaasStanzaName(String jaasStanzaName) {
        this.jaasStanzaName = jaasStanzaName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public HashMap<String, String> getLdapGroups() {
        return ldapGroups;
    }

    public void setLdapGroups(HashMap<String, String> ldapGroups) {
        this.ldapGroups = ldapGroups;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public ArrayList<String> getLdapGroupToAdd() {
        return ldapGroupToAdd;
    }

    public void setLdapGroupToAdd(ArrayList<String> ldapGroupToAdd) {
        this.ldapGroupToAdd = ldapGroupToAdd;
    }

    public ObjectStore getObjectStore() {
        return objectStore;
    }

    public void setObjectStore(ObjectStore objectStore) {
        this.objectStore = objectStore;
    }

    public void setGroupLookup(ArrayList<String> groupLookup) {
        this.groupLookup = groupLookup;
    }

    public ArrayList<String> getGroupLookup() {
        return groupLookup;
    }
}
