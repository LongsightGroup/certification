package com.rsmart.certification.impl;

import com.rsmart.certification.api.CertificateAward;
import com.rsmart.certification.api.CertificateDefinition;
import com.rsmart.certification.api.CertificateService;
import com.rsmart.certification.api.criteria.CriteriaTemplate;
import com.rsmart.certification.api.criteria.Criterion;
import com.rsmart.certification.api.VariableResolutionException;
import com.rsmart.certification.criteria.impl.gradebook.DueDatePassedCriteriaTemplate;
import com.rsmart.certification.criteria.impl.gradebook.FinalGradeScoreCriteriaTemplate;
import com.rsmart.certification.criteria.impl.gradebook.GreaterThanScoreCriteriaTemplate;
import com.rsmart.certification.criteria.impl.gradebook.WillExpireCriteriaTemplate;
import com.rsmart.certification.impl.hibernate.criteria.gradebook.GradebookItemCriterionHibernateImpl;
import com.rsmart.certification.impl.hibernate.criteria.gradebook.WillExpireCriterionHibernateImpl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.service.gradebook.shared.GradeDefinition;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;



public class GradebookVariableResolver extends AbstractVariableResolver {
	
	/*private CertificateService
	    certService = null;
	private HashMap<String, CriteriaTemplate>
	    criteriaTemplates = new HashMap<String, CriteriaTemplate>();
	private HashSet<Class<? extends Criterion>>
	    criterionClasses = new HashSet<Class<? extends Criterion>>();
	private GreaterThanScoreCriteriaTemplate
	    gbItemScoreTemplate = null;
	private DueDatePassedCriteriaTemplate
	    gbDueDatePassedTemplate = null;
	private FinalGradeScoreCriteriaTemplate
		gbFinalGradeScoreTemplate = null;
	private WillExpireCriteriaTemplate gbWillExpireTemplate = null;
	private ResourceLoader
	    resourceLoader = null;
	    */
	private static final Log
    	LOG = LogFactory.getLog(GradebookVariableResolver.class);
	
	
	private GradebookService
    	gradebookService = null;
	private UserDirectoryService
    	userDirectoryService = null;
	private ToolManager
    	toolManager = null;
	private SecurityService
	    securityService = null;
	private SessionManager
	    sessionManager = null;
	private String
    	adminUser = null;
	
	public final String 
		CERT_EXPIREDATE					= "cert.expiredate",
		CERT_AWARDDATE                  = "cert.date";
	public GradebookVariableResolver()
	{
		//TODO: Internationalize
		addVariable(CERT_EXPIREDATE, "expiration date");
		addVariable(CERT_AWARDDATE, "date of award");
	}
	
	public String getValue(CertificateAward award, String varLabel)
		throws VariableResolutionException
	{
		if (CERT_EXPIREDATE.equals(varLabel))
		{
			//if this is true, then the criteria has been met.
			//so, this assumes that the user has a grade recorded in the criteria's gradebook item
			
			Set<Criterion> awardCriteria = getAwardCriteriaFromAward(award);
			
			Iterator<Criterion> itAwardCriteria = awardCriteria.iterator();
			while (itAwardCriteria.hasNext())
			{
				Criterion criterion = itAwardCriteria.next();
				if (criterion instanceof WillExpireCriterionHibernateImpl)
				{
					WillExpireCriterionHibernateImpl criterionImpl = (WillExpireCriterionHibernateImpl) criterion;
					Date dateRecorded = getDateRecorded(criterionImpl);
					
					int expiryOffset = Integer.parseInt(criterionImpl.getExpiryOffset());
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(dateRecorded);
					
					cal.add(Calendar.MONTH, expiryOffset);
					
					Date expiryDate = cal.getTime();
					
					DateFormat sdf = SimpleDateFormat.getDateInstance();
					return sdf.format(expiryDate);
				}
			}
			//could not find a WillExpireCriteriaTemplate
			return "";
		}
		else if (CERT_AWARDDATE.equals(varLabel))
		{
			//search for a criteria with a gradebook item
			Set<Criterion> awardCriteria = getAwardCriteriaFromAward(award);
			//WillExpireCriterionHibernateImpl takes precedence
			Iterator<Criterion> itAwardCriteria = awardCriteria.iterator();
			while (itAwardCriteria.hasNext())
			{
				Criterion criterion = itAwardCriteria.next();
				if (criterion instanceof WillExpireCriterionHibernateImpl)
				{
					Date dateRecorded = getDateRecorded((WillExpireCriterionHibernateImpl) criterion);
					
					DateFormat sdf = SimpleDateFormat.getDateInstance();
					return sdf.format(dateRecorded);
				}
			}
			
			//Try other types of GradebookItemCriterionHibernateImpl
			itAwardCriteria = awardCriteria.iterator();
			while (itAwardCriteria.hasNext())
			{
				Criterion criterion = itAwardCriteria.next();
				if (criterion instanceof GradebookItemCriterionHibernateImpl)
				{
					Date dateRecorded = getDateRecorded((GradebookItemCriterionHibernateImpl) criterion);
					
					DateFormat sdf = SimpleDateFormat.getDateInstance();
					return sdf.format(dateRecorded);
				}
			}
			
			//No gradebook related criteria found, so just return the award timestamp
			DateFormat
            dateFormat = SimpleDateFormat.getDateInstance();

			return dateFormat.format(award.getCertificationTimeStamp());
		}
		
        throw new VariableResolutionException("could not resolve variable: \"" + varLabel + "\"");
	}
	
	private Set<Criterion> getAwardCriteriaFromAward(CertificateAward award)
		throws VariableResolutionException
	{
		if (award == null)
		{
			throw new VariableResolutionException("award is null");
		}
		
		CertificateDefinition certDef = award.getCertificateDefinition();
		if (certDef == null)
		{
			throw new VariableResolutionException("certificate definition is null");
		}
		
		Set<Criterion> criteria = certDef.getAwardCriteria();
		if (criteria == null)
		{
			throw new VariableResolutionException("no award criteria");
		}
		return criteria;
	}
	
	private Date getDateRecorded(GradebookItemCriterionHibernateImpl criterionImpl)
		throws VariableResolutionException
	{
		Long gradebookItemId = criterionImpl.getItemId();
		
		GradeDefinition gradeDef = getGradebookService().getGradeDefinitionForStudentForItem(contextId(), gradebookItemId, userId());
		Date dateRecorded = gradeDef.getDateRecorded();
		
		if (dateRecorded == null)
		{
			throw new VariableResolutionException("error retrieving date of grade entry");
		}
		
		return dateRecorded;
	}
	
	
	public GradebookService getGradebookService()
	{
		return gradebookService;
	}
	
	public void setGradebookService(GradebookService gradebookService)
	{
		this.gradebookService = gradebookService;
	}
	
	public UserDirectoryService getUserDirectoryService()
	{
		return userDirectoryService;
	}
	
	public void setUserDirectoryService(UserDirectoryService userDirectoryService)
	{
		this.userDirectoryService = userDirectoryService;
	}
	
	public ToolManager getToolManager()
	{
		return toolManager;
	}
	
	public void setToolManager (ToolManager toolManager)
	{
		this.toolManager = toolManager;
	}
	
	public SessionManager getSessionManager()
	{
		return sessionManager;
	}
	
	public void setSessionManager(SessionManager sessionManager)
	{
		this.sessionManager = sessionManager;
	}
	
	public SecurityService getSecurityService()
	{
		return securityService;
	}
	
	public void setSecurityService(SecurityService securityService)
	{
		this.securityService = securityService;
	}
	
	public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }
    
    protected final String contextId()
    {
        return getToolManager().getCurrentPlacement().getContext();
    }
	
    protected final String userId()
    {
        return getUserDirectoryService().getCurrentUser().getId();
    }
    
	protected Object doSecureGradebookAction(SecureGradebookActionCallback callback)
        throws Exception
    {
        final SessionManager
            sessionManager = getSessionManager();
        final SecurityService
            securityService = getSecurityService();

        final Session
            sakaiSession = sessionManager.getCurrentSession();
        final String
            contextId = contextId(),
            currentUserId = userId(),
            currentUserEid = getUserDirectoryService().getCurrentUser().getEid(),
            adminUser = getAdminUser();

        try
        {
            sakaiSession.setUserId(adminUser);
            sakaiSession.setUserEid(adminUser);

            securityService.pushAdvisor
                (
                    new SecurityAdvisor ()
                    {
                        public SecurityAdvice isAllowed(String userId, String function, String reference)
                        {
                            String
                                compTo = null;

                            if (contextId.startsWith("/site/"))
                            {
                                compTo = contextId;
                            }
                            else
                            {
                                compTo = "/site/" + contextId;
                            }

                            if (reference.equals(compTo) && ("gradebook.viewOwnGrades".equals(function) ||
                                                             "gradebook.editAssignments".equals(function)))
                            {
                                return SecurityAdvice.ALLOWED;
                            }
                            else
                            {
                                return SecurityAdvice.PASS;
                            }
                        }
                    }
                );

            return callback.doSecureAction();
        }
        finally
        {
            securityService.popAdvisor();

            sakaiSession.setUserId(currentUserId);
            sakaiSession.setUserEid(currentUserEid);
        }
    }
}
