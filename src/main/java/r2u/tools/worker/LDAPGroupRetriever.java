package r2u.tools.worker;

import com.filenet.api.collection.AccessPermissionList;
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
import r2u.tools.constants.Constants;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class LDAPGroupRetriever {
    private static final Logger logger = Logger.getLogger(LDAPGroupRetriever.class.getName());

    public void startWork(ObjectStore objectStoreSource, String query, HashMap<String, String> ldapGroups) {
        long startTime, endTime;
        startTime = System.currentTimeMillis();
        Iterator<?> iterator = fetchRows(objectStoreSource, query);
        ArrayList<String> ldap = new ArrayList<>();
        if (iterator != null) {
            while (iterator.hasNext()) {
                RepositoryRow repositoryRow = (RepositoryRow) iterator.next();
                Id idValue = repositoryRow.getProperties().getIdValue("ID");
                CustomObject customObject = Factory.CustomObject.fetchInstance(objectStoreSource, idValue, null);
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
        logger.info("Security fixer terminated within: " + DurationFormatUtils.formatDuration(endTime - startTime, Constants.dateTimeFormat, true));
    }

    private static Iterator<?> fetchRows(ObjectStore objectStoreSource, String query) {
        if (objectStoreSource != null) {
            SearchSQL searchSQL = new SearchSQL();
            searchSQL.setQueryString(query);
            return new SearchScope(objectStoreSource).fetchRows(searchSQL, null, null, Boolean.TRUE).iterator();
        }
        return null;
    }
}
