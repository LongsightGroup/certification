package com.rsmart.certification.criteria.impl.gradebook;

import com.rsmart.certification.api.criteria.Criterion;
import com.rsmart.certification.impl.ExpiryOffsetTemplateVariable;

import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.util.ResourceLoader;


public class WillExpireCriteriaTemplate
    extends GradebookItemCriteriaTemplate
{
    ExpiryOffsetTemplateVariable expiryOffsetVariable = null;

    private final String EXPRESSION_KEY = "will.expire.criteria.expression";
    
	public WillExpireCriteriaTemplate(final GradebookCriteriaFactory factory)
	{
	    super(factory,
	            null,
	            new AssignmentLabeler()
	            {
	                public String getLabel(Assignment assignment)
	                {
	                    StringBuffer
	                        assnLabel = new StringBuffer();
	                    ResourceLoader
	                        rl = factory.getResourceLoader();
	
	                    String
	                        pointsStr = rl.getFormattedMessage("points", new String[] { assignment.getPoints().toString() });
	
	                    assnLabel.append(assignment.getName()).append(" (").append(pointsStr).append(')');
	
	                    return assnLabel.toString();
	                }
	            });
	
	    expiryOffsetVariable =  new ExpiryOffsetTemplateVariable("expiry.offset", factory);
	
	    addVariable(expiryOffsetVariable);
	}
	
	public String getId()
	{
	    return WillExpireCriteriaTemplate.class.getName();
	}
	
	public String getExpression()
	{
	    return getExpression(null);
	}
	
	public String getExpression (Criterion criterion)
	{
                if (criterion == null)
                {
                        return getResourceLoader().getFormattedMessage(EXPRESSION_KEY, new String[]{});
                }
                else
                {
                        return getResourceLoader().getFormattedMessage(WillExpireCriteriaTemplate.class.getName(), new String[]{"Unimplemented","Unimplemented"});
                }
	}

}
