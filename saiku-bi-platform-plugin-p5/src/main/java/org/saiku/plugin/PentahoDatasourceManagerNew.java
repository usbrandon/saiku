/*  
 *   Copyright 2012 OSBI Ltd
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.saiku.plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import mondrian.olap.MondrianProperties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.repository.RepositoryException;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.plugin.action.mondrian.catalog.IMondrianCatalogService;
import org.pentaho.platform.plugin.action.olap.IOlapService;
import org.saiku.database.dto.MondrianSchema;
import org.saiku.datasources.connection.ISaikuConnection;
import org.saiku.datasources.connection.RepositoryFile;
import org.saiku.datasources.datasource.SaikuDatasource;
import org.saiku.repository.AclEntry;
import org.saiku.repository.IRepositoryObject;
import org.saiku.service.datasource.IDatasourceManager;
import org.saiku.service.user.UserService;

public class PentahoDatasourceManagerNew implements IDatasourceManager {

    private static final Log LOG = LogFactory.getLog(PentahoDatasourceManagerNew.class);

    private Map<String, SaikuDatasource> datasources =
        Collections.synchronizedMap(new HashMap<String, SaikuDatasource>());

    private String saikuDatasourceProcessor;

    private String saikuConnectionProcessor;

    private String dynamicSchemaProcessor;

    private IPentahoSession session;

    private IMondrianCatalogService catalogService;

    private String datasourceResolver;

	private IOlapService olapService;

    public void setDatasourceResolverClass(String datasourceResolverClass) {
        this.datasourceResolver = datasourceResolverClass;
    }

    public void setSaikuDatasourceProcessor(String datasourceProcessor) {
        this.saikuDatasourceProcessor = datasourceProcessor;
    }

    public void setSaikuConnectionProcessor(String connectionProcessor) {
        this.saikuConnectionProcessor = connectionProcessor;
    }

    public void setDynamicSchemaProcessor(String dynamicSchemaProcessor) {
        this.dynamicSchemaProcessor = dynamicSchemaProcessor;
    }

    public PentahoDatasourceManagerNew() {
    }

    public void init() {
        load();
    }

    public void load() {
        datasources.clear();
        loadDatasources();
    }

    public void unload() {

    }






    private Map<String, SaikuDatasource> loadDatasources() {
        try {


            this.session = PentahoSessionHolder.getSession();
            
            ClassLoader cl = this.getClass().getClassLoader();
            ClassLoader cl2 = this.getClass().getClassLoader().getParent();

            Thread.currentThread().setContextClassLoader(cl2);
            
            this.olapService = PentahoSystem.get( IOlapService.class, session );


            Thread.currentThread().setContextClassLoader(cl);
            if (StringUtils.isNotBlank(this.datasourceResolver)) {
                MondrianProperties.instance().DataSourceResolverClass.setString(this.datasourceResolver);
            }

            Set<String> uniqueCatalogs = new HashSet<String>(olapService.getCatalogNames(session));
            System.out.println(uniqueCatalogs);
            System.out.println("Catalogs Count: " + uniqueCatalogs.size());
            for (String name : uniqueCatalogs) {
                
                Properties props = new Properties();
                if (saikuConnectionProcessor != null) {
                    props.put(ISaikuConnection.CONNECTION_PROCESSORS, saikuConnectionProcessor);
                }

                SaikuDatasource sd = new SaikuDatasource(name, SaikuDatasource.Type.OLAP, props);
                datasources.put(name, sd);
            }
            return datasources;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e);
        }
        return new HashMap<String, SaikuDatasource>();
    }


    public SaikuDatasource addDatasource(SaikuDatasource datasource) {
        throw new UnsupportedOperationException();
    }

    public SaikuDatasource setDatasource(SaikuDatasource datasource) {
        throw new UnsupportedOperationException();
    }

    public List<SaikuDatasource> addDatasources(List<SaikuDatasource> datasources) {
        throw new UnsupportedOperationException();
    }

    public boolean removeDatasource(String datasourceName) {
        throw new UnsupportedOperationException();
    }

    public boolean removeSchema(String schemaName) {
        return false;
    }

    public Map<String, SaikuDatasource> getDatasources() {
        return loadDatasources();
    }

    public SaikuDatasource getDatasource(String datasourceName) {
        return loadDatasources().get(datasourceName);
    }

    public void addSchema(String file, String path, String name) {
        throw new UnsupportedOperationException();
    }

    public List<MondrianSchema> getMondrianSchema() {
        throw new UnsupportedOperationException();
    }

    public MondrianSchema getMondrianSchema(String catalog) {
        throw new UnsupportedOperationException();
    }

    public RepositoryFile getFile(String file) {
        throw new UnsupportedOperationException();
    }

    public String getFileData(String file) {
        throw new UnsupportedOperationException();
    }

    public void setUserService(UserService us) {

    }

    public List<MondrianSchema> getInternalFilesOfFileType(String type) throws RepositoryException {
        return null;
    }

    public void createFileMixin(String type) throws RepositoryException {

    }

    public byte[] exportRepository() {
        return new byte[0];
    }

    public void restoreRepository(byte[] data) {

    }

    public boolean hasHomeDirectory(String name) {
        return false;
    }

    public void restoreLegacyFiles(byte[] data) {

    }

    public String getFoodmartschema() {
        return null;
    }

    public void setFoodmartschema(String schema) {

    }

    public void setFoodmartdir(String dir) {

    }

    public String getFoodmartdir() {
        return null;
    }

    public String getDatadir() {
        return null;
    }

    public void setDatadir(String dir) {

    }

    public void setFoodmarturl(String foodmarturl) {

    }

    public String getFoodmarturl() {
        return null;
    }

    public void setACL(String a, String b, String c, List<String> d) {

    }

    public AclEntry getACL(String a, String b, List<String> c) {
        throw new UnsupportedOperationException();
    }

    public void deleteFolder(String folder) {
        throw new UnsupportedOperationException();
    }

    public void createUser(String username) {
        throw new UnsupportedOperationException();
    }

    public List<IRepositoryObject> getFiles(String type, String username, List<String> roles) {
        throw new UnsupportedOperationException();
    }

    public String saveFile(String path, String content, String user, List<String> roles) {
        throw new UnsupportedOperationException();

    }

    public String removeFile(String path, String user, List<String> roles) {
        return null;
    }

    public String moveFile(String source, String target, String user, List<String> roles) {
        return null;
    }

    public String saveInternalFile(String path, String content, String type) {
        return null;
    }

    public void removeInternalFile(String filePath) {
    
    }

    public String getInternalFileData(String file) {
        throw new UnsupportedOperationException();
    }

    public String getFileData(String file, String username, List<String> roles) {
        throw new UnsupportedOperationException();
    }
}
