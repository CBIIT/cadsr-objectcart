package gov.nih.nci.objectCart.dao.orm;

import java.util.Map;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.acegi.authentication.CSMAuthenticationProvider;
import gov.nih.nci.security.authorization.instancelevel.InstanceLevelSecurityHelper;
import gov.nih.nci.system.dao.orm.ORMDAOFactoryBean;
import gov.nih.nci.system.dao.orm.ORMDAOImpl;

public class CartORMDAOFactoryBean extends ORMDAOFactoryBean {

	
	public CartORMDAOFactoryBean(String configLocation,
			Properties systemProperties, Map systemPropertiesMap)
			throws Exception {
		super(configLocation, systemProperties, systemPropertiesMap);
	}

	public void afterPropertiesSet() throws Exception
	{
		super.afterPropertiesSet();
		if (isSecurityEnabledFlag())
			setOrmDAO(new CartORMDAOImpl((SessionFactory)this.getSessionFactory(), (Configuration)this.getConfiguration(), isCaseSensitive(), getResultCountPerQuery(), isInstanceLevelSecurity(), isAttributeLevelSecurity(), getAuthenticationProvider()));
		else
			setOrmDAO(new CartORMDAOImpl((SessionFactory)this.getSessionFactory(), (Configuration)this.getConfiguration(), isCaseSensitive(), getResultCountPerQuery(), isInstanceLevelSecurity(), isAttributeLevelSecurity(), null));			
	}
	
	protected void postProcessConfiguration(Configuration configuration)
	{
		if (isInstanceLevelSecurity())
			InstanceLevelSecurityHelper.addFilters(getAuthorizationManager(), configuration);
	}
	
}
