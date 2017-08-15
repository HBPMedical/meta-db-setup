package eu.humanbrainproject.mip.migrations.meta;

import org.flywaydb.core.api.callback.BaseFlywayCallback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplyHierarchyPatchesCallback extends BaseFlywayCallback {
    private static Logger LOG = Logger.getLogger(ApplyHierarchyPatchesCallback.class.getName());

    public void afterMigrate(Connection connection) {

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * from meta_variables;");
            stmt.execute();
            final ResultSet resultSet = stmt.getResultSet();

            while (resultSet.next()) {
                String source = resultSet.getString("source");

                        hierarchy_patch jsonb NOT NULL,
                        target_table varchar(256) NOT NULL

            }

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Cannot apply hierarchy patches", e);
        }

    }

}
