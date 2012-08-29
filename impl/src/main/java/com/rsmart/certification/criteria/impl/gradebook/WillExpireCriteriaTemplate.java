package com.rsmart.certification.criteria.impl.gradebook;

import com.rsmart.certification.api.criteria.Criterion;
import com.rsmart.certification.impl.hibernate.criteria.gradebook.GreaterThanScoreCriterionHibernateImpl;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.util.ResourceLoader;

public class WillExpireCriteriaTemplate
    extends GradebookItemCriteriaTemplate
{
    //TODO: ExpiryOffsetTemplateVariable expiryOffsetVariable = null;

    private final String EXPRESSION_KEY = "greater.than.score.criteria.expression";
    
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
	
	    //TODO: expiryOffsetVariable =  new ExpiryOffsetTemplateVariable("expiry.offset", factory);
	
	    //TODO:addVariable(expiryOffsetVariable);
	}
	
	public String getId()
	{
	    return GreaterThanScoreCriteriaTemplate.class.getName();
	}
	
	public String getExpression()
	{
	    return getExpression(null);
	}
	
	public String getExpression (Criterion criterion)
	{
		if (criterion == null)
		{
			return rl.getFormattedMessage(EXPRESSION_KEY, new String[]{});
		}
		
	    String
	        vars[] = new String[2];
	
	    
	    GreaterThanScoreCriterionHibernateImpl
	       gischi = (GreaterThanScoreCriterionHibernateImpl)criterion;
	
	    vars[0] = gischi.getItemName();
	    vars[1] = gischi.getScore();
	
	    return rl.getFormattedMessage(GreaterThanScoreCriteriaTemplate.class.getName(), vars);
	}

}
