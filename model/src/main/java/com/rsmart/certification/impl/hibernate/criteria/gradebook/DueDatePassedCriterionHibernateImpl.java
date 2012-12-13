package com.rsmart.certification.impl.hibernate.criteria.gradebook;

import com.rsmart.certification.impl.hibernate.criteria.AbstractCriterionHibernateImpl;

import java.util.ArrayList;
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
	
	//OWLTODO: Implement
	@Override
	public List<String> getReportData()
	{
		return null;
	}
}
