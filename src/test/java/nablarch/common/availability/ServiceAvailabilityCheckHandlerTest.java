package nablarch.common.availability;

import java.util.ArrayList;
import java.util.List;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import nablarch.core.ThreadContext;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import nablarch.fw.InboundHandleable;
import nablarch.fw.OutboundHandleable;
import nablarch.fw.invoker.BasicPipelineListBuilder;
import nablarch.fw.invoker.PipelineInvoker;
import nablarch.fw.results.ServiceUnavailable;

import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceAvailabilityCheckHandlerTest {

    private ServiceAvailabilityCheckHandler target = new ServiceAvailabilityCheckHandler();

    @Mocked
    private ServiceAvailability serviceAvailability;

    @Mocked
    private Handler<?, ?> nextHandler;

    @Mocked
    InboundOutboundHandler nextInoutHandler;
    
    private ExecutionContext createContext() {

        ExecutionContext context = new ExecutionContext();
        target.setServiceAvailability(serviceAvailability);
        context.addHandler(target);
        context.addHandler(nextHandler);

        return context;
    }

    private PipelineInvoker createInvoker(final ExecutionContext context) {
        PipelineInvoker invoker = new PipelineInvoker();
        BasicPipelineListBuilder builder = new BasicPipelineListBuilder();
        
        List<Object> handlerList = new ArrayList<Object>();
        ServiceAvailabilityCheckHandler target = new ServiceAvailabilityCheckHandler();
        target.setServiceAvailability(serviceAvailability);
        handlerList.add(target);
        handlerList.add(nextInoutHandler);

        builder.setHandlerList(handlerList);
        invoker.setHandlerListBuilder(builder);
        return invoker;
    }

    @Test
    public void testHandle() {

        {
            // サービスが提供されている場合
            final ExecutionContext context = createContext();
            ThreadContext.setRequestId("example");
    
            new NonStrictExpectations() {{
                serviceAvailability.isAvailable("example");
                result = true;
            }};
            
            context.handleNext(null);
            
            new Verifications() {{
                nextHandler.handle(null, context);
                times = 1;
            }};
        }
        {
            // サービスが提供されていない場合
            final ExecutionContext context = createContext();
            ThreadContext.setRequestId("example");
            new NonStrictExpectations() {{
                serviceAvailability.isAvailable("example");
                result = false;
            }};
    
            try {
                context.handleNext(null);
                fail("例外発生するはず");
            } catch (ServiceUnavailable e) {
                // OK
            }
            
            new Verifications() {{
                nextInoutHandler.handleInbound(context);
                times = 0;
            }};
        }

        {
            // サービスが提供されている場合(内部リクエストIDを使う場合
            final ExecutionContext context = createContext();
            target.setUsesInternalRequestId(true);
            ThreadContext.setInternalRequestId("internal");
    
            new NonStrictExpectations() {{
                serviceAvailability.isAvailable("internal");
                result = true;
            }};
            
            context.handleNext(null);
            
            new Verifications() {{
                nextHandler.handle(null, context);
                times = 1;
            }};
        }
        {
            // サービスが提供されていない場合

            final ExecutionContext context = createContext();
            target.setUsesInternalRequestId(true);
            ThreadContext.setInternalRequestId("internal");
            new NonStrictExpectations() {{
                serviceAvailability.isAvailable("internal");
                result = false;
            }};
    
            try {
                context.handleNext(null);
                fail("例外発生するはず");
            } catch (ServiceUnavailable e) {
                // OK
            }
            
            new Verifications() {{
                nextInoutHandler.handleInbound(context);
                times = 0;
            }};
        }
    }

    @Test
    public void testHandleInbound() {
        {
            final ExecutionContext context = createContext();
            PipelineInvoker invoker = createInvoker(context);
            ThreadContext.setRequestId("example");
            // サービスが提供されている場合
            new NonStrictExpectations() {{
                serviceAvailability.isAvailable("example");
                result = true;
            }};
    
            invoker.invokeInbound(context);
            
            new Verifications() {{
                nextInoutHandler.handleInbound(context);
                times = 1;
            }};
        }
        {
            final ExecutionContext context = createContext();
            // サービスが提供されていない場合
            new NonStrictExpectations() {{
                serviceAvailability.isAvailable("example");
                result = false;
            }};
    
            try {
                context.handleNext(null);
                fail("例外発生するはず");
            } catch (ServiceUnavailable e) {
                // OK
            }
            
            new Verifications() {{
                nextInoutHandler.handleInbound(context);
                times = 0;
            }};
        }
    }

    private static interface InboundOutboundHandler extends InboundHandleable, OutboundHandleable {
        
    }
}
