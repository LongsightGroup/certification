package com.rsmart.certification.tool;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.entity.api.ResourceProperties;
//import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;
//import org.sakaiproject.tool.gradebook.facades.Authz;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;


/**
 * 
 * @author bbailla2, plukasew
 *
 */
public class ExtraUserPropertyUtility 
{
	//not needed?: public static final String EXTRA_USER_PROPERTY_COLUMN_PREVIS = "extraProp_";
	public static final String NULL_DISPLAY_VALUE= "";
	
	private static final String ENABLE_SAKAI_PROPERTY = "certification.extraUserProperties.enable";
	private static final String KEYS_SAKAI_PROPERTY = "certification.extraUserProperties.keys";
	private static final String TITLES_SAKAI_PROPERTY = "certification.extraUserProperties.titles";
	
	//permission to expose extra properties when viewing the report
	private static final String PERMISSION_VIEW_EXTRA_USER_PREPERTIES = "certificate.extraprops.view";
	
	//should be handled by a permission, or we could reinforce with this as well?
		//private static final String ALLOWED_ACCOUNT_TYPES_SAKAI_PROPERTY = "certification.extraUserProperties.allowedAccountTypesForDisplay";
	
	
	private static final Log logger = LogFactory.getLog(ExtraUserPropertyUtility.class);
	
	private boolean extraUserPropertiesEnabled;
	
	/* Map to hold extra property keys and their corresponding column title. The key is used to retrieve the property
	 * value for each user, and the title is used as the column header when those values are displayed in the certificate's
	 * report*/
	private Map<String, String> extraUserPropertyKeyAndTitleMap;
	
	/*I think this will be all site role permission based*/
	//private List<String> extraUserPropertyAllowedAccountTypesForDisplayList;
	
	private UserDirectoryService userDirectoryService;
	private ToolManager toolManager;
	private SiteService siteService;
	private SecurityService securityService;
	//private Authz authzService;
	
	private static ExtraUserPropertyUtility instance = null;
	
	/**
	 * private constructor for singleton model
	 */
	private ExtraUserPropertyUtility()
	{
		userDirectoryService = (UserDirectoryService) ComponentManager.get(UserDirectoryService.class);
		toolManager = (ToolManager) ComponentManager.get(ToolManager.class);
		siteService = (SiteService) ComponentManager.get(SiteService.class);
		securityService = (SecurityService) ComponentManager.get(SecurityService.class);
		//authzService = (Authz) ComponeentManager.get("org_sakaiproject_tool_gradebook_facades_Authz");
		
		//read sakai.properties
		extraUserPropertiesEnabled = ServerConfigurationService.getBoolean(ENABLE_SAKAI_PROPERTY, false);
		extraUserPropertyKeyAndTitleMap = new HashMap<String, String>();
		//extraUserPropertyAllowedAccountTypesForDisplayList = new ArrayList<String>();
		String[] keys = ServerConfigurationService.getStrings(KEYS_SAKAI_PROPERTY);
		String[] titles = ServerConfigurationService.getStrings(TITLES_SAKAI_PROPERTY);
		if (keys !=null && titles != null && keys.length == titles.length)
		{
			for (int i = 0; i < keys.length; ++i)
			{
				if (keys[i] != null && titles[i] != null)
				{
					extraUserPropertyKeyAndTitleMap.put(keys[i].trim(), titles[i].trim());
				}
			}
		}
	}
	
	
	/**
	 * This class is a singleton. Use this method to acquire the instance
	 * @return the single instance of this class. Will not return null
	 */
	public static synchronized ExtraUserPropertyUtility getInstance()
	{
		if (instance == null)
		{
			instance = new ExtraUserPropertyUtility();
		}
		
		return instance;
	}
	
	/**
	 * Determines if the extra user properties feature has been enabled globally.
	 * This is controlled via sakai.properties
	 * @return
	 */
	public boolean isExtraUserPropertiesEnabled()
	{
		return extraUserPropertiesEnabled;
	}
	
	/**
	 * Returns a map containing the extra properties for the given user.
	 * If a property value is not found for the user, or the user's account type prohibits display,
	 * the NULL_DISPLAY_VALUE will be substituted for that property value
	 * 
	 * @param user the Sakai user to retrieve properties for
	 * @return a map containing the extra properties, or an empty map if something goes wrong. Will not return null
	 */
	public Map<String, String> getExtraPropertiesMapForUser(User user)
	{
		Map<String, String> extraPropMap = new HashMap<String, String>();
		
		if (extraUserPropertiesEnabled && user != null)
		{
			for (String key : extraUserPropertyKeyAndTitleMap.keySet())
			{
				ResourceProperties props = user.getProperties();
				if (props != null)
				{
					String propValue = props.getProperty(key);
					//if (propValue == null || propValue.trim().isEmpty() || !isDisplayAllowedForAccountType(user.getType()))
					if (propValue == null || propValue.trim().isEmpty())
					{
						propValue = NULL_DISPLAY_VALUE;
					}
					extraPropMap.put(key.trim(), propValue.trim());
				}
			}
		}
		return extraPropMap;
	}
	
	/**
	 * Returns  a map containing the extra properties for the given user.
	 * If a property value is not found for the user, or the user's account type prohibits display,
	 * the NULL_DISPLAY_VALUE will be substituted for that property value
	 * 
	 * @param eid the EID of the Sakai user fto retrieve properties for
	 * @return a map containing the extra properties, or an empty map if something goes wrong. Will not return null
	 */
	public Map<String, String> getExtraPropertiesMapForUserByEid(String eid)
	{
		User user = null;
		
		if (extraUserPropertiesEnabled && userDirectoryService != null && eid != null && !eid.trim().isEmpty())
		{
			try
			{
				user = userDirectoryService.getUserByEid(eid);
			}
			catch (UserNotDefinedException unde)
			{
				if (logger.isDebugEnabled())
				{
					logger.debug("Extra User Properties: UserDirectoryService cannot find user with Eid: " + eid
						+ ".\nException was: " + unde.getLocalizedMessage());
				}
			}
		}
		
		return getExtraPropertiesMapForUser(user);
	}
	
	/**
	 * Returns a map containing the extra properties for the given user.
	 * If a property value is not found for the user, or the user's account type prohibits display,
	 * the NULL_DISPLAY_VALUE will be substituted for that property value
	 * 
	 * @param uid the internal UID of the Sakai user to retrieve properties for (this is NOT a username)
	 * @return a map containing the extra properties, or an empty map if something goes wrong. Will not return null.
	 */
	public Map<String, String> getExtraPropertiesMapForUserByUid(String uid)
	{
		User user = null;
		
		if (extraUserPropertiesEnabled && userDirectoryService != null && uid != null && !uid.trim().isEmpty())
		{
			try
			{
				user = userDirectoryService.getUser(uid);
			}
			catch (UserNotDefinedException unde)
			{
				if (logger.isDebugEnabled())
				{
					logger.debug("Extra User Properties: UserDirectoryService cannot find user with Uid: " + uid
							+ ".\nException was: " + unde.getLocalizedMessage());
				}
			}
		}
		
		return getExtraPropertiesMapForUser(user);
	}
	
	/**
	 * Returns a map containing the extra properties for the user specified in the given EnrollmentRecord.
	 * If a property value is not found for the user, or the user's account type prohibits display,
	 * the NULL_DISPLAY_VALUE will be substituted for that property value
	 * 
	 * @param enrollment an EnrollmentRecord for the Sakai user to retrieve properties for
	 * @return a map containing the extra properties, or an empty map if something goes wrong. WIll not return null.
	 */
	/*public Map<String, String> getExtraPropertiesMapFOrUserByEnrollmentRecord(EnrollmentRecord enrollment)
	{
		String uid = "";
		if (enrollment != null)
		{
			org.sakaiproject.section.api.coursemanagement.User user = enrollment.getUser();
			if (user != null)
			{
				uid = user.getUserUid();
			}
		}
		
		return getExtraPropertiesMapForUserByUid(uid);
	}*/
	
	/**
	 * Checks the permission for the current user to determine if they are allowed to view extra user properties
	 * @return true if the user isallowed to view extra user properties
	 */
	public boolean isExtraPropertyViewingAllowedForCurrentUser()
	{
		boolean userCanView = false;
		
		if (extraUserPropertiesEnabled && userDirectoryService != null && toolManager != null && siteService != null && securityService != null)
		{
			User currentUser = userDirectoryService.getCurrentUser();
			if (currentUser != null)
			{
				Placement currentPlacement = toolManager.getCurrentPlacement();
				if (currentPlacement != null)
				{
					String siteId = currentPlacement.getContext();
					if (siteId != null && !siteId.trim().isEmpty())
					{
						String siteRef = siteService.siteReference(siteId);
						if (siteRef != null && !siteRef.trim().isEmpty() && currentUser.getId() != null && !currentUser.getId().trim().isEmpty())
						{
							userCanView = securityService.unlock(currentUser.getId(), PERMISSION_VIEW_EXTRA_USER_PREPERTIES, siteRef);
						}
					}
				}
			}
		}
		
		return userCanView;
	}
	
	/**
	 * Returns a read-only map of property keys to column titles
	 * 
	 * @return an immutable map, possibly empty. Will not return null
	 */
	public Map<String, String> getExtraUserPropertiesKeyAndTitleMap()
	{
		return Collections.unmodifiableMap(extraUserPropertyKeyAndTitleMap);
	}
	
	/**
	 * Conveneience method to return the key and title map as a set of map entries,
	 * which makes it easier to iterate over user JSF tags. Back by an immutable map.
	 * @return a set of map entires, possibly empty
	 */
	public Set<Map.Entry<String, String>> getExtraUserPropertyKeyAndTitleMapAsSet()
	{
		return getExtraUserPropertiesKeyAndTitleMap().entrySet();
	}
	
	/**
	 * Given a column title, returns the preopty key associated with that column
	 * 
	 * @param title the title of the column of interest
	 * @return the property key for the column, or an empty string i fno found or key was null. Will not return null
	 */
	public String getKeyForTitle(String title)
	{
		String key = "";
		if (title != null && !title.trim().isEmpty())
		{
			String trimmedTitle = title.trim();
			for (String k : extraUserPropertyKeyAndTitleMap.keySet())
			{
				String value = extraUserPropertyKeyAndTitleMap.get(k);
				if (trimmedTitle.equals(value))
				{
					if (k != null)
					{
						key = k;
					}
					break;
				}
			}
		}
		
		return key;
	}
	
	/**
	 * Every EnrollmentRecord object has an associated user. This method returns a comparator for EnrollmentRecord objects
	 * based on properties of this user. The specific property to compare on is passed in to the constructor.
	 * 
	 * @param sortKey user property key to compare on
	 * @return a comparator for EnrollmentRecord objects. Comparison based on associated user properties. WIll not return null 
	 */
	/*public ExtraUserPropertyEnrollmentRecordComparator getEnrollmentRecordComparator(String sortKey)
	{
		return new ExtraUserPropertyEnrollmentRecordComparator(sortKey);
	}*/
	
	/**
	 * Every user has a uid. This method returns a comparator for uid strings based not on the string itself, but on other properties of the user.
	 * The specific porperty to compare on is passed in on  the constructor.
	 * 
	 * @param sortKey user property key to compare on
	 * @return a comparator for user uid strings. Comparison based not on string but on user properties. Will not reutnr null
	 */
	public ExtraUserPropertyUidComparator getUidComparator(String sortKey)
	{
		return new ExtraUserPropertyUidComparator(sortKey);
	}
	
	/***************************NESTED CLASSES***************************/
	
	/**
	 * Compares EnrollmentRecord objects by a specific property of the associated user. Used for sorting
	 * extra user property columns in gradebook tables.
	 *
	 */
	/*public class ExtraUserPropertyEnrollmentRecordComparator implements Comparator<EnrollmentRecord>
	{
		private String key; //user property key to base comparison on
		
		public ExtraUserPropertyEnrollmentRecordComparator(String extraUserPropertyKey)
		{
			if (extraUserPropertyKey != null)
			{
				key = extraUserPropertyKey.trim();
			}
			else
			{
				key = "";
			}
		}
		
		public int compare(EnrollmentRecord rec1, EnrollmentRecord rec2)
		{
			ExtraUserPropertyUtility propUtil = ExtraUserPropertyUtility.getInstance();
			String value1 = propUtil.getExtraPropertiesMapForUserByEnrollmentRecord(rec1).get(key);
			if (value1 == null)
			{
				value1 = "";
			}
			String value2 = propUtil.getExtraPropertiesMapForUserByEnrollmentRecord(rec2).get(key);
			if (value2 == null)
			{
				value2 = "";
			}
			return value1.compareTo(value2);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj != null)
			{
				if (obj instanceof ExtraUserPropertyEnrollmentRecordComparator)
				{
					ExtraUserPropertyEnrollmentRecordComparator other = (ExtraUserPropertyEnrollmentRecordComparator) obj;
					if (key.equals(obj))
					{
						return true;
					}
				}
			}
		}
	}*/
	
	
	public class ExtraUserPropertyUidComparator implements Comparator<String>
	{
		private String key; //user property to base comparison on
		
		public ExtraUserPropertyUidComparator(String extraUserPropertyKey)
		{
			if (extraUserPropertyKey != null)
			{
				key = extraUserPropertyKey.trim();
			}
			else
			{
				key = "";
			}
		}
		
		public int compare(String uid1, String uid2)
		{
			ExtraUserPropertyUtility propUtil = ExtraUserPropertyUtility.getInstance();
			String value1 = propUtil.getExtraPropertiesMapForUserByUid(uid1).get(key);
			if (value1 == null)
			{
				value1 = "";
			}
			String value2 = propUtil.getExtraPropertiesMapForUserByUid(uid2).get(key);
			if (value2 == null)
			{
				value2 = "";
			}
			return value1.compareTo(value2);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj != null)
			{
				if (obj instanceof ExtraUserPropertyUidComparator)
				{
					ExtraUserPropertyUidComparator other = (ExtraUserPropertyUidComparator) obj;
					if (key.equals(obj))
					{
						return true;
					}
				}
			}
			
			return false;
		}
	}
	
	/***************************END NESTED CLASSES***************************/
	
}