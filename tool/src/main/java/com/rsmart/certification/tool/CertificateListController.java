package com.rsmart.certification.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.beans.support.SortDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.rsmart.certification.api.CertificateAward;
import com.rsmart.certification.api.CertificateDefinition;
import com.rsmart.certification.api.CertificateDefinitionStatus;
import com.rsmart.certification.api.CertificateService;
import com.rsmart.certification.api.criteria.CriteriaFactory;
import com.rsmart.certification.api.criteria.UnknownCriterionTypeException;
import com.rsmart.certification.api.DocumentTemplate;
import com.rsmart.certification.api.DocumentTemplateException;
import com.rsmart.certification.api.DocumentTemplateService;
import com.rsmart.certification.api.TemplateReadException;
import com.rsmart.certification.api.UnmetCriteriaException;
import com.rsmart.certification.api.VariableResolutionException;
import com.rsmart.certification.api.criteria.Criterion;
import com.rsmart.certification.impl.hibernate.criteria.gradebook.DueDatePassedCriterionHibernateImpl;
import com.rsmart.certification.impl.hibernate.criteria.gradebook.FinalGradeScoreCriterionHibernateImpl;
import com.rsmart.certification.impl.hibernate.criteria.gradebook.GradebookItemCriterionHibernateImpl;
import com.rsmart.certification.impl.hibernate.criteria.gradebook.GreaterThanScoreCriterionHibernateImpl;
import com.rsmart.certification.impl.hibernate.criteria.gradebook.WillExpireCriterionHibernateImpl;
import com.rsmart.certification.tool.utils.ExtraUserPropertyUtility;

/**
 * @author bbailla2
 * 
 * User: duffy
 * Date: Jun 7, 2011
 * Time: 4:15:18 PM
 */
@Controller
public class CertificateListController
    extends BaseCertificateController
{
	public static final String THIS_PAGE = "list.form";
	
	//Pagination request params
	public static final String PAGINATION_NEXT = "next";
	public static final String PAGINATION_LAST = "last";
	public static final String PAGINATION_PREV = "previous";
	public static final String PAGINATION_FIRST = "first";
	public static final String PAGINATION_PAGE = "page";
	public static final String PAGE_SIZE = "pageSize";
	public static final String PAGE_NO = "pageNo";
	public static final List<Integer> PAGE_SIZE_LIST = Arrays.asList(10,25,50,100,200,Integer.MAX_VALUE);
	
	//Other request params
	public static final String PARAM_CERT_ID = "certId";
	
	private final String MAIL_SUPPORT_SAKAI_PROPERTY =  "mail.support";
	private final String MAIL_SUPPORT = ServerConfigurationService.getString(MAIL_SUPPORT_SAKAI_PROPERTY);
	
	private final String ADMIN_VIEW = "certviewAdmin";
	private final String PARTICIPANT_VIEW = "certviewParticipant";
	private final String UNAUTHORIZED_VIEW = "certviewUnauthorized";
	
	private final String CERTIFICATE_NAME_PROPERTY = "name";
	
	//Keys for http session attributes
	private final String SESSION_LIST_ATTRIBUTE = "certList";
	private final String SESSION_REQUIREMENTS_ATTRIBUTE = "requirements";
	private final String SESSION_EXPIRY_OFFSET_ATTRIBUTE = "expiryOffset";
	private final String SESSION_REPORT_PROP_HEADERS_ATTRIBUTE = "reportPropHeaders";
	private final String SESSION_REPORT_CRIT_HEADERS_ATTRIBUTE = "reportCritHeaders";
	private final String SESSION_REPORT_LIST_ATTRIBUTE = "reportList";
	private final String SESSION_REQUIREMENT_LIST_ATTRIBUTE = "certRequirementList";
	private final String SESSION_IS_AWARDED_ATTRIBUTE = "certIsAwarded";
	
	//Keys for mav models
	private final String MODEL_KEY_CERTIFICATE_LIST = "certList";
	private final String MODEL_KEY_PAGE_SIZE_LIST = "pageSizeList";
	private final String MODEL_KEY_PAGE_NO = "pageNo";
	private final String MODEL_KEY_PAGE_SIZE = "pageSize";
    private final String MODEL_KEY_FIRST_ELEMENT = "firstElement";
    private final String MODEL_KEY_LAST_ELEMENT = "lastElement";
    private final String MODEL_KEY_CERTIFICATE = "cert";
    private final String MODEL_KEY_REQUIREMENT_LIST_ATTRIBUTE = "certRequirementList";
    private final String MODEL_KEY_IS_AWARDED_ATTRIBUTE = "certIsAwarded";
    private final String MODEL_KEY_ERROR_ARGUMENTS_ATTRIBUTE = "errorArgs";
    private final String MODEL_KEY_ERRORS_ATTRIBUTE = "errors";
    private final String MODEL_KEY_REQUIREMENTS_ATTRIBUTE = "requirements";
    private final String MODEL_KEY_EXPIRY_OFFSET_ATTRIBUTE = "expiryOffset";
    private final String MODEL_KEY_USER_PROP_HEADERS_ATTRIBUTE = "userPropHeaders";
    private final String MODEL_KEY_CRIT_HEADERS_ATTRIBUTE = "critHeaders";
    private final String MODEL_KEY_REPORT_LIST_ATTRIBUTE = "reportList";
    
    //UI Message keys
    private final String MESSAGE_ERROR_NOT_ADMIN = "error.not.admin";
    private final String MESSAGE_ERROR_NO_SELECTION = "error.no.selection";
    private final String MESSAGE_ERROR_BAD_ID = "error.bad.id";
    private final String MESSAGE_TEMPLATE_PROCESSING_ERROR = "form.error.templateProcessingError";
    private final String MESSAGE_FORM_PRINT_ERROR = "form.print.error";
    
    //DATE FORMATS
    private final String PDF_FILE_NAME_DATE_FORMAT = "yyyy_MM_dd";
    private final String CSV_FILE_NAME_FORMAT = "yyyy-MM-dd";
    
    //MIME TYPES
    private final static String PDF_MIME_TYPE = "application/octet-stream";
    private final static String CSV_MIME_TYPE = "text/csv";
    
    private final String LEVEL_WARN = "warn";
    
    private final String REDIRECT = "redirect:";
	
	private String getAbsoluteUrlForRedirect(String redirectTo)
	{
        String placementId = getToolManager().getCurrentPlacement().getId();
        String portalurl = ServerConfigurationService.getPortalUrl();
        String redirectPrefix = portalurl + "/tool/" + placementId;
        String redirectString = REDIRECT + redirectPrefix + "/" + redirectTo;
        return redirectString;
	}
	
	@RequestMapping("/" + THIS_PAGE)
	public ModelAndView certListHandler(@RequestParam(value=PAGINATION_PAGE, required=false) String page,
			@RequestParam(value=PAGE_SIZE, required=false) Integer pageSize,
			@RequestParam(value=PAGE_NO, required=false) Integer pageNo, HttpServletRequest request) throws Exception
    {
		if(isAdministrator())
		{
			return certAdminListHandler(page, pageSize, pageNo, request);
		}
		else if (isAwardable())
		{
			return certParticipantListHandler(page, pageSize, pageNo, request);
		}
		else
		{
			return certUnauthorizedListHandler(page, pageSize, pageNo, request);
		}
	}

    public ModelAndView certAdminListHandler(String page, Integer pageSize, Integer pageNo, HttpServletRequest request) throws Exception
    {
    	ModelAndView
            mav = new ModelAndView(ADMIN_VIEW);

    	Map<String, Object>
            model = new HashMap<String, Object>();
    	
    	List<CertificateDefinition>
            certDefList = new ArrayList<CertificateDefinition>();

        HttpSession
            session = request.getSession();

        PagedListHolder
            certList = null;

    	if(page==null)
		{
    		String
                siteId = siteId();

			certDefList.addAll(getCertificateService().getCertificateDefinitionsForSite(siteId));

            certList = new PagedListHolder(certDefList);
	    	if(pageSize != null)
	    	{
	    		certList.setPageSize(pageSize);
	    	}
	    	else
	    	{
	    		pageSize = PAGE_SIZE_LIST.get(3);
	    		certList.setPageSize(pageSize);
	    	}
	    	if(pageNo != null)
	    	{
	    		certList.setPage(pageNo);
	    	}
            certList.setSort(
                new SortDefinition()
                {
                    public String getProperty() {
                        return CERTIFICATE_NAME_PROPERTY;
                    }

                    public boolean isIgnoreCase() {
                        return true;
                    }

                    public boolean isAscending() {
                        return true;
                    }
                }
            );

            certList.resort();                
		}
    	else
    	{
    		certList = (PagedListHolder) session.getAttribute(SESSION_LIST_ATTRIBUTE);

    		if(PAGINATION_NEXT.equals(page)  && !certList.isLastPage())
    		{
    			certList.nextPage();
    		}
    		else if(PAGINATION_LAST.equals(page))
    		{
    			certList.setPage(certList.getLastLinkedPage());
    		}
    		else if(PAGINATION_PREV.equals(page) && !certList.isFirstPage())
    		{
    			certList.previousPage();
    		}
    		else if(PAGINATION_FIRST.equals(page))
    		{
    			certList.setPage(certList.getFirstLinkedPage());
    		}
    	}

        session.setAttribute(SESSION_LIST_ATTRIBUTE, certList);
        model.put(MODEL_KEY_CERTIFICATE_LIST, certList);
        model.put(MODEL_KEY_PAGE_SIZE_LIST, PAGE_SIZE_LIST);
        model.put(MODEL_KEY_PAGE_NO, certList.getPage());
        model.put(MODEL_KEY_PAGE_SIZE, pageSize);
        model.put(MODEL_KEY_FIRST_ELEMENT, (certList.getFirstElementOnPage()+1));
        model.put(MODEL_KEY_LAST_ELEMENT, (certList.getLastElementOnPage()+1));
    	mav.addAllObjects(model);
    	return mav;
    }
    
    public ModelAndView certParticipantListHandler(String page, Integer pageSize, Integer pageNo, HttpServletRequest request) throws Exception
    {
        final CertificateService cs = getCertificateService();
    	ModelAndView mav = new ModelAndView(PARTICIPANT_VIEW);
		Map<String, Object> model = new HashMap<String, Object>();
    	
        Set<CertificateDefinition> certDefs = null;
    	
    	Map<String, List<Map.Entry<String, String>>> certRequirementList = new HashMap<String, List<Map.Entry<String, String>>>();
    	
    	Map<String, Boolean> certificateIsAwarded = new HashMap<String, Boolean>();
    	
        HttpSession session = request.getSession();
        PagedListHolder certList = null;
             
        // If this is the first time we're going to the page, or changing the paging size
    	if(page==null)
		{
            certDefs = cs.getCertificateDefinitionsForSite
                        (siteId(),
                         new CertificateDefinitionStatus[]
                         {
                            CertificateDefinitionStatus.ACTIVE
                         });

            List<String> certDefIds = new ArrayList<String>();

            for(CertificateDefinition cfl : certDefs)
            {
                certDefIds.add(cfl.getId());
                List<Map.Entry<String, String>> requirementList = new ArrayList<Map.Entry<String, String>>();
                try
                {	
                	requirementList = cs.getCertificateRequirementsForUser(cfl.getId(), userId(), siteId());
                }
                catch (IdUnusedException e)
                {
                	logger.warn("While getting certificate requirements, found unused certificate id: " + cfl.getId());
                }
                certRequirementList.put (cfl.getId(), requirementList);
            }
            
            for (CertificateDefinition cd : certDefs)
            {
                boolean awarded = false;
                if (isAwardable() && cd.isAwarded(userId()))
                {
                	awarded = true;
                }
                
                certificateIsAwarded.put(cd.getId(), new Boolean(awarded));
            }
            
            
            
	    	certList = new PagedListHolder();
	    	if(pageSize != null)
	    	{
	    		certList.setPageSize(pageSize);
	    	}
	    	else
	    	{
	    		pageSize = PAGE_SIZE_LIST.get(3);
	    		certList.setPageSize(pageSize);
	    	}
	    	if(pageNo != null)
	    	{
	    		certList.setPage(pageNo);
	    	}
            certList.setSource(Arrays.asList(certDefs.toArray()));

            certList.setSort(
                new SortDefinition()
                {
                    public String getProperty() {
                        return CERTIFICATE_NAME_PROPERTY;
                    }

                    public boolean isIgnoreCase() {
                        return true;
                    }

                    public boolean isAscending() {
                        return true;
                    }
                }
            );

            certList.resort();
		}
    	
    	// If they're changing pages
		else
		{
			certList = (PagedListHolder) session.getAttribute(SESSION_LIST_ATTRIBUTE);
			certRequirementList = (Map) session.getAttribute(SESSION_REQUIREMENT_LIST_ATTRIBUTE);
			certificateIsAwarded = (Map) session.getAttribute(SESSION_IS_AWARDED_ATTRIBUTE);

    		if(PAGINATION_NEXT.equals(page)  && !certList.isLastPage())
    		{
    			certList.nextPage();
    		}
    		else if(PAGINATION_LAST.equals(page))
    		{
    			certList.setPage(certList.getLastLinkedPage());
    		}
    		else if(PAGINATION_PREV.equals(page) && !certList.isFirstPage())
    		{
    			certList.previousPage();
    		}
    		else if(PAGINATION_FIRST.equals(page))
    		{
    			certList.setPage(certList.getFirstLinkedPage());
    		}
		}

    	session.setAttribute (SESSION_LIST_ATTRIBUTE, certList);
        session.setAttribute (SESSION_REQUIREMENT_LIST_ATTRIBUTE, certRequirementList);
        session.setAttribute (SESSION_IS_AWARDED_ATTRIBUTE, certificateIsAwarded);
        model.put(MODEL_KEY_CERTIFICATE_LIST, certList);
        model.put(MODEL_KEY_REQUIREMENT_LIST_ATTRIBUTE, certRequirementList);
        model.put(MODEL_KEY_IS_AWARDED_ATTRIBUTE, certificateIsAwarded);
        model.put(MODEL_KEY_PAGE_SIZE_LIST, PAGE_SIZE_LIST);
        model.put(MODEL_KEY_PAGE_NO, certList.getPage());
        model.put(MODEL_KEY_FIRST_ELEMENT, (certList.getFirstElementOnPage()+1));
        model.put(MODEL_KEY_LAST_ELEMENT, (certList.getLastElementOnPage()+1));

		mav.addAllObjects(model);
		return mav;
    }
    
    public ModelAndView certUnauthorizedListHandler(String page, Integer pageSize, Integer pageNo, HttpServletRequest request) throws Exception
    {
    	ModelAndView mav = new ModelAndView(UNAUTHORIZED_VIEW);
    	return mav;
    }

    @RequestMapping("/delete.form")
    public ModelAndView deleteCertificateHandler(@RequestParam(PARAM_CERT_ID) String certId,
                    HttpServletRequest request,
                    HttpServletResponse response)
    {

        HashMap<String, String>
            model = new HashMap<String, String>();

        if (!isAdministrator())
        {
            model.put(ERROR_MESSAGE, MESSAGE_ERROR_NOT_ADMIN);
        }

        if (certId == null || certId.trim().length() == 0)
        {
            model.put(ERROR_MESSAGE, MESSAGE_ERROR_NO_SELECTION);
        }

        try
        {
            getCertificateService().deleteCertificateDefinition(certId);
        }
        catch (IdUnusedException e)
        {
            model.put(ERROR_MESSAGE, MESSAGE_ERROR_BAD_ID);
        }
        catch (DocumentTemplateException dte)
        {
        	model.put(ERROR_MESSAGE, MESSAGE_TEMPLATE_PROCESSING_ERROR);
        }

        if (model.size () > 0)
        {
            return new ModelAndView (REDIRECT + THIS_PAGE, model);
        }

        return new ModelAndView (REDIRECT + THIS_PAGE);
    }

    @RequestMapping("/print.form")
    public ModelAndView printCertificateHandler(@RequestParam(PARAM_CERT_ID) String certId,
                                        HttpServletRequest request,
    		                            HttpServletResponse response)
    {
    	ModelAndView mav = null;
    	
    	OutputStream out = null;
    	
    	//true if there's trouble creating the certificate 
    	boolean creationError = false;
    	
        CertificateService certService = getCertificateService();
        CertificateDefinition definition = null;

        try
        {
            definition = certService.getCertificateDefinition(certId);
        }
        catch (IdUnusedException iue)
        {
        	try
        	{
        		mav = certParticipantListHandler(null, null, null, request);
        		//this gets mav's actual model (not a clone)
	        	Map model = mav.getModel();
	        	//add the error to mav's model
	        	model.put(ERROR_MESSAGE, MESSAGE_ERROR_BAD_ID);
	        	return mav;
        	}
        	catch (Exception e)
        	{
        		//Guess there's nothing we can do
        		logger.error(userId() + " has attempted to download certificate for non existant certificate: " + certId+ ", failed to provide feedback");
        		return null;
        	}
        }
        

        Date issueDate = definition.getIssueDate(userId());
        
        //they've been awarded iff issueDate != null and they're awardable
        if (issueDate != null && isAwardable())
        {
        
	        DocumentTemplate template = definition.getDocumentTemplate();
	        DocumentTemplateService dts = getDocumentTemplateService();
	
	        try
	        {
	            //get an input stream for the PDF
	            InputStream in = dts.render(template, definition, userId());
	            
	            //Creating the pdf was a success
	            //proceed to create the http response
	            
	            //Make the filename
	            StringBuilder fNameBuff = new StringBuilder();
	            SimpleDateFormat sdf = new SimpleDateFormat(PDF_FILE_NAME_DATE_FORMAT);
	            String
	                certName = definition.getName(),
	                templName = template.getName(),
	                extension = "";
	            int dotIndex = -1;
	
	            if (templName != null && (dotIndex = templName.lastIndexOf('.')) > -1)
	            {
	                extension = templName.substring(dotIndex);
	            }
	
	            certName = certName.replaceAll("[^a-zA-Z0-9]+","-");
	
	            String strIssueDate = sdf.format(issueDate);
	            
	            fNameBuff.append (certName);
	            fNameBuff.append('_').append(strIssueDate);
	            fNameBuff.append(extension);
	            
	            //Configure the http headers
	            response.setContentType(PDF_MIME_TYPE);
	            response.addHeader("Content-Disposition", "attachement; filename = " + fNameBuff.toString());
	            response.setHeader("Cache-Control", "");
	            response.setHeader("Pragma", "");
	            
	            //put the pdf into the payload
	            byte buff[] = new byte[2048];
	            int numread = 0;
	
	            out = response.getOutputStream();
	            
	            while ((numread = in.read(buff)) != -1)
	            {
	                out.write(buff, 0, numread);
	            }
	            
	            out.flush();
	            out.close();
	        }
	        
	        catch (TemplateReadException e)
	        {
	            creationError = true;
	        }
	        catch (VariableResolutionException e)
	        {
	        	creationError = true;
	        }
	        catch (IOException e)
	        {
	        	creationError = true;
	        }
        }
        
        if (creationError)
        {
        	try
        	{
        		mav = certParticipantListHandler(null, null, null, request);
        		//this gets mav's actual model (not a clone)
	        	Map model = mav.getModel();
	        	//add these entries to mav's model
	        	model.put(ERROR_MESSAGE, MESSAGE_FORM_PRINT_ERROR);
	        	model.put(MODEL_KEY_ERROR_ARGUMENTS_ATTRIBUTE, MAIL_SUPPORT);
        	}
        	catch (Exception e)
        	{
        		//An exception while handling previous errors
        		//Guess there's nothing we can do
        		logger.error("Couldn't create the pdf for " + userId() + ", certId is " + certId + ", failed to provide feedback");
        		return null;
        	}
        }
        
        return mav;
    }
    
    
    /**
     * This method handles the report. This includes landing on the report view, handling the paging navigators, 
     * and exporting the csv. However, returning to the certificates list is handled in jsp
     * 
     * @author bbailla2
     * 
     * @param certId the certificate on which is being reported
     * @param page the destination (next, previous, first, last)
     * @param pageSize the page size (for the paging navigator)
     * @param pageNo the destination (specified number)
     * @param export true if exporting a csv
     * @param request http request
     * @param response http response
     * @return the ModelAndView object for jsp
     * @throws Exception
     */
    @RequestMapping("/reportView.form")
    public ModelAndView certAdminReportHandler(@RequestParam(PARAM_CERT_ID) String certId, @RequestParam(value=PAGINATION_PAGE, required=false) String page,
		@RequestParam(value=PAGE_SIZE, required=false) Integer pageSize,
		@RequestParam(value=PAGE_NO, required=false) Integer pageNo,
		@RequestParam(value="export", required=false) Boolean export,
		HttpServletRequest request,
		HttpServletResponse response) throws Exception
	{
    	if (!isAdministrator())
    	{
    		//only people who have permission to add/edit certificates can see this report
    		return null;
    	}
    	
    	//The model that will be sent to the UI
    	Map<String, Object> model = new HashMap<String, Object>();
    	
    	//Any errors that need to be sent to the UI
    	List<String> errors = new ArrayList<String>();
    	
    	//Will be used to 'cache' some data to speed up the paging navigator
    	HttpSession session = request.getSession();
    	
    	/*The Report table's headers for columns that are related to the certificate definition's criteria 
    	 * (other headers are already handled in jsp)*/
    	List<Object> criteriaHeaders = new ArrayList<Object>();
    	
    	//holds the contents of the table, the page number, the page size, etc.
    	PagedListHolder reportList = null;
    	
    	
    	//Pass the certificate definition to the UI (so it can print its name and use its id as necessary) 
		CertificateService certService = getCertificateService();
	    CertificateDefinition definition = null;
	    try
	    {
	        definition = certService.getCertificateDefinition(certId);
	        if (logIfNull(definition, "cannot retrieve certificate definition for certId = " + certId))
	        		return null;
	    }
	    catch (IdUnusedException e)
	    {
	        //they sent an invalid certId in their http GET;
	    	/*possible causes: they clicked on View Report after another user deleted the certificate definition,
	    	or they attempted to do evil with a random http GET.
	    	We don't care, show them nothing*/
	    	return null;
	    }
	    model.put(MODEL_KEY_CERTIFICATE, definition);
    	
	    //for internationalization - loads Messages.properties
	    ResourceLoader messages = getMessages();
	    
	    //we'll need this to get additional user properties
    	ExtraUserPropertyUtility extraPropsUtil = ExtraUserPropertyUtility.getInstance();
    	//determines if the current user has permission to view extra properties
    	boolean canShowUserProps = extraPropsUtil.isExtraUserPropertiesEnabled() && extraPropsUtil.isExtraPropertyViewingAllowedForCurrentUser();
    	
	    List<String> propHeaders = new ArrayList<String>();
    	
	    List<String> requirements = new ArrayList<String>();
	    
	    Integer expiryOffset = null;
    	
    	if(page==null && export==null)
		{
    		//It's their first time hitting the page or they changed the page size 
    		// -we'll load/refresh all the data
    		
    		//get the requirements for the current user
    		Iterator<Criterion> itCriterion = definition.getAwardCriteria().iterator();
    		while (itCriterion.hasNext())
    		{
    			Criterion crit = itCriterion.next();
    			if ( !(crit instanceof WillExpireCriterionHibernateImpl) )
    			{
    				//we only care about criteria that affect whether the certificate is awarded
    				//WillExpireCriteironHibernateImpl has no effect on whether it is awarded
    				requirements.add(crit.getExpression());
    			}
    		}
    		
    		
    		//Get the headers for the additional user properties
        	//keeps track of the order of the keys so that we know that the headers and the cells line up
        	List<String> propKeys = new ArrayList<String> ();
        	//contains the headers that we'll push to jsp
        	if (canShowUserProps)
        	{
        		Map<String, String> propKeysTitles = extraPropsUtil.getExtraUserPropertiesKeyAndTitleMap();
        		propKeys = new ArrayList<String>(propKeysTitles.keySet());
        		//perhaps valueSet() does the same thing, but I'm being cautious about the order
    			Iterator<String> itPropKeys = propKeys.iterator();
    			while (itPropKeys.hasNext())
    			{
    				String key = itPropKeys.next();
    				propHeaders.add(propKeysTitles.get(key));
    			}
        	}
        	
        	
	    	
	    	//Use orderedCriteria to keep track of the order of the headers so that we can populate the table accordingly
	    	ArrayList<Criterion> orderedCriteria = new ArrayList<Criterion>();
	    
	    	//truncates decimals if it gets a whole number; shows decimals otherwise
	    	NumberFormat numberFormat = NumberFormat.getNumberInstance();
	    	
	    	//iterate through the certificate definition's criteria, and grab headers for the criteria columns accordingly
	    	itCriterion = definition.getAwardCriteria().iterator();
	    	while (itCriterion.hasNext())
	    	{
	    		Criterion crit = itCriterion.next();
	    		if (logIfNull(crit, "definition contained null criterion. certId: " + certId))
	    			return null;
	    		
	    		if (crit instanceof DueDatePassedCriterionHibernateImpl)
	    		{
	    			DueDatePassedCriterionHibernateImpl ddpCrit = (DueDatePassedCriterionHibernateImpl) crit;	    			
	    			//says 'Due date for <itemName>'
	    			criteriaHeaders.add(messages.getFormattedMessage("report.table.header.duedate", new String[]{ddpCrit.getItemName()}));
	    		}
	    		else if (crit instanceof FinalGradeScoreCriterionHibernateImpl)
	    		{
	    			FinalGradeScoreCriterionHibernateImpl fgsCrit = (FinalGradeScoreCriterionHibernateImpl) crit;
	    			//says 'Final Course Grade'
	    			criteriaHeaders.add(messages.getString("report.table.header.fcg"));
	    		}
	    		else if (crit instanceof GreaterThanScoreCriterionHibernateImpl)
	    		{
	    			GreaterThanScoreCriterionHibernateImpl gtsCrit = (GreaterThanScoreCriterionHibernateImpl) crit;
	    			//says '<itemName>'
	    			criteriaHeaders.add(gtsCrit.getItemName());
	    		}
	    		else if (crit instanceof WillExpireCriterionHibernateImpl)
	    		{
	    			WillExpireCriterionHibernateImpl wechi = (WillExpireCriterionHibernateImpl) crit;
	    			//says 'Expires'
	    			criteriaHeaders.add(0, messages.getString("report.table.header.expire"));
	    			String strExpiryOffset = wechi.getExpiryOffset();
	    			if (logIfNull(strExpiryOffset, "no expiry offset found for criterion: "+ wechi.getId()))
	    				return null;
    				expiryOffset = new Integer(strExpiryOffset);
	    		}
	    		else if (crit instanceof GradebookItemCriterionHibernateImpl)
	    		{
	    			//I believe this is only used as a parent class and this code will never be reached
	    			logger.warn("certAdminReportHandler failed to find a child criterion for a GradebookItemCriterion");
	    			//GradebookItemCriterionHibernateImpl giCrit = (GradebookItemCriterionHibernateImpl) crit;
	    			//criteriaHeaders.add(giCrit.getItemName());
	    			return null;
	    		}
	    		
	    		
	    		//Expiration date should immediately follow issue date
	    		if (crit instanceof WillExpireCriterionHibernateImpl)
	    		{
	    			//0th position immediately follows the issue date
	    			orderedCriteria.add(0, crit);
	    		}
	    		else
	    		{
	    			//all other criteria go at the back
	    			orderedCriteria.add(crit);
	    		}
	    		
	    	}
	    	
	    	
	    	
	    	
	    	
	    	//Prepare the Report table's contents
	    	List<ReportRow> reportRows = new ArrayList<ReportRow>();
	    	
	    	/* Iterate through the list of users who have the ability to be awarded certificates,
	    	 * populate each row of the table accordingly*/
	    	Set<String> userIds = getHistoricalGradedUserIds();
	    	userIds.addAll(getAwardableUserIds());
	    	//List<String> userIds = getAwardableGradedUserIds();
	    	Iterator<String> itUser = userIds.iterator();
	    	while (itUser.hasNext())
	    	{
	    		String userId = itUser.next();
	    		try
	    		{
	    			//get their user object
	    			User currentUser = getUserDirectoryService().getUser(userId);
	    			
	    			//The user exists, so create their row
	    			ReportRow currentRow = new ReportRow();
	    			
	    			//set the name
	    			String firstName = currentUser.getFirstName();
	    			String lastName = currentUser.getLastName();
	    			//do it in an appropriate format
	    			setNameFieldForReportRow(currentRow, firstName, lastName);
	    			
	    			currentRow.setUserId(currentUser.getEid());
	    			
	    			currentRow.setRole(getRole(userId));
	    			
	    			ArrayList<String> extraProps = new ArrayList<String>();
	    			if (canShowUserProps)
	    			{
	    				Map<String, String> extraPropsMap = extraPropsUtil.getExtraPropertiesMapForUser(currentUser);
	    				Iterator<String> itKeys = propKeys.iterator();
	    				while (itKeys.hasNext())
	    				{
	    					String key = itKeys.next();
	    					extraProps.add(extraPropsMap.get(key));
	    				}
	    			}
	    			currentRow.setExtraProps(extraProps);
	    			
	    			//Get the issue date
	    			Date issueDate = definition.getIssueDate(userId);
	    			if (issueDate == null)
	    			{
	    				//certificate was not awarded to this user
	    				currentRow.setIssueDate(null);
	    			}
	    			else
	    			{
	    				//format the date
						String formatted = dateFormat.format(issueDate);
						currentRow.setIssueDate(formatted);
	    			}
	    			
	    			//Now populate the criterionCells by iterating through the criteria (in the order that they appear)
	    			List<String> criterionCells = new ArrayList<String>();
	    			Iterator<Criterion> itCriteria = orderedCriteria.iterator();
	    			while (itCriteria.hasNext())
	    			{
	    				Criterion crit = itCriteria.next();
	    				if (logIfNull(crit, "null criterion in orderedCriteria for certId: " + certId))
	    					return null;
	    	    		
	    				// OWLTODO: refactor this entire block; use over-ridden method to provide cell(s) instead of instanceof checks
	    	    		if (crit instanceof DueDatePassedCriterionHibernateImpl)
	    	    		{
	    	    			DueDatePassedCriterionHibernateImpl ddpCrit = (DueDatePassedCriterionHibernateImpl) crit;
	    	    			Date dueDate = ddpCrit.getDueDate();
	    	    			
	    	    			if (logIfNull(dueDate, "DueDatePassed Criterion without a due date" + crit.getId(), LEVEL_WARN))
	    	    				//place holder
	    	    				criterionCells.add(null);
	    	    			else
	    	    			{
	    	    				//add the formatted date to the criterion cells
	    	    				String formatted = dateFormat.format(dueDate);
		    	    			criterionCells.add(formatted);
	    	    			}
	    	    		}
	    	    		else if (crit instanceof FinalGradeScoreCriterionHibernateImpl)
	    	    		{
	    	    			CriteriaFactory critFact = crit.getCriteriaFactory();
	    	    			if (logIfNull (critFact, "criterion without a factory. crit: " + crit.getId()))
	    	    				return null;
	    	    			
	    	    			Double score = critFact.getFinalScore(userId, siteId());
	    	    			if (score==null)
	    	    			{
	    	    				String incomplete = messages.getString("report.table.incomplete");
	    	    				criterionCells.add(incomplete);
	    	    			}
	    	    			else
	    	    			{
	    	    				String formatted = numberFormat.format(score);
	    	    				criterionCells.add(formatted);
	    	    			}
	    	    		}
	    	    		else if (crit instanceof GreaterThanScoreCriterionHibernateImpl)
	    	    		{
	    	    			GreaterThanScoreCriterionHibernateImpl gtsCrit = (GreaterThanScoreCriterionHibernateImpl) crit;
	    	    			CriteriaFactory critFact = gtsCrit.getCriteriaFactory();
	    	    			if (logIfNull (critFact, "criterion without a factory. crit: " + gtsCrit.getId()))
	    	    				return null;
	    	    			
	    	    			Double score = critFact.getScore(gtsCrit.getItemId(), userId, siteId());
	    	    			if (score == null)
	    	    			{
	    	    				String incomplete = messages.getString("report.table.incomplete");
	    	    				criterionCells.add(incomplete);
	    	    			}
	    	    			else
	    	    			{
	    	    				String formatted = numberFormat.format(score);
	    	    				criterionCells.add(formatted);
	    	    			}
	    	    		}
	    	    		else if (crit instanceof WillExpireCriterionHibernateImpl)
	    	    		{
	    	    			if (issueDate == null)
	    	    			{
	    	    				//user didn't achieve the certificate, so expiration can't be calculated
	    	    				
	    	    				//place holder
	    	    				criterionCells.add(null);
	    	    			}
	    	    			else
	    	    			{
	    	    				//we already have the expiration date
	    	    				/*WillExpireCriterionHibernateImpl weCrit = (WillExpireCriterionHibernateImpl) crit;
	    	    				get the expiry offset and add it to the issue date
	    	    				String strExpiryOffset = weCrit.getExpiryOffset();
	    	    				if (logIfNull(strExpiryOffset, "no expiry offset found for criterion: "+ weCrit.getId()))
	    	    					return null;
	    	    				Integer expiryOffset = new Integer(strExpiryOffset);*/
	    	    				Calendar cal = Calendar.getInstance();
	    	    				cal.setTime(issueDate);
	    	    				cal.add(Calendar.MONTH, expiryOffset);
	    	    				Date expiryDate = cal.getTime();
	    	    				String formatted = dateFormat.format(expiryDate);
	    	    				criterionCells.add(formatted);
	    	    			}
	    	    		}
	    	    		else if (crit instanceof GradebookItemCriterionHibernateImpl)
	    	    		{
	    	    			//I believe this is only used as a parent class and this code will never be reached
	    	    			logger.warn("certAdminReportHandler failed to find a child criterion for a GradebookItemCriterion");
	    	    			//place holder
	    	    			//criterionCells.add(null);
	    	    			return null;
	    	    		}
	    				
	    			}
	    			currentRow.setCriterionCells(criterionCells);
	    			
	    			//show whether the certificate was awarded
	    			//certificate is awarded iff the issue date is null
	    			if (issueDate == null)
	    			{
	    				String no = messages.getString("report.table.no");
	    				currentRow.setAwarded(no);
	    			}
	    			else
	    			{
	    				String yes = messages.getString("report.table.yes");
	    				currentRow.setAwarded(yes);
	    			}
	    			
	    			
	    			reportRows.add(currentRow);
	    		}
	    		catch (UserNotDefinedException e)
	    		{
	    			//user's not in the system anymore. Ignore
	    		}
	    	}
	    	
	    	
	    	//set up the paging navigator
	    	//the 'if' surrounding this scope: page == null && export == null
	    	//this happens when freshly arriving on this page or when changing the page size
	    	reportList = new PagedListHolder(reportRows);
    	
	    	if(pageSize != null)
	    	{
	    		//they changed the page size
	    		reportList.setPageSize(pageSize);
	    	}
	    	else
	    	{
	    		//fresh arival, set the default page size
	    		//set default to 100
	    		pageSize = PAGE_SIZE_LIST.get(3);
	    		reportList.setPageSize(pageSize);
	    	}
	    	if(pageNo != null)
	    	{
	    		reportList.setPage(pageNo);
	    	}
        	reportList.setSort(
                new SortDefinition()
                {
                    public String getProperty() {
                    	//sort by the getName() method
                        return CERTIFICATE_NAME_PROPERTY;
                    }

                    public boolean isIgnoreCase() {
                        return true;
                    }

                    public boolean isAscending() {
                        return true;
                    }
                }
            );

            reportList.resort();
		} 	// page==null && export==null
    	else if (export == null)
    	{
    		// !(page == null && export == null) && export == null -> page != null
    		// page != null -> they clicked a navigation button
    		
    		//pull the headers and the report list from the http session
    		requirements = (List<String>) session.getAttribute(SESSION_REQUIREMENTS_ATTRIBUTE);
    		expiryOffset = (Integer) session.getAttribute(SESSION_EXPIRY_OFFSET_ATTRIBUTE);
    		propHeaders = (List<String>) session.getAttribute(SESSION_REPORT_PROP_HEADERS_ATTRIBUTE);
    		criteriaHeaders = (List<Object>) session.getAttribute(SESSION_REPORT_CRIT_HEADERS_ATTRIBUTE);
    		reportList = (PagedListHolder) session.getAttribute(SESSION_REPORT_LIST_ATTRIBUTE);
    		
    		//navigate appropriately
    		if(PAGINATION_NEXT.equals(page)  && !reportList.isLastPage())
    		{
    			reportList.nextPage();
    		}
    		else if(PAGINATION_LAST.equals(page))
    		{
    			reportList.setPage(reportList.getLastLinkedPage());
    		}
    		else if(PAGINATION_PREV.equals(page) && !reportList.isFirstPage())
    		{
    			reportList.previousPage();
    		}
    		else if(PAGINATION_FIRST.equals(page))
    		{
    			reportList.setPage(reportList.getFirstLinkedPage());
    		}
    	}	// export == null
    	else if (export.booleanValue())
    	{
    		// they clicked Export as CSV
    		//get the headers and the report list from the http session
    		requirements = (List<String>) session.getAttribute(SESSION_REQUIREMENTS_ATTRIBUTE);
    		expiryOffset = (Integer) session.getAttribute(SESSION_EXPIRY_OFFSET_ATTRIBUTE);
    		propHeaders = (List<String>) session.getAttribute(SESSION_REPORT_PROP_HEADERS_ATTRIBUTE);
    		criteriaHeaders = (List<Object>) session.getAttribute(SESSION_REPORT_CRIT_HEADERS_ATTRIBUTE);
    		reportList = (PagedListHolder) session.getAttribute(SESSION_REPORT_LIST_ATTRIBUTE);
    		
    		try
    	    {
    	        definition = certService.getCertificateDefinition(certId);
    	        
    	        //prepare the http response header
    	    	DateFormat filenameDateFormat = new SimpleDateFormat(CSV_FILE_NAME_FORMAT);
    	    	String today = filenameDateFormat.format(new Date());
    	    	String report = messages.getString("report.export.fname");
    	    	String defName = definition.getName();
    	    	if (logIfNull(defName,"certificate name is null: "+ certId))
    	    	{
    	    		errors.add(messages.getString("report.export.error"));
    	    		return reportViewError(model, errors, requirements, propHeaders, criteriaHeaders, reportList);
    	    	}
    	    	defName = defName.replaceAll("[^a-zA-Z0-9]+","-");
    	    	
    	    	response.setContentType(CSV_MIME_TYPE);
    	    	response.addHeader("Content-Disposition", "attachment; filename = " + defName + "_" + report + "_" + today +".csv");
    	    	response.setHeader("Cache-Control", "");
    	    	response.setHeader("Pragma", "");
    	    	
    	    	//fill in the csv's header
    	    	StringBuilder contents = new StringBuilder();
    	    	appendItem(contents, messages.getString("report.table.header.name"), false);
    	    	appendItem(contents, messages.getString("report.table.header.userid"), false);
    	    	appendItem(contents, messages.getString("report.table.header.role"), false);
    	    	if (canShowUserProps)
    	    	{
    	    		if (logIfNull(propHeaders, "propHeaders is null"))
	    			{
        	    		errors.add(messages.getString("report.export.error"));
        	    		return reportViewError(model, errors, requirements, propHeaders, criteriaHeaders, reportList);
	    			}
    	    		Iterator<String> itPropHeaders = propHeaders.iterator();
    	    		while (itPropHeaders.hasNext())
    	    		{
    	    			appendItem(contents, itPropHeaders.next(), false);
    	    		}
    	    	}
    	    	
    	    	appendItem(contents, messages.getString("report.table.header.issuedate"), false);
    	    	
    	    	Iterator<Object> itHeaders = criteriaHeaders.iterator();
    	    	while (itHeaders.hasNext())
    	    	{
    	    		appendItem(contents, (String) itHeaders.next(), false);
    	    	}
    	    	
    	    	appendItem(contents, messages.getString("report.table.header.awarded"), true);
    	    	
    	    	
    	    	// gets the original list of ReportRows
    	    	List<ReportRow> table = null;
    	    	try
    	    	{
    	    		table = (List<ReportRow>) reportList.getSource();
    	    	}
    	    	catch( Exception ex )
    	    	{
    	    		logger.error( "Couldn't cast reportList for the reportView. certId: " + certId);
    	    		errors.add(messages.getString("report.export.error"));
    	    		return reportViewError(model, errors, requirements, propHeaders, criteriaHeaders, reportList);
    	    	}
    	    	
    	    	//fill the rest of the csv
    	    	Iterator<ReportRow> itTable = table.iterator();
    	    	while (itTable.hasNext())
    	    	{
	    			//represents a line in the table
    	    		ReportRow row = itTable.next();
    	    		appendItem(contents, row.getName(), false);
    	    		appendItem(contents, row.getUserId(), false);
    	    		appendItem(contents, row.getRole(), false);
    	    		if (canShowUserProps)
    	    		{
    	    			List<String> extraProps = row.getExtraProps();
    	    			if (logIfNull(extraProps, "Extra props is null for certId: " + certId))
	    				{
    	    	    		errors.add(messages.getString("report.export.error"));
    	    	    		return reportViewError(model, errors, requirements, propHeaders, criteriaHeaders, reportList);
	    				}
	    	    		Iterator<String> itExtraProps = extraProps.iterator();
	    	    		while (itExtraProps.hasNext())
	    	    		{
	    	    			appendItem(contents, itExtraProps.next(), false);
	    	    		}
    	    		}
    	    		
    	    		appendItem(contents, row.getIssueDate(), false);
    	    		
    	    		Iterator<String> itCriterionCells = row.getCriterionCells().iterator();
    	    		while (itCriterionCells.hasNext())
    	    		{
    	    			appendItem(contents, itCriterionCells.next(), false);
    	    		}
    	    		
    	    		appendItem(contents, row.getAwarded(), true);
    	    	}
    	    	
    	    	
    	    	//send contents
    	    	String data = contents.toString();
    	    	
    	    	OutputStream out = response.getOutputStream();
    	    	
    	    	out.write(data.getBytes());
    	    	
    	    	out.flush();
    	    	out.close();
    	    	
    	    	
    	    	//we're not updating their view
    	    	return null;
    	    }
    	    catch (IdUnusedException e)
    	    {
    	        //they sent an invalid certId in their http GET;
    	    	/*possible causes: they clicked on View Report after another user deleted the certificate definition,
    	    	or they attempted to do evil with a random http GET.
    	    	We don't care*/
    	    	logger.error("unused certificate id passed to report's csv export: "+ certId);
	    		errors.add(messages.getString("report.export.error"));
	    		return reportViewError(model, errors, requirements, propHeaders, criteriaHeaders, reportList);
    	    }
    	}
    	else
    	{
    		//should never happen
    		logger.warn("hit reportView.form with export=false. Should never happen");
    		return null;
    	}
    	
    	//handle plurals when appropriate
    	String strExpiryOffset = null;
    	if (expiryOffset != null && expiryOffset == 1)
    	{
    		strExpiryOffset = "1 " + messages.getString("report.expiry.offset.month"); 
    	}
    	else if (expiryOffset != null)
    	{
    		strExpiryOffset = expiryOffset + " " + messages.getString("report.expiry.offset.months");
    	}
    	
    	//push the navigator and the headers to the http session
    	session.setAttribute(SESSION_REQUIREMENTS_ATTRIBUTE, requirements);
    	session.setAttribute(SESSION_EXPIRY_OFFSET_ATTRIBUTE, expiryOffset);
    	session.setAttribute(SESSION_REPORT_PROP_HEADERS_ATTRIBUTE, propHeaders);
    	session.setAttribute(SESSION_REPORT_CRIT_HEADERS_ATTRIBUTE, criteriaHeaders);
    	session.setAttribute(SESSION_REPORT_LIST_ATTRIBUTE, reportList);
    	
    	//populate the model as necessary
    	model.put(MODEL_KEY_ERRORS_ATTRIBUTE, errors);
    	model.put(MODEL_KEY_REQUIREMENTS_ATTRIBUTE, requirements);
    	model.put(MODEL_KEY_EXPIRY_OFFSET_ATTRIBUTE, strExpiryOffset);
    	model.put(MODEL_KEY_USER_PROP_HEADERS_ATTRIBUTE, propHeaders);
    	model.put(MODEL_KEY_CRIT_HEADERS_ATTRIBUTE,criteriaHeaders);
    	model.put(MODEL_KEY_REPORT_LIST_ATTRIBUTE, reportList);
    	model.put(MODEL_KEY_PAGE_SIZE_LIST, PAGE_SIZE_LIST);
        model.put(MODEL_KEY_PAGE_NO, reportList.getPage());
        model.put(MODEL_KEY_PAGE_SIZE, reportList.getPageSize());
        model.put(MODEL_KEY_FIRST_ELEMENT, (reportList.getFirstElementOnPage()+1));
        model.put(MODEL_KEY_LAST_ELEMENT, (reportList.getLastElementOnPage()+1));
    	
        //send the model to the jsp
    	ModelAndView mav = new ModelAndView("reportView", model);
		return mav;
	}
    
    
    /**
     * If an error occurs that prevents us from generating the report view, 
     * this will give us a return value such that the user will see the relevant error
     * 
     * @author bbailla2
     * 
     * @param model
     * @param errors
     * @param requirements
     * @param propHeaders
     * @param criteriaHeaders
     * @param reportList
     * @return
     */
    private ModelAndView reportViewError(Map<String, Object> model, List<String> errors, List<String> requirements, List<String> propHeaders, List<Object> criteriaHeaders, PagedListHolder reportList)
    {
    	//Include what we can, but ultimately ensure that we can display the errors to the user
    	if (model.get(MODEL_KEY_ERRORS_ATTRIBUTE) == null)
    	{
    		model.put(MODEL_KEY_ERRORS_ATTRIBUTE, errors);
    	}
    	if (model.get(MODEL_KEY_REQUIREMENTS_ATTRIBUTE) == null)
    	{
    		model.put(MODEL_KEY_REQUIREMENTS_ATTRIBUTE, requirements);
    	}
    	if (model.get(MODEL_KEY_USER_PROP_HEADERS_ATTRIBUTE) == null)
    	{
    		model.put(MODEL_KEY_USER_PROP_HEADERS_ATTRIBUTE, propHeaders);
    	}
    	if (model.get(MODEL_KEY_CRIT_HEADERS_ATTRIBUTE) == null)
    	{
    		model.put(MODEL_KEY_CRIT_HEADERS_ATTRIBUTE, criteriaHeaders);
    	}
    	
    	//will break if reportList is null, so we need to be careful with this
    	PagedListHolder plh = (PagedListHolder) model.get(MODEL_KEY_REPORT_LIST_ATTRIBUTE);
    	if (plh == null)
    	{
    		if (reportList == null)
    		{
	    		reportList = new PagedListHolder(new ArrayList<String>());
    		}
    		plh=reportList;
    		model.put(MODEL_KEY_REPORT_LIST_ATTRIBUTE, reportList);
    	}
    	
    	if (model.get(MODEL_KEY_PAGE_SIZE_LIST) == null)
    	{
    		model.put(MODEL_KEY_PAGE_SIZE_LIST, PAGE_SIZE_LIST);
    	}
    	if (model.get(MODEL_KEY_PAGE_NO) == null)
    	{
    		model.put(MODEL_KEY_PAGE_NO, plh.getPage());
    	}
    	if (model.get(MODEL_KEY_PAGE_SIZE) == null)
    	{
    		model.put(MODEL_KEY_PAGE_SIZE, plh.getPageSize());
    	}
    	if (model.get(MODEL_KEY_FIRST_ELEMENT) == null)
    	{
    		model.put(MODEL_KEY_FIRST_ELEMENT, plh.getFirstElementOnPage()+1);
    	}
    	if (model.get(MODEL_KEY_LAST_ELEMENT) == null)
    	{
    		model.put(MODEL_KEY_LAST_ELEMENT, plh.getLastElementOnPage()+1);
    	}
    	return new ModelAndView("reportView", model);
    }
    
    
    /**
     * if the specified object is null, the specified message gets logged at the specified logging level
     * 
     * @author bbailla2
     * 
     * @param obj
     * @param message
     * @param level
     * @return
     */
    private boolean logIfNull(Object obj, String message, String level)
    {
    	if (obj==null)
    	{
    		if (level == null)
    		{
    			logger.error(message);
    		}
    		else if (LEVEL_WARN.equals(level))
    		{
    			logger.warn(message);
    		}
    		return true;
    	}
    	return false;
    }
    
    /**
     * if the specified object is null, the specified message gets logged at the error logging level
     * 
     * @author bbailla2
     * 
     * @param obj
     * @param message
     * @return
     */
    private boolean logIfNull(Object obj, String message)
    {
    	return logIfNull(obj, message, null);
    }
    
    /**
     * Sets the name field on the row in an appropriate format ('lastname, firstname' unless a name is missing)
     * 
     * @author bbailla2
     * 
     * @param row
     * @param firstName
     * @param lastName
     */
    private void setNameFieldForReportRow(ReportRow row, String firstName, String lastName)
    {
    	if (lastName==null)
		{
			lastName = "";
		}
    	
		if (firstName==null)
		{
			firstName = "";
		}
		
		//if one name is missing, use the opposite
		if ("".equals(lastName))
		{
			//use the opposite name or empty string if firstName is missing (both cases are covered here)
			row.setName(firstName);
		}
		else if ("".equals(firstName))
		{
			row.setName(lastName);
		}
		else
		{
			//both names present
			row.setName(lastName+", "+firstName);	    					
		}
    }
    
    /**
     * Appends item to a StringBuilder for csv format by surrounding them in double quotes, and separating lines when appropriate
     * 
     * @author bbailla2
     * 
     * @param stringBuilder the StringBuilder we are appending to
     * @param item the item that we are appending to the csv
     * @param eol true if this is the last item in the current line
     */
    private void appendItem(StringBuilder stringBuilder, String item, boolean eol)
    {
    	stringBuilder.append('\"');
    	if (item!=null)
    	{
    		stringBuilder.append(item);
    	}
    	stringBuilder.append('\"');
    	if (!eol)
    	{
    		stringBuilder.append(',');
    	}
    	else
    	{
    		stringBuilder.append('\n');
    	}
    }
    
    
    /**
     * @author bbailla2
     */
    public class ReportRow
    {
    	private String name = "";
    	private String userId = "";
    	private String role = "";
    	private List<String> extraProps = new ArrayList<String>();
    	private String issueDate = "";
    	private List<String> criterionCells = new ArrayList<String>();
    	private String awarded = "";
    	
    	public void setName(String name)
    	{
    		this.name=name;
    	}
    	
    	public String getName()
    	{
    		return name;
    	}
    	
    	public void setUserId(String userId)
    	{
    		this.userId=userId;
    	}
    	
    	public String getUserId()
    	{
    		return userId;
    	}
    	
    	public void setRole(String role)
    	{
    		this.role = role;
    	}
    	
    	public String getRole()
    	{
    		return role;
    	}
    	
    	public void setExtraProps(List<String> extraProps)
    	{
    		this.extraProps = extraProps;
    	}
    	
    	public List<String> getExtraProps()
    	{
    		return extraProps;
    	}
    	
    	public void setIssueDate(String issueDate)
    	{
    		this.issueDate = issueDate;
    	}
    	
    	public String getIssueDate()
    	{
    		return issueDate;
    	}
    	
    	public void setCriterionCells (List<String> criterionCells)
    	{
    		this.criterionCells = criterionCells;
    	}
    	
    	public List<String> getCriterionCells()
    	{
    		return criterionCells;
    	}
    	
    	public void setAwarded(String awarded)
    	{
    		this.awarded = awarded;
    	}
    	
    	public String getAwarded()
    	{
    		return awarded;
    	}
    }
}
