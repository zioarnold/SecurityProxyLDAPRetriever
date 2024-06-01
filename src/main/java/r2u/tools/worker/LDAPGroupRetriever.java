package r2u.tools.worker;

import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.constants.AccessType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.CustomObject;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.security.AccessPermission;
import com.filenet.api.util.Id;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.log4j.Logger;
import r2u.tools.config.Configurator;
import r2u.tools.constants.Constants;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class LDAPGroupRetriever {
    private static final Logger logger = Logger.getLogger(LDAPGroupRetriever.class.getName());
    private final Configurator instance = Configurator.getInstance();

    /**
     * Metodo atto a produrre un file csv contenente i gruppi LDAP assegnati alla custom object o classe documentale
     *
     * @param query      query non puo` essere vuota
     * @param ldapGroups gruppi ldap da ricercare
     */
    public void retrieveLDAPGroups(String query, HashMap<String, String> ldapGroups) {
        long startTime, endTime;
        startTime = System.currentTimeMillis();
        Iterator<?> iterator = fetchRows(instance.getObjectStore(), query);
        ArrayList<String> ldap = new ArrayList<>();
        if (iterator != null) {
            while (iterator.hasNext()) {
                RepositoryRow repositoryRow = (RepositoryRow) iterator.next();
                Id idValue = repositoryRow.getProperties().getIdValue("ID");
                CustomObject customObject = Factory.CustomObject.fetchInstance(instance.getObjectStore(), idValue, null);
                AccessPermissionList customObjectPermissions = customObject.get_Permissions();
                for (Object fetchedDocumentPermission : customObjectPermissions) {
                    AccessPermission accessPermission = (AccessPermission) fetchedDocumentPermission;
                    if (ldapGroups.containsKey(accessPermission.get_GranteeName())) {
                        String code = repositoryRow.getProperties().getStringValue("codice");
                        logger.info("Found: " + accessPermission.get_GranteeName().split(",")[0] + "," + code);
                        ldap.add(ldapGroups.get(accessPermission.get_GranteeName()) + "," + code);
                    }
                }
            }
        } else {
            logger.error("Unable to fetch rows from object store");
        }

        try (PrintWriter writer = new PrintWriter("output.csv", "UTF-8")) {
            for (String out : ldap) {
                writer.println(out);
            }
        } catch (Exception exception) {
            logger.error("Error on create/writing to file output.csv", exception);
        }
        endTime = System.currentTimeMillis();
        logger.info("retrieveLDAPGroups terminated within: " + DurationFormatUtils.formatDuration(endTime - startTime, Constants.dateTimeFormat, true));
    }

    /**
     * Funzione atto a ricercare i dati in base alla query
     *
     * @param objectStoreSource dati di object store a cui bisogna fare richiesta
     * @param query             variable in cui viene passata la query configurabile su config.json
     * @return variabile che contiene i dati in base alla query passatogli
     */
    private static Iterator<?> fetchRows(ObjectStore objectStoreSource, String query) {
        if (objectStoreSource != null) {
            SearchSQL searchSQL = new SearchSQL();
            searchSQL.setQueryString(query);
            return new SearchScope(objectStoreSource).fetchRows(searchSQL, null, null, Boolean.TRUE).iterator();
        }
        return null;
    }

    /**
     * Metodo atto ad aggiungere un gruppo alla sicurezza
     *
     * @param query variabile di ricerca dei dati
     */
    public void assignNetCoServCoGroups(String query) {
        long startTime, endTime;
        startTime = System.currentTimeMillis();
        Iterator<?> iterator = fetchRows(instance.getObjectStore(), query);
        Integer accessMask = 0, inheritableDepth = 0;
        AccessType accessType = null;
        boolean isServco = false;
        if (iterator != null) {
            while (iterator.hasNext()) {
                RepositoryRow repositoryRow = (RepositoryRow) iterator.next();
                Id idValue = repositoryRow.getProperties().getIdValue("ID");
                CustomObject customObject = Factory.CustomObject.fetchInstance(instance.getObjectStore(), idValue, null);
                AccessPermissionList customObjectPermissions = customObject.get_Permissions();
                //Vedo la lista di tutte le utenze/gruppi (per capirci tab Security)
                for (Object fetchedDocumentPermission : customObjectPermissions) {
                    AccessPermission accessPermission = (AccessPermission) fetchedDocumentPermission;
                    //Vedo se contiene un gruppo qualsiasi con affisso SERCO
                    if (accessPermission.get_GranteeName().contains("SERCO")) {
                        isServco = true;
                    }
                    if (accessPermission.get_GranteeName().equals(instance.getGroupLookup().get(0))) {
                        accessMask = accessPermission.get_AccessMask();
                        accessType = accessPermission.get_AccessType();
                        inheritableDepth = accessPermission.get_InheritableDepth();

                    }
                }
                AccessPermission permission = Factory.AccessPermission.createInstance();
                //Se ne contiene, allora aggiungo in quella lista il gruppo SERCO o NETCO con criteri di sicurezza identici al gruppo lookup e poi salvo
                if (isServco) {
                    logger.info("Security proxy is SERCO, adding " + instance.getLdapGroupToAdd().get(0) + " with the same parameter of " + instance.getGroupLookup().get(0));
                    permission.set_GranteeName(instance.getLdapGroupToAdd().get(0));
                } else {
                    logger.info("Security proxy is NETCO, adding " + instance.getLdapGroupToAdd().get(1) + " with the same parameter of " + instance.getGroupLookup().get(0));
                    permission.set_GranteeName(instance.getLdapGroupToAdd().get(1));
                }
                permission.set_AccessType(accessType);
                permission.set_AccessMask(accessMask);
                permission.set_InheritableDepth(inheritableDepth);
                //noinspection unchecked
                customObjectPermissions.add(permission);
                customObject.set_Permissions(customObjectPermissions);
                customObject.save(RefreshMode.REFRESH);
                isServco = false;

            }
        } else {
            logger.error("Unable to fetch rows from object store");
        }
        endTime = System.currentTimeMillis();
        logger.info("assignNetCoServCoGroups terminated within: " + DurationFormatUtils.formatDuration(endTime - startTime, Constants.dateTimeFormat, true));
    }
}
