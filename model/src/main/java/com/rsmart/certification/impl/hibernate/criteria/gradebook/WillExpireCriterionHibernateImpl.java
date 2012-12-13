package com.rsmart.certification.impl.hibernate.criteria.gradebook;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WillExpireCriterionHibernateImpl
    extends GradebookItemCriterionHibernateImpl
{
	private final String MESSAGE_REPORT_TABLE_HEADER_EXPIRE = "report.table.header.expire";
	
    public String getExpiryOffset()
    {
        return getVariableBindings().get("expiry.offset");
    }

    public void setExpiryOffset(String expiryOffset)
    {
        getVariableBindings().put("expiry.offset", expiryOffset);
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
			
			final DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(issueDate);
			cal.add(Calendar.MONTH, expiryOffset);
			Date expiryDate = cal.getTime();
			datum = dateFormat.format(expiryDate);
		}
		
		reportData.add(datum);
		return reportData;
	}
	
}
