package com.rsmart.certification.impl.hibernate.criteria.gradebook;

import java.util.ArrayList;
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

	//OWLTODO: Implement
	@Override
	public List<String> getReportData() 
	{
		// TODO Auto-generated method stub
		return null;
	}
}
