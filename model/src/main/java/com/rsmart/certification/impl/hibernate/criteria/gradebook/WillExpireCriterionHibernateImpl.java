package com.rsmart.certification.impl.hibernate.criteria.gradebook;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.rsmart.certification.api.criteria.CriteriaFactory;
import com.rsmart.certification.api.criteria.UnknownCriterionTypeException;

public class WillExpireCriterionHibernateImpl extends GradebookItemCriterionHibernateImpl
{
	private final String MESSAGE_REPORT_TABLE_HEADER_EXPIRE = "report.table.header.expire";
	private final DateFormat REPORT_DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy");
	
    public String getExpiryOffset()
    {
        return getVariableBindings().get(CriteriaFactory.KEY_EXPIRY_OFFSET);
    }

    public void setExpiryOffset(String expiryOffset)
    {
        getVariableBindings().put(CriteriaFactory.KEY_EXPIRY_OFFSET, expiryOffset);
    }

	@Override
	public List<String> getReportHeaders() 
	{
		List<String> reportHeaders = new ArrayList<String>();
		
		String header = getCertificateService().getString(MESSAGE_REPORT_TABLE_HEADER_EXPIRE);
		
		reportHeaders.add(header);
		return reportHeaders;
	}

	/**
	 * Must supply the issue date before calling this method
	 */
	@Override
	public List<String> getReportData(String userId, String siteId, Date issueDate) 
	{
		List<String> reportData = new ArrayList<String>();
	
		String datum = "";
		
		if (issueDate != null)
		{
			Integer expiryOffset = new Integer(getExpiryOffset());
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(issueDate);
			cal.add(Calendar.MONTH, expiryOffset);
			Date expiryDate = cal.getTime();
			datum = REPORT_DATE_FORMAT.format(expiryDate);
		}
		
		reportData.add(datum);
		return reportData;
	}
	
	@Override
	public Date getDateMet(String userId, String siteId)
	{
		//For this criterion, date met is undefined
		return null;
	}
	
	@Override
	public String getProgress(String userId, String siteId)
	{
		//For this criterion, progress is undefined
		return "";
	}
}
