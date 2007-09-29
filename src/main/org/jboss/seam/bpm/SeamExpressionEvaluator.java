package org.jboss.seam.bpm;


import java.lang.reflect.Method;

import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

import org.jboss.seam.el.EL;
import org.jboss.seam.el.SeamFunctionMapper;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.Expression;
import org.jbpm.jpdl.el.ExpressionEvaluator;
import org.jbpm.jpdl.el.FunctionMapper;
import org.jbpm.jpdl.el.VariableResolver;

/**
 * Plugs the JBoss EL expression language and Seam
 * EL resolvers into jBPM. Note that this current 
 * implementation does not allow jBPM to see stuff
 * defined only by the JSF ELResolvers.
 * 
 * @author Gavin King
 *
 */
public class SeamExpressionEvaluator 
    extends ExpressionEvaluator
{

    @Override
    public Object evaluate(String expression, Class returnType, final VariableResolver resolver, FunctionMapper mapper)
        throws ELException
    {
        return createExpression(expression, returnType, mapper).evaluate(resolver);
    }
    
    @Override
    public Expression parseExpression(final String expression, final Class returnType, FunctionMapper mapper)
        throws ELException
    {
        return createExpression(expression, returnType, mapper);
    }
    
    private static Expression createExpression(final String expression, final Class returnType, final FunctionMapper mapper)
    {
        return 
            new Expression() {
                private MethodExpression me;
                private ValueExpression ve; 
                
                private void initMethodExpression() {
                    me = EL.EXPRESSION_FACTORY.createMethodExpression(EL.EL_CONTEXT, expression, returnType, new Class[0]);
                }
                
                private void initValueExpression() {
                    ve = EL.EXPRESSION_FACTORY.createValueExpression(EL.EL_CONTEXT, expression, returnType);
                }
                
                @Override
                public Object evaluate(VariableResolver resolver) throws ELException
                {
                    try {
                        try {
                            if (me==null && ve==null) {
                                initMethodExpression();
                            }
                            if (me!=null && ve==null) {
                                return me.invoke(createELContext(resolver, mapper), new Object[0]);
                            }
                        } catch (javax.el.ELException e) {                                    
                            if (ve==null) {
                                initValueExpression();
                            }
                            if (ve!=null) {
                                return ve.getValue(createELContext(resolver, mapper));
                            }
                        }
                        throw new ELException();
                    } catch (javax.el.ELException e) {
                        throw new ELException(e);
                    }
                }
            };
    }
   
    private static javax.el.FunctionMapper decorateFunctionMapper(final FunctionMapper functionMapper)
    {
        return new SeamFunctionMapper( new javax.el.FunctionMapper() {
                @Override
                public Method resolveFunction(String prefix, String localName)
                {
                    return functionMapper.resolveFunction(prefix, localName);
                }
            });
    }
    
    private static ELContext createELContext(VariableResolver resolver, FunctionMapper functionMapper)
    {
        CompositeELResolver composite = new CompositeELResolver();
        composite.add(EL.EL_RESOLVER);
        composite.add( new JbpmELResolver(resolver) );
        return EL.createELContext(composite, decorateFunctionMapper(functionMapper));
    }
    
}
