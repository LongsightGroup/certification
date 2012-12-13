package com.rsmart.certification.impl.hibernate.criteria.gradebook;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: duffy
 * Date: Jul 5, 2011
 * Time: 9:59:47 AM
 */
public class DueDatePassedCriterionHibernateImpl
    extends GradebookItemCriterionHibernateImpl
{
	private final String MESSAGE_REPORT_TABLE_HEADER_DUEDATE = "report.table.header.duedate";
	
	@Override
	public List<String> getReportHeaders()
	{
		List<String> reportHeaders = new ArrayList<String>();
		
		String gradebookItem = getItemName();
		String header = getCertificateService().getFormattedMessage(MESSAGE_REPORT_TABLE_HEADER_DUEDATE, new Object[]{gradebookItem});
		
		reportHeaders.add(header);
		return reportHeaders;
	}
	
	
	@Override
	public List<String> getReportData(String userId, String siteId, Date issueDate)
	{
		List<String> reportData = new ArrayList<String>();
		
		DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		String datum = dateFormat.format(getDueDate());
		
		reportData.add(datum);
		return reportData;
	}
}
